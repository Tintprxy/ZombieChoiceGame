package controller;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.GameModel;
import model.GameScene;
import model.GameState;
import model.InventoryItem;
import model.InventoryLoader;
import model.ItemType;
import model.SceneLoader;
import view.ChoiceScreenView;
import view.InstructionsView;
import view.TitleView;
import view.InventoryChoiceView;
import view.StoryTurnstileView;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MainController {
    private final Stage stage;
    private final GameModel model;
    private final BorderPane rootPane;
    // private final SceneLoader SceneLoader;
    private String lastHealthAppliedSceneId = null;
    private final Set<String> addItemProcessedScenes = new HashSet<>();
    private GameScene currentScene;

    public MainController(Stage stage) {
        this.stage = stage;
        this.model = new GameModel();
        this.rootPane = new BorderPane();
    }

    public void startApp() {
        Scene scene = new Scene(rootPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Zombie Choice Game");
        stage.setMinWidth(10);
        stage.setMinHeight(10);
        updateView(); 
        stage.show();
    }

    public void updateView() {
        rootPane.setTop(null);
        rootPane.setCenter(null);

        switch (model.getCurrentState()) {
            case TITLE -> showTitleView();
            case INSTRUCTIONS -> showInstructionsView();
            // case FIRST_CHOICE -> showFirstChoiceView();  
            default -> System.out.println("Unknown state.");
        }
    }

    protected void showTitleView() {
        TitleView titleView = new TitleView();
        titleView.applyTheme(model.isDarkMode());

        titleView.startButton.setOnAction(e -> {
            model.clearInventory();
            showChooseStoryView();
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

    private void showChooseStoryView() {
        StoryTurnstileView turnstileView = new StoryTurnstileView(model.isDarkMode());
        turnstileView.getTopBar().toggleButton.setOnAction(e -> {
            model.toggleDarkMode();
            System.out.println("[DEBUG] Dark mode toggled: " + model.isDarkMode());
            turnstileView.applyTheme(model.isDarkMode());
        });

        turnstileView.getTopBar().resetButton.setOnAction(e -> {
            System.out.println("[DEBUG] Reset button clicked.");
            lastHealthAppliedSceneId = null;
            addItemProcessedScenes.clear();
            model.clearInventory();
            model.resetHealth();
            model.setCurrentState(GameState.TITLE);
            updateView();
        });
        
        Button driveButton = (Button) turnstileView.getStory1Box().getChildren().get(3);
        driveButton.setOnAction(e -> {
            SceneLoader sceneLoader = new SceneLoader("src/data/drive_story1.json");
            showInventoryChoiceView(sceneLoader, "start");
        });
        
        Button walkButton = (Button) turnstileView.getStory2Box().getChildren().get(3);
        walkButton.setOnAction(e -> {
            SceneLoader sceneLoader = new SceneLoader("src/data/walk_story2.json");
            showInventoryChoiceView(sceneLoader, "start");
        });
        
        rootPane.setCenter(turnstileView);
        System.out.println("Story1Box children: " + turnstileView.getStory1Box().getChildren().size());
        System.out.println("Story2Box children: " + turnstileView.getStory2Box().getChildren().size());
    }

        private void showInventoryChoiceView(SceneLoader SceneLoader, String startSceneId) {
        InventoryChoiceView inventoryView = new InventoryChoiceView(
            model.isDarkMode(),
            model.getHealth(),
            model.getInventory()
        );

        inventoryView.getTopBar().toggleButton.setOnAction(e -> {
            model.toggleDarkMode();
            System.out.println("[DEBUG] Dark mode toggled: " + model.isDarkMode());
            inventoryView.applyTheme(model.isDarkMode());
        });

        inventoryView.getTopBar().resetButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Reset Game");
            confirm.setHeaderText("Return to the title screen?");
            confirm.setContentText("Any progress will be lost.");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                lastHealthAppliedSceneId = null;
                addItemProcessedScenes.clear();
                model.clearInventory();
                model.resetHealth();
                model.setCurrentState(GameState.TITLE);
                updateView();
            }
        });


        inventoryView.getHealthHeavyButton().setOnAction(e -> {
            System.out.println("[DEBUG] Health Heavy button clicked.");
            applyInventoryChoice("Health Heavy");
            GameScene next = SceneLoader.getSceneById(startSceneId); 
            if (next != null) {
                System.out.println("[DEBUG] Starting scene: " + next.getId());
                showSceneView(next, SceneLoader);
            } else {
                System.out.println("[DEBUG] Failed to load starting scene.");
            }
        });

        inventoryView.getAttackHeavyButton().setOnAction(e -> {
            System.out.println("[DEBUG] Attack Heavy button clicked.");
            applyInventoryChoice("Attack Heavy");
            GameScene next = SceneLoader.getSceneById(startSceneId);
            if (next != null) {
                System.out.println("[DEBUG] Starting scene: " + next.getId());
                showSceneView(next, SceneLoader);
            } else {
                System.out.println("[DEBUG] Failed to load starting scene.");
            }
        });

        inventoryView.getBalancedButton().setOnAction(e -> {
            System.out.println("[DEBUG] Balanced button clicked.");
            applyInventoryChoice("Balanced");
            GameScene next = SceneLoader.getSceneById(startSceneId);
            if (next != null) {
                System.out.println("[DEBUG] Starting scene: " + next.getId());
                showSceneView(next, SceneLoader);
            } else {
                System.out.println("[DEBUG] Failed to load starting scene.");
            }
        });

        rootPane.setCenter(inventoryView);
    }

        private void applyInventoryChoice(String choiceLabel) {
        model.clearInventory();
        switch (choiceLabel.toLowerCase()) {
            case "health heavy":
                List<InventoryItem> healthItems = loadInventoryFromJson("c:\\Users\\tthom\\documents\\--Main\\ZombieChoiceGame\\src\\data\\health_inventory.json");
                for (InventoryItem item : healthItems) {
                    model.addItem(item);
                }
                break;
            case "attack heavy":
                List<InventoryItem> attackItems = loadInventoryFromJson("c:\\Users\\tthom\\documents\\--Main\\ZombieChoiceGame\\src\\data\\attack_inventory.json");
                for (InventoryItem item : attackItems) {
                    model.addItem(item);
                }
                break;
            case "balanced":
                List<InventoryItem> balancedItems = loadInventoryFromJson("c:\\Users\\tthom\\documents\\--Main\\ZombieChoiceGame\\src\\data\\balanced_inventory.json");
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
            InventoryItem[] items = new Gson().fromJson(reader, InventoryItem[].class);
            return Arrays.asList(items);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addWeaponToInventory(InventoryItem weapon, String nextSceneId, SceneLoader sceneLoader) {
        if (weapon.getType() != ItemType.WEAPON) {
            System.out.println("[DEBUG] Tried to add non-weapon item as weapon.");
            return;
        }
        List<InventoryItem> weapons = model.getInventory().getOrDefault(ItemType.WEAPON, new ArrayList<>());
        int maxWeapons = 2;
        if (weapons.size() < maxWeapons) {
            boolean added = model.addItem(weapon);
            if (added) {
                writeTempWeaponJson(weapon);
                addWeaponToJson(weapon);
                System.out.println("[DEBUG] Added weapon: " + weapon.getName());
                addItemProcessedScenes.add(nextSceneId); 
                GameScene nextScene = sceneLoader.getSceneById(nextSceneId);
                if (nextScene != null) {
                    showSceneView(nextScene, sceneLoader);
                } else {
                    updateView();
                }
            } else {
                List<String> weaponNames = weapons.stream().map(InventoryItem::getName).toList();
                showWeaponRemovalDialog(weaponNames, weapon, nextSceneId, sceneLoader);
            }
        } else {
            List<String> weaponNames = weapons.stream().map(InventoryItem::getName).toList();
            showWeaponRemovalDialog(weaponNames, weapon, nextSceneId, sceneLoader);
        }
    }

    private void resetInventoryToDefault() {
        try {
            File defaultFile = new File("src/data/empty_inventory.json");
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

    private void showSceneView(GameScene scene, SceneLoader sceneLoader) {
        System.out.println("[DEBUG] Scene " + scene.getId() + " bitten flag: " + scene.isBitten());
        this.currentScene = scene;
        GameScene modifiedScene = scene;

        if (modifiedScene.isBitten()) {
            boolean hasAntidote = false;
            List<InventoryItem> keyItems = model.getInventory().get(ItemType.KEY_ITEM);
            if (keyItems != null) {
                hasAntidote = keyItems.stream().anyMatch(item -> item.getName().equalsIgnoreCase("Antidote"));
            }
            if (hasAntidote) {
                System.out.println("[DEBUG] Player is bitten and has the antidote, switching to infection_choice scene.");
                modifiedScene = sceneLoader.getSceneById("infection_choice");
            } else {
                System.out.println("[DEBUG] Player is bitten and does NOT have the antidote, switching to game_over_infection scene.");
                modifiedScene = sceneLoader.getSceneById("game_over_infection");
            }
        }

        final GameScene currentSceneFinal = modifiedScene;

        if (!currentSceneFinal.getId().equals(lastHealthAppliedSceneId)) {
            if (currentSceneFinal.getHealthChange() == -1) {
                model.setHealth(0);
                System.out.println("[DEBUG] HealthChange is -1 for scene " + currentSceneFinal.getId() + ". Health set to 0.");
            } else {
                int before = model.getHealth();
                model.subtractHealth(currentSceneFinal.getHealthChange());
                System.out.println("[DEBUG] Applied scene healthChange: " 
                    + currentSceneFinal.getHealthChange() + " | Health before: " 
                    + before + ", after: " + model.getHealth());
            }
            lastHealthAppliedSceneId = currentSceneFinal.getId();
        }
        
        if (currentSceneFinal.getId().contains("infection_cured")) {
            List<InventoryItem> keyItems = model.getInventory().get(ItemType.KEY_ITEM);
            if (keyItems != null) {
                boolean removed = keyItems.removeIf(item -> item.getName().equalsIgnoreCase("Antidote"));
                if (removed) {
                    System.out.println("[DEBUG] Antidote has been removed from inventory for scene: " + currentSceneFinal.getId());
                    model.setAntidoteUsed(true);
                } else {
                    System.out.println("[DEBUG] No antidote found to remove for scene: " + currentSceneFinal.getId());
                }
            }
        }

        if ("inventory_choice".equals(currentSceneFinal.getId())) {
            showInventoryChoiceView(sceneLoader, "start");
            return;
        }

        if (currentSceneFinal.getAddItem() != null && !addItemProcessedScenes.contains(currentSceneFinal.getId())) {
            InventoryItem item = currentSceneFinal.getAddItem();
            addItemProcessedScenes.add(currentSceneFinal.getId());
            if (item.getType() == ItemType.WEAPON) {
                String nextSceneId = currentSceneFinal.getChoices().get(0).getNextId(); 
                addWeaponToInventory(item, nextSceneId, sceneLoader);
            } else {
                model.addItem(item);
                System.out.println("[DEBUG] Added item from scene: " + item.getName());
            }
        }

        if (currentSceneFinal.getNewKeyItem() != null && !currentSceneFinal.getNewKeyItem().trim().isEmpty()) {
            String keyItemFilePath = "src/data/" + currentSceneFinal.getNewKeyItem().toLowerCase() + ".json";
            InventoryItem keyItem = InventoryLoader.loadKeyItemFromJson(keyItemFilePath);
            if (keyItem != null) {
                model.setKeyItem(keyItem);
                System.out.println("[DEBUG] New key item set: " + keyItem.getName());
            } else {
                System.out.println("[DEBUG] Failed to load key item from: " + keyItemFilePath);
            }
        }

        ChoiceScreenView view = new ChoiceScreenView(
            model.getHealth(),
            currentSceneFinal.getPrompt(),
            currentSceneFinal.getChoices(),
            model.isDarkMode(),
            model.getInventory(),
            model, 
            choice -> {
                if ("inventory_choice".equals(currentSceneFinal.getId())) {
                    applyInventoryChoice(choice.getLabel());
                    showSceneView(sceneLoader.getSceneById("start"), sceneLoader);
                    return;
                }
                if (currentSceneFinal.getThreatLevel() > -1 && choice.getLabel().toLowerCase().contains("fight")) {
                    int threat = currentSceneFinal.getThreatLevel();
                    int fightNumber = currentSceneFinal.getFightNumber();
                    int ded = computeDurabilityDecrease(threat);
                    int winPenalty = computeWinHealthPenalty(threat);
                    int losePenalty = computeLoseHealthPenalty(threat);
                    handleFight(currentSceneFinal, fightNumber, ded, winPenalty, losePenalty, choice.getNextId(), sceneLoader);
                    model.removeBrokenWeapons();
                    return;
                }
                System.out.printf("[DEBUG] No fight calculation for choice \"%s\"; loading scene: %s%n", 
                    choice.getLabel(), choice.getNextId());
                GameScene next = sceneLoader.getSceneById(choice.getNextId());
                if (next != null) {
                    showSceneView(next, sceneLoader);
                } else {
                    System.out.println("[DEBUG] No scene found with id " + choice.getNextId());
                    model.setCurrentState(GameState.ENDING);
                    updateView();
                }
            },
            () -> {
                model.toggleDarkMode();
                showSceneView(currentSceneFinal, sceneLoader);
            },
            () -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Reset Game");
                confirm.setHeaderText("Are you sure you want to return to the title screen?");
                confirm.setContentText("Any current progress will be lost.");
                Optional<ButtonType> res = confirm.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.OK) {
                    lastHealthAppliedSceneId = null;
                    addItemProcessedScenes.clear();
                    resetInventoryToDefault();
                    model.resetHealth();
                    model.setCurrentState(GameState.TITLE);
                    updateView();
                }
            },
            item -> {
                System.out.println("[DEBUG] Attempting to consume item: " + item.getName() + ", type: " + item.getType());
                boolean consumed = model.consumeItem(item);
                if (consumed) {
                    System.out.println("[DEBUG] Consumed item: " + item.getName() +
                        " | Health after: " + model.getHealth());
                    showSceneView(scene, sceneLoader);
                } else {
                    System.out.println("[DEBUG] Failed to consume item: " + item.getName());
                }
            }
        );
        rootPane.setCenter(view);
    }

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

    private void showWeaponRemovalDialog(List<String> weaponNames, InventoryItem item, String nextSceneId, SceneLoader SceneLoader) {
        Platform.runLater(() -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(weaponNames.get(0), weaponNames);
            dialog.setTitle("Weapon Inventory Full");
            dialog.setHeaderText("Choose a weapon to remove to make space for: " + item.getName());
            dialog.setContentText("Remove:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(selectedName -> {
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
                    GameScene nextScene = SceneLoader.getSceneById(nextSceneId);
                    if (nextScene != null) {
                        showSceneView(nextScene, SceneLoader);
                    } else {
                        updateView();
                    }
                } else {
                    showWeaponRemovalDialog(weaponNames, item, nextSceneId, SceneLoader);
                }
            });
        });
    }

    private void addWeaponToJson(InventoryItem item) {
        try (FileReader reader = new FileReader("src/data/inventory.json")) {
            JsonElement element = JsonParser.parseReader(reader);
            JsonArray arr;
            if (element == null || !element.isJsonArray()) {
                arr = new JsonArray();
            } else {
                arr = element.getAsJsonArray();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("name", item.getName());
            obj.addProperty("type", item.getType().toString());
            obj.addProperty("durability", item.getDurability());
            obj.addProperty("power", item.getPower());
            arr.add(obj);
            try (FileWriter writer = new FileWriter("src/data/inventory.json")) {
                new GsonBuilder().setPrettyPrinting().create().toJson(arr, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to add weapon to JSON: " + e.getMessage());
        }
    }

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

    private void handleFight(GameScene scene, int fightNumber, int decreaseDurAmount, int subHealthWin, int subHealthLose, String defaultWinSceneId, SceneLoader SceneLoader) {
        int threatLevel = scene.getThreatLevel();
        System.out.printf("[DEBUG] In handleFight: Scene \"%s\" with fightNumber: %d, threatLevel: %d%n",
            scene.getId(), fightNumber, threatLevel);
        List<InventoryItem> weapons = model.getInventory().getOrDefault(ItemType.WEAPON, new ArrayList<>());
        List<InventoryItem> winningWeapons = weapons.stream()
            .filter(w -> w.getPower() >= threatLevel && w.getDurability() > 0)
            .sorted(Comparator.comparingInt(InventoryItem::getPower))
            .toList();
        InventoryItem chosenWeapon = winningWeapons.isEmpty() ? null : winningWeapons.get(0);
        String winSceneId = (fightNumber > 0) ? "fight_result_win_" + fightNumber : defaultWinSceneId;
        String loseSceneId = (fightNumber > 0) ? "fight_result_lose_" + fightNumber : "fight_result_lose_1";
        System.out.printf("[DEBUG] Computed winSceneId: %s, loseSceneId: %s%n", winSceneId, loseSceneId);

        if (chosenWeapon != null) {
            int oldDurability = chosenWeapon.getDurability();
            chosenWeapon.decreaseDurability(decreaseDurAmount);
            int newDurability = chosenWeapon.getDurability();
            model.subtractHealth(-subHealthWin);
            System.out.printf("[DEBUG] WIN | used %s (power %d >= threat %d), durability decreased from %d to %d | new health: %d%n",
                chosenWeapon.getName(), chosenWeapon.getPower(), threatLevel, oldDurability, newDurability, model.getHealth());
            GameScene winScene = SceneLoader.getSceneById(winSceneId);
            if (winScene != null) {
                showSceneView(winScene, SceneLoader);
            } else {
                System.out.println("[DEBUG] No win scene found with id " + winSceneId);
                model.setCurrentState(GameState.ENDING);
                updateView();
            }
        } else {
            int fistsPower = 2;
            if (fistsPower >= threatLevel) {
                model.subtractHealth(-subHealthWin);
                System.out.printf("[DEBUG] WIN (unarmed) | fists power(%d) >= threat(%d) | new health: %d%n",
                    fistsPower, threatLevel, model.getHealth());
                GameScene winScene = SceneLoader.getSceneById(winSceneId);
                if (winScene != null) {
                    showSceneView(winScene, SceneLoader);
                } else {
                    System.out.println("[DEBUG] No win scene found with id " + winSceneId);
                    model.setCurrentState(GameState.ENDING);
                    updateView();
                }
            } else {
                model.subtractHealth(-subHealthLose);
                System.out.printf("[DEBUG] LOSE (unarmed) | fists power(%d) < threat(%d) | new health: %d%n",
                    fistsPower, threatLevel, model.getHealth());
                GameScene loseScene = SceneLoader.getSceneById(loseSceneId);
                if (loseScene != null) {
                    showSceneView(loseScene, SceneLoader);
                } else {
                    System.out.println("[DEBUG] No lose scene found with id " + loseSceneId);
                    model.setCurrentState(GameState.ENDING);
                    updateView();
                }
            }
        }
    }

    private int computeDurabilityDecrease(int threatLevel) {
        return (threatLevel >= 5) ? 2 : 1;
    }

    private int computeWinHealthPenalty(int threatLevel) {
        return 0;
    }

    private int computeLoseHealthPenalty(int threatLevel) {
        return 25 + (2 * threatLevel);
    }
}