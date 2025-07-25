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
public class InstructionsView extends BorderPane {
    public final TopBarView topBar = new TopBarView();
    public final Button backButton = new Button("Back to Menu");

    private final VBox contentBox = new VBox(15);

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

            Danger:
            Every fight or injury will reduce your health.
            Travel is measured in miles.
            Some choices will completly drain your health bar.

            Stay alert, make smart choices, and survive.
            """);
        instructions.setWrappingWidth(450);
        instructions.setTextAlignment(TextAlignment.LEFT);
        instructions.setStyle("-fx-font-size: 14px;");

        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.getChildren().addAll(heading, instructions, backButton);

        setCenter(contentBox);
    }

    public void applyTheme(boolean isDarkMode) {
        setBackground(new Background(new BackgroundFill(
                isDarkMode ? Color.web("#2e2e2e") : Color.WHITE,
                CornerRadii.EMPTY, Insets.EMPTY)));

        topBar.applyTheme(isDarkMode);
        backButton.setStyle(isDarkMode
                ? "-fx-background-color: #444444; -fx-text-fill: white;"
                : "-fx-background-color: #eeeeee; -fx-text-fill: black;");

        contentBox.getChildren().forEach(node -> {
            if (node instanceof Text text) {
                text.setFill(isDarkMode ? Color.LIGHTGRAY : Color.BLACK);
            }
        });
    }

    public Scene buildScene() {
        return new Scene(this, 600, 500);
    }
}