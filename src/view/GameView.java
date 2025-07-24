package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.Node;

public class GameView extends VBox {
    private final Text storyText = new Text("You wake up in a dark hospital room...\nA growl echoes from the hallway.");
    public final Button continueButton = new Button("Next");

    public GameView() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        storyText.setStyle("-fx-font-size: 16px;");
        storyText.setWrappingWidth(400);

        getChildren().addAll(storyText, continueButton);
    }

    public void updateText(String newText, String buttonText) {
        storyText.setText(newText);
        continueButton.setText(buttonText);
    }

    public void applyTheme(boolean isDarkMode) {
        Color bg = isDarkMode ? Color.web("#2e2e2e") : Color.WHITE;
        setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));

        for (Node node : getChildren()) {
            if (node instanceof Text text) {
                text.setFill(isDarkMode ? Color.LIGHTGRAY : Color.BLACK);
            } else if (node instanceof Button button) {
                button.setStyle(isDarkMode
                        ? "-fx-background-color: #444444; -fx-text-fill: white;"
                        : "-fx-background-color: #eeeeee; -fx-text-fill: black;");
            }
        }
    }
}