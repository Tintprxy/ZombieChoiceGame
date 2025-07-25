package controller;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.GameModel;
import model.GameState;
import view.*;
import view.ChoiceScreenView;

public class MainController {

    private final Stage stage;
    private final GameModel model;
    private final BorderPane rootPane;

    public MainController(Stage stage) {
        this.stage = stage;
        this.model = new GameModel();
        this.rootPane = new BorderPane();
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
        var choices = List.of(
            new ChoiceScreenView.Choice("Drive", "imgs/armoredCarImg.jpg", "drive"),
            new ChoiceScreenView.Choice("Walk", "imgs/walkingImg.jpg", "walk")
        );

        ChoiceScreenView view = new ChoiceScreenView(
            model.getHealth(),
            "The street you're on is quiet ... but you hear zombies groaning in the distance.\n" +
            "You need to make a choice. What will you do?\n\nDo you want to drive or walk?",
            choices,
            model.isDarkMode(),

            selectedChoice -> {
                if (selectedChoice.id().equals("walk")) {
                    model.subtractHealth(30); 
                }
                model.setCurrentState(GameState.ENDING);
                updateView();
            },

            // When dark mode is toggled:
            () -> {
                model.toggleDarkMode();
                showFirstChoiceView(); 
            }
        );
        rootPane.setTop(null); 
        rootPane.setCenter(view);
    }

    // private void applyFirstChoiceTheme(VBox layout, TopBarView topBar) {
    //     boolean dark = model.isDarkMode();

    //     // Shared color logic
    //     layout.setStyle("-fx-background-color: " + Theme.getBodyBackground(dark) + ";");

    //     // Get label and style it
    //     layout.getChildren().stream()
    //         .filter(node -> node instanceof Label)
    //         .map(node -> (Label) node)
    //         .forEach(label -> label.setStyle("-fx-text-fill: " + Theme.getTextColor(dark) + ";"));

    //     // Top bar handles itself
    //     topBar.applyTheme(dark);
    // }
}