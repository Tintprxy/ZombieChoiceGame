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
            model.setCurrentState(GameState.FIRST_CHOICE);
            updateView();
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
            model.subtractHealth(scene.getHealthChange());
            int after = model.getHealth();
            System.out.println("[DEBUG] Applied scene healthChange: " + scene.getHealthChange() +
                " | Health before: " + before + ", after: " + after);
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
                int before = model.getHealth();
                model.subtractHealth(choice.getHealthEffect());
                int after = model.getHealth();
                System.out.println("[DEBUG] Applied choice healthEffect: " + choice.getHealthEffect() +
                    " | Health before: " + before + ", after: " + after);

                GameScene next = loader.getSceneById(choice.getNextId());
                if (next != null) {
                    showSceneView(next);
                } else {
                    model.setCurrentState(GameState.ENDING);
                    updateView();
                }
            },
            () -> {
                model.toggleDarkMode();
                showSceneView(currentScene);
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
}