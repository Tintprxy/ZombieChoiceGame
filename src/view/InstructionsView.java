package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.control.ScrollPane;

public class InstructionsView extends BorderPane {
    public final TopBarView topBar = new TopBarView();
    public final Button backButton = new Button("Back to Menu");

    private final VBox contentBox = new VBox(20);
    private final ScrollPane scrollPane;

    public InstructionsView() {
        setTop(topBar);

        Text heading = new Text("Game Instructions");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Text instructions = new Text("""
            Welcome to the Zombie Choice Game!
            This game is a text-based adventure where you make choices to survive in a zombie-infested world.
        
            Controls:
            click on buttons to make choices, and use the top bar to toggle dark mode.

            Health Bar:
            Your health will decrease when injured or attacked. Watch the bar at the top of the screen.

            Inventory:
            You can collect items like food, weapons, and tools.
            These items will give you advantages/disadvantages throughout the game.
            You can carry up to:
              • 2 weapons
              • 3 consumables(food, water, etc.)
              • 1 key item(map, keys, etc.)

            IMPORTANT:
            Weapons are used automatically to fight enemies.
            Weapons have durability, which decreases with use.
            Consumables restore health.
            Key items are used to progress the story or unlock new areas not reachable without them..
            Some choices will completely drain your health bar.
            THIS GAME IS MADE TO BE AS LOGICAL AS POSSIBLE WHILE STILL BEING CHALLENGING.
            If you find yourself stuck, try to think about your choices.
            Some choices mean nothing while others could be life or death.
            Stay alert, make smart choices, and survive.
            """);
        instructions.setWrappingWidth(450);
        instructions.setTextAlignment(TextAlignment.LEFT);
        instructions.setStyle("-fx-font-size: 14px;");

        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.getChildren().addAll(heading, instructions);

        scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        setCenter(scrollPane);
        setBottom(backButton);
        BorderPane.setMargin(backButton, new Insets(20, 0, 20, 0));
    }

    public void applyTheme(boolean isDarkMode) {
        setBackground(new Background(new BackgroundFill(
                Color.web(isDarkMode ? Theme.DARK_BG : Theme.LIGHT_BG),
                CornerRadii.EMPTY, Insets.EMPTY)));

        scrollPane.setStyle(
                "-fx-background: " + (isDarkMode ? Theme.DARK_BG : Theme.LIGHT_BG) + ";" +
                        "-fx-background-color: " + (isDarkMode ? Theme.DARK_BG : Theme.LIGHT_BG) + ";"
        );

        topBar.applyTheme(isDarkMode);
        backButton.setStyle(isDarkMode
                ? "-fx-background-color: " + Theme.DARK_BUTTON_BG + "; -fx-text-fill: " + Theme.DARK_BUTTON_TEXT + ";"
                : "-fx-background-color: " + Theme.LIGHT_BUTTON_BG + "; -fx-text-fill: " + Theme.LIGHT_BUTTON_TEXT + ";");

        contentBox.getChildren().forEach(node -> {
            if (node instanceof Text text) {
                text.setFill(isDarkMode ? Color.web(Theme.DARK_TEXT) : Color.web(Theme.LIGHT_TEXT));
            }
        });
    }

    public Scene buildScene() {
        return new Scene(this, 800, 900);
    }
}