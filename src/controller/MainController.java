package controller;

import com.google.gson.*;
import java.io.*;
import java.util.*;
import javafx.scene.control.ChoiceDialog;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.GameModel;
import model.GameScene;
import model.GameState;
import model.InventoryItem;
import model.SceneLoader;
import model.ItemType;
import view.*;

public class MainController {
    private final Stage stage;
    private final GameModel model;
    private final BorderPane rootPane;
    private final SceneLoader loader;
    private String lastHealthAppliedSceneId = null;
    private final Set<String> addItemProcessedScenes = new HashSet<>();
    private GameScene currentScene;

    public MainController(Stage stage) {
        this.stage = stage;
        this.model = new GameModel();
        this.rootPane = new BorderPane();
        this.loader = new SceneLoader("src/data/scenes.json"); 
    }

    public void startApp() {
        Scene scene = new Scene(rootPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Zombie Choice Game");
        updateView(); 
        stage.show();
    }

    public void updateView() {
        rootPane.setTop(null);
        rootPane.setCenter(null);

        switch (model.getCurrentState()) {
            case TITLE -> showTitleView();
            case INSTRUCTIONS -> showInstructionsView();
            case FIRST_CHOICE -> showFirstChoiceView();  
            default -> System.out.println("Unknown state.");
        }
    }

    private void showTitleView() {
        resetInventoryToDefault();
        model.reloadInventory();
        addItemProcessedScenes.clear();

        TitleView titleView = new TitleView();
        titleView.applyTheme(model.isDarkMode());

        titleView.startButton.setOnAction(e -> {
                model.clearInventory();
                resetInventoryToDefault();
                showInventoryChoice();
        });

        titleView.instructionsButton.setOnAction(e -> {
            model.setCurrentState(GameState.INSTRUCTIONS);
            updateView();
        });

        titleView.topBar.toggleButton.setOnAction(e -> {
            model.toggleDarkMode();
            titleView.applyTheme(model.isDarkMode());
        });
        rootPane.setCenter(titleView);
    }

    private void showInstructionsView() {
        InstructionsView view = new InstructionsView();
        view.applyTheme(model.isDarkMode());

        view.topBar.toggleButton.setOnAction(e -> {
            model.toggleDarkMode();
            view.applyTheme(model.isDarkMode());
        });

        view.backButton.setOnAction(e -> {
            model.setCurrentState(GameState.TITLE);
            updateView();
        });

        rootPane.setCenter(view);
    }

    private void showFirstChoiceView() {
        // Load the first scene using its ID
        GameScene scene = loader.getSceneById("start");

        if (scene != null) {
            showSceneView(scene);
        } else {
            // If scene can't be loaded, fallback to ending state
            model.setCurrentState(GameState.ENDING);
            updateView();
        }
    }

    private void showSceneView(GameScene scene) {
        this.currentScene = scene;

        // Only apply healthChange if entering a new scene
        if (!scene.getId().equals(lastHealthAppliedSceneId)) {
            int before = model.getHealth();
            // Skip applying the JSON healthChange if this is a fight result scene.
            if (!scene.getId().startsWith("fight_result")) {
                 model.subtractHealth(scene.getHealthChange());
                 System.out.println("[DEBUG] Applied scene healthChange: " + scene.getHealthChange() +
                     " | Health before: " + before + ", after: " + model.getHealth());
            } else {
                 System.out.println("[DEBUG] Skipped JSON healthChange for fight result scene: " + scene.getId());
            }
            lastHealthAppliedSceneId = scene.getId();
        }

        // Only process addItem if not already processed for this scene
        if (scene.hasAddItem() && !addItemProcessedScenes.contains(scene.getId())) {
            InventoryItem item = scene.getAddItem();
            boolean added = model.addItem(item);
            if (!added && item.isWeapon()) {
                writeTempWeaponJson(item);
                List<InventoryItem> weapons = model.getInventory().get(ItemType.WEAPON);
                List<String> weaponNames = new ArrayList<>();
                for (InventoryItem w : weapons) weaponNames.add(w.getName());
                showWeaponRemovalDialog(weaponNames, item, scene.getId());
                return; 
            } else {
                System.out.println("[DEBUG] Added item to inventory: " + item.getName() + " | Success: " + added);
                if (added && item.isWeapon()) {
                    addWeaponToJson(item);
                }
            }
            addItemProcessedScenes.add(scene.getId());
        }

        ChoiceScreenView view = new ChoiceScreenView(
            model.getHealth(),
            scene.getPrompt(),
            scene.getChoices(),
            model.isDarkMode(),
            model.getInventory(),
            choice -> {
                // Check if the current scene is the inventory choice screen.
                if ("inventory_choice".equals(currentScene.getId())) {
                    // Set up inventory based on the choice label.
                    applyInventoryChoice(choice.getLabel());
                    // Load next scene; here we assume "start" is the first gameplay scene.
                    showSceneView(loader.getSceneById("start"));
                    return;
                }

                // Delegate fight handling to a single helper if the player chose “Fight”
                if (scene.getThreatLevel() > -1 && "Fight".equalsIgnoreCase(choice.getLabel())) {
                    int threat = scene.getThreatLevel();
                    int ded = computeDurabilityDecrease(threat);
                    int winPenalty = computeWinHealthPenalty(threat);
                    int losePenalty = computeLoseHealthPenalty(threat);
                    handleFight(scene, ded, winPenalty, losePenalty);
                    return;
                } else {
                    GameScene next = loader.getSceneById(choice.getNextId());
                    if (next != null) {
                        showSceneView(next);
                    } else {
                        model.setCurrentState(GameState.ENDING);
                        updateView();
                    }
                }
            },
            () -> {
                model.toggleDarkMode();
                showSceneView(currentScene);
            },
            () -> { // NEW: onReset
                javafx.scene.control.Alert confirm =
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Reset Game");
                confirm.setHeaderText("Are you sure you want to return to the title screen?");
                confirm.setContentText("Any current progress will be lost.");
                Optional<javafx.scene.control.ButtonType> res = confirm.showAndWait();
                if (res.isPresent() && res.get() == javafx.scene.control.ButtonType.OK) {
                    // Clear transient state
                    lastHealthAppliedSceneId = null;
                    addItemProcessedScenes.clear();
                    resetInventoryToDefault(); // optional, if you reset items on title
                    model.resetHealth();       // optional, if you have such a helper
                    // Go back to title
                    model.setCurrentState(GameState.TITLE);
                    updateView(); // shows TitleView
                }
            },
            item -> {
                System.out.println("[DEBUG] Attempting to consume item: " + item.getName() + ", type: " + item.getType());
                boolean consumed = model.consumeItem(item);
                if (consumed) {
                    System.out.println("[DEBUG] Consumed item: " + item.getName() +
                        " | Health restored: " + item.getHealthRestore() +
                        " | Health after: " + model.getHealth());
                    showSceneView(scene);
                } else {
                    System.out.println("[DEBUG] Failed to consume item: " + item.getName());
                }
            }
        );
        rootPane.setCenter(view);

        // Keep your existing window resize
        Platform.runLater(() -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
            stage.setWidth(1100);
            stage.setHeight(700);
        });
    }

