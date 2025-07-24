package controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.GameModel;
import view.TitleView;
import view.GameView;
import view.InstructionsView;

public class MainController {
    private final Stage stage;
    private final GameModel model;

    public MainController(Stage stage) {
        this.stage = stage;
        this.model = new GameModel();
    }

    public void startApp() {
        showTitleView();
    }

private void showTitleView() {
    TitleView titleView = new TitleView();
    titleView.applyTheme(model.isDarkMode());

    titleView.startButton.setOnAction(e -> showGameIntro()); 
    titleView.instructionsButton.setOnAction(e -> showInstructionsView()); 

    titleView.topBar.toggleButton.setOnAction(e -> {
        model.toggleDarkMode();
        titleView.applyTheme(model.isDarkMode());
    });

    Scene scene = new Scene(titleView, 800, 600);
    stage.setScene(scene);
    stage.setTitle("Zombie Choice Game");
    stage.show();
}

// place holder
    private void showGameIntro() {
        GameView game = new GameView();
        game.applyTheme(model.isDarkMode());

        game.continueButton.setOnAction(e -> {
            game.updateText("A door creaks open... something is coming.", "Continue...");
        });

        Scene scene = new Scene(game, 500, 300);
        stage.setScene(scene);
    }
    
    private void showInstructionsView() {
        InstructionsView instructionsView = new InstructionsView();
        instructionsView.applyTheme(model.isDarkMode());

        instructionsView.backButton.setOnAction(e -> showTitleView());
        instructionsView.topBar.toggleButton.setOnAction(e -> {
            model.toggleDarkMode();
            instructionsView.applyTheme(model.isDarkMode());
        });

        Scene scene = new Scene(instructionsView, 600, 600);
        stage.setScene(scene);
    }
}