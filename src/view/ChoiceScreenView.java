package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;
import java.util.List;
import java.util.function.Consumer;

public class ChoiceScreenView extends VBox {

    private Label healthLabel;
    private ProgressBar healthBar;
    private Label promptLabel;
    public TopBarView topBar = new TopBarView();

    public ChoiceScreenView(int health, String promptText, List<Choice> choices, boolean darkMode, Consumer<Choice> onChoiceSelected, Runnable onToggleTheme) {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));

        // Health
        healthLabel = new Label("Health: " + health);
        healthBar = new ProgressBar(health / 100.0);
        healthBar.setPrefWidth(300);
        healthBar.setStyle("-fx-accent: green;");
        healthBar.setTooltip(new Tooltip("Current Health: " + health));

        // Prompt
        promptLabel = new Label(promptText);
        promptLabel.setWrapText(true);

        // Choices
        HBox choiceRow = new HBox(40);
        choiceRow.setAlignment(Pos.CENTER);

        for (Choice choice : choices) {
            ImageView imageView = new ImageView(new Image("file:" + choice.imagePath));
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            Button button = new Button(choice.label);
            button.setOnAction(e -> onChoiceSelected.accept(choice));
            Theme.applyButtonStyle(button, darkMode);

            VBox column = new VBox(10, imageView, button);
            column.setAlignment(Pos.CENTER);
            choiceRow.getChildren().add(column);
        }

        topBar.toggleButton.setOnAction(e -> onToggleTheme.run());

        applyTheme(darkMode);

        getChildren().addAll(topBar, healthLabel, healthBar, promptLabel, choiceRow);
    }

    public void applyTheme(boolean darkMode) {
        setStyle("-fx-background-color: " + Theme.getBodyBackground(darkMode));
        promptLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode));
        healthLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode));
        topBar.applyTheme(darkMode);
    }

    public record Choice(String label, String imagePath, String id) {}
}