    // Helper to write new weapon to a temp JSON file
    private void writeTempWeaponJson(InventoryItem item) {
        try (FileWriter writer = new FileWriter("src/data/new_weapon.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", item.getName());
            obj.addProperty("type", item.getType().toString());
            obj.addProperty("durability", item.getDurability());
            obj.addProperty("power", item.getPower());
            new Gson().toJson(obj, writer);
        } catch (IOException e) {
            System.err.println("Failed to write temp weapon JSON: " + e.getMessage());
        }
    }

    // Helper to remove a weapon from inventory.json
    private void removeWeaponFromJson(String weaponName) {
        try {
            File file = new File("src/data/inventory.json");
            JsonArray arr = JsonParser.parseReader(new FileReader(file)).getAsJsonArray();
            JsonArray newArr = new JsonArray();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                if (!obj.has("name") || !weaponName.equals(obj.get("name").getAsString())) {
                    newArr.add(obj);
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(newArr, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to remove weapon from JSON: " + e.getMessage());
        }
    }

    // Helper to add a weapon to inventory.json
    private void addWeaponToJson(InventoryItem item) {
        try {
            File file = new File("src/data/inventory.json");
            JsonArray arr = JsonParser.parseReader(new FileReader(file)).getAsJsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("name", item.getName());
            obj.addProperty("type", item.getType().toString());
            obj.addProperty("durability", item.getDurability());
            obj.addProperty("power", item.getPower());
            arr.add(obj);
            try (FileWriter writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(arr, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to add weapon to JSON: " + e.getMessage());
        }
    }

    private void resetInventoryToDefault() {
        try {
            File defaultFile = new File("src/data/default_inventory.json");
            File inventoryFile = new File("src/data/inventory.json");
            try (
                FileReader reader = new FileReader(defaultFile);
                FileWriter writer = new FileWriter(inventoryFile)
            ) {
                int c;
                while ((c = reader.read()) != -1) {
                    writer.write(c);
                }
            }
            System.out.println("[DEBUG] Inventory reset to default.");
        } catch (IOException e) {
            System.err.println("Failed to reset inventory: " + e.getMessage());
        }
    }

    private void showWeaponRemovalDialog(List<String> weaponNames, InventoryItem item, String nextSceneId) {
        Platform.runLater(() -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(weaponNames.get(0), weaponNames);
            dialog.setTitle("Weapon Inventory Full");
            dialog.setHeaderText("Choose a weapon to remove to make space for: " + item.getName());
            dialog.setContentText("Remove:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(selectedName -> {
                // Show confirmation dialog
                javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Removal");
                confirm.setHeaderText("Are you sure you want to remove " + selectedName + "?");
                confirm.setContentText("This cannot be undone.");

                Optional<javafx.scene.control.ButtonType> confirmation = confirm.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == javafx.scene.control.ButtonType.OK) {
                    model.removeItem(selectedName);
                    removeWeaponFromJson(selectedName);

                    model.addItem(item);
                    addWeaponToJson(item);
                    System.out.println("[DEBUG] Removed weapon: " + selectedName +
                        " | Added new item: " + item.getName());                   
                    addItemProcessedScenes.add(nextSceneId);

                    // Go to the intended next scene
                    GameScene nextScene = loader.getSceneById(nextSceneId);
                    if (nextScene != null) {
                        showSceneView(nextScene);
                    } else {
                        updateView();
                    }
                } else {
                    showWeaponRemovalDialog(weaponNames, item, nextSceneId);
                }
            });
        });
    }

    private void handleFight(GameScene scene, int decreaseDurAmount, int subHealthWin, int subHealthLose) {
        int threatLevel = scene.getThreatLevel();
        List<InventoryItem> weapons = model.getInventory().getOrDefault(ItemType.WEAPON, new ArrayList<>());
        // Allow winning when weapon power is equal to threat level.
        List<InventoryItem> winningWeapons = weapons.stream()
            .filter(w -> w.getPower() >= threatLevel && w.getDurability() > 0)
            .sorted(Comparator.comparingInt(InventoryItem::getPower))
            .toList();
        InventoryItem chosenWeapon = winningWeapons.isEmpty() ? null : winningWeapons.get(0);

        if (chosenWeapon != null) {
            int oldDurability = chosenWeapon.getDurability();
            chosenWeapon.decreaseDurability(decreaseDurAmount);
            int newDurability = chosenWeapon.getDurability();
            // Subtract health using negative value to apply penalty
            model.subtractHealth(-subHealthWin);
            System.out.printf("[DEBUG] WIN | used %s, durability decreased from %d to %d | new health: %d%n",
                chosenWeapon.getName(), oldDurability, newDurability, model.getHealth());
            showSceneView(loader.getSceneById("fight_result_win_1"));
        } else {
            // Use fists with default power of 2.
            int fistsPower = 2;
            // If fists power is greater than or equal to threat, the user wins.
            if (fistsPower >= threatLevel) {
                model.subtractHealth(-subHealthWin);
                System.out.printf("[DEBUG] WIN (unarmed) | fists power(%d) >= threat(%d) | new health: %d%n",
                    fistsPower, threatLevel, model.getHealth());
                showSceneView(loader.getSceneById("fight_result_win_1"));
            } else {
                model.subtractHealth(-subHealthLose);
                System.out.printf("[DEBUG] LOSE (unarmed) | fists power(%d) < threat(%d) | new health: %d%n",
                    fistsPower, threatLevel, model.getHealth());
                showSceneView(loader.getSceneById("fight_result_lose_1"));
            }
        }
    }

    private int computeDurabilityDecrease(int threatLevel) {
        // Example: Use 1 for lower threat levels, 2 for higher ones.
        return (threatLevel >= 5) ? 2 : 1;
    }

    private int computeWinHealthPenalty(int threatLevel) {
        // Example: Base penalty of 10 plus additional damage based on threat.
        return 10 + threatLevel;
    }

    private int computeLoseHealthPenalty(int threatLevel) {
        // Example: Base penalty of 25 plus double the threat level.
        return 25 + (2 * threatLevel);
    }

    private void applyInventoryChoice(String choiceLabel) {
        // Clear any existing inventory.
        model.clearInventory();
        
        switch (choiceLabel.toLowerCase()) {
            case "health heavy":
                // Load a health heavy inventory from its JSON file.
                List<InventoryItem> healthItems = loadInventoryFromJson("c:\\Users\\tthom\\Desktop\\ZombieChoiceGame\\src\\data\\health_inventory.json");
                for (InventoryItem item : healthItems) {
                    model.addItem(item);
                }
                break;
            case "attack heavy":
                // Load an attack heavy inventory from its JSON file.
                List<InventoryItem> attackItems = loadInventoryFromJson("c:\\Users\\tthom\\Desktop\\ZombieChoiceGame\\src\\data\\attack_inventory.json");
                for (InventoryItem item : attackItems) {
                    model.addItem(item);
                }
                break;
            case "balanced":
                // Load the default balanced inventory.
                List<InventoryItem> balancedItems = loadInventoryFromJson("c:\\Users\\tthom\\Desktop\\ZombieChoiceGame\\src\\data\\balanced_inventory.json");
                for (InventoryItem item : balancedItems) {
                    model.addItem(item);
                }
                break;
            default:
                break;
        }
        System.out.println("[DEBUG] Applied " + choiceLabel + " inventory");
    }

    private List<InventoryItem> loadInventoryFromJson(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            // Parse the JSON array into an InventoryItem[]
            InventoryItem[] items = new Gson().fromJson(reader, InventoryItem[].class);
            return Arrays.asList(items);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // In showFirstChoiceView (or a new method) in MainController:
    private void showInventoryChoice() {
        GameScene scene = loader.getSceneById("inventory_choice");
        if (scene != null) {
            showSceneView(scene);
        } else {
            // Fallback to the normal start scene if inventory_choice is missing
            showSceneView(loader.getSceneById("start"));
        }
    }
}