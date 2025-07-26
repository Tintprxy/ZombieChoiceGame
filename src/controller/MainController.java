package controller;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.GameModel;
import model.GameScene;
import model.GameState;
import model.SceneLoader;
import view.*;
public class MainController {

    private final Stage stage;
    private final GameModel model;
    private final BorderPane rootPane;
    private final SceneLoader loader;
    private String lastHealthAppliedSceneId = null;
    // private Map<ItemType, List<InventoryItem>> currentInventory;

    public MainController(Stage stage) {
        this.stage = stage;
        this.model = new GameModel();
        this.rootPane = new BorderPane();
        this.loader = new SceneLoader("src/data/scenes.json"); 
        // this.currentInventory = model.getInventory();  
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

        view.topBar.toggleButton.setOnAction(e -> {
            model.toggleDarkMode();
            view.applyTheme(model.isDarkMode());
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
        // Only apply healthChange if entering a new scene
        if (!scene.getId().equals(lastHealthAppliedSceneId)) {
            int before = model.getHealth();
            model.subtractHealth(scene.getHealthChange());
            int after = model.getHealth();
            System.out.println("[DEBUG] Applied scene healthChange: " + scene.getHealthChange() +
                " | Health before: " + before + ", after: " + after);
            lastHealthAppliedSceneId = scene.getId();
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
                updateView();
            },
            item -> {
                System.out.println("[DEBUG] Attempting to consume item: " + item.getName() + ", type: " + item.getType());
                boolean consumed = model.consumeItem(item);
                if (consumed) {
                    System.out.println("[DEBUG] Consumed item: " + item.getName() +
                        " | Health restored: " + item.getHealthRestore() +
                        " | Health after: " + model.getHealth());
                    // Stay in the current scene: just refresh the current scene view
                    showSceneView(scene);
                } else {
                    System.out.println("[DEBUG] Failed to consume item: " + item.getName());
                }
            }
        );
        rootPane.setCenter(view);
    }
}