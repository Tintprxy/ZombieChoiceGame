package controller;

import java.util.List;
import java.util.Map;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.GameModel;
import model.GameScene;
import model.GameState;
import model.InventoryItem;
import model.ItemType;
import model.SceneLoader;
import java.io.File;
import view.*;
public class MainController {

    private final Stage stage;
    private final GameModel model;
    private final BorderPane rootPane;
    private final SceneLoader loader;
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
        GameScene scene = loader.getSceneById("start"); // "start" is your first scene's ID in JSON
        if (scene == null) {
            System.err.println("Scene not found: start");
            return;
        }

        model.subtractHealth(scene.getHealthChange());

        ChoiceScreenView view = new ChoiceScreenView(
           model.getHealth(),
            scene.getPrompt(),
            scene.getChoices(),
            model.isDarkMode(),
            model.getInventory(),
            choice -> {
                model.subtractHealth(choice.getHealthEffect());
                model.setCurrentState(GameState.ENDING); 
                updateView();
            },
            () -> {
                model.toggleDarkMode();
                updateView();
            }
        );

        rootPane.setTop(view.getTopBar());
        rootPane.setCenter(view.getLayout());
    }
}