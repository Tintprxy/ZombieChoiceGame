package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ChooseStoryView extends BorderPane {
    private final TopBarView topBar;
    private final Button story1Button;
    private final Button story2Button;
    private final VBox contentBox;

    public ChooseStoryView(boolean darkMode) {
        topBar = new TopBarView();
        topBar.applyTheme(darkMode);

        story1Button = new Button("Story Option 1");
        story2Button = new Button("Story Option 2");

        contentBox = new VBox(15, story1Button, story2Button);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));

        updateButtonsStyle(darkMode);
        setTop(topBar);
        setCenter(contentBox);
        applyTheme(darkMode);
    }

    public void applyTheme(boolean darkMode) {
        setBackground(new Background(new BackgroundFill(
                darkMode ? Color.web("#2e2e2e") : Color.WHITE,
                CornerRadii.EMPTY, Insets.EMPTY)));

        topBar.applyTheme(darkMode);
        updateButtonsStyle(darkMode);

        for (Node node : contentBox.getChildren()) {
            if (node instanceof Button button) {
                button.setStyle(darkMode
                        ? "-fx-background-color: #444444; -fx-text-fill: white;"
                        : "-fx-background-color: #eeeeee; -fx-text-fill: black;");
            }
        }
    }

    private void updateButtonsStyle(boolean darkMode) {
        String btnStyle = darkMode
                ? "-fx-background-color: #444444; -fx-text-fill: white;"
                : "-fx-background-color: #eeeeee; -fx-text-fill: black;";
        story1Button.setStyle(btnStyle);
        story2Button.setStyle(btnStyle);
    }

    public TopBarView getTopBar() {
        return topBar;
    }

    public Button getStory1Button() {
        return story1Button;
    }

    public Button getStory2Button() {
        return story2Button;
    }
}