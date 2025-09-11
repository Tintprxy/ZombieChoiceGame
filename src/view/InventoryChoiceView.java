package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.InventoryItem;
import model.ItemType;
import java.util.List;
import java.util.Map;
import static view.Theme.*;


public class InventoryChoiceView extends BorderPane {
    private final TopBarView topBar = new TopBarView();
    private boolean darkMode;

    private final Label promptLabel;
    private final Button healthHeavyButton;
    private final Button attackHeavyButton;
    private final Button balancedButton;

    public InventoryChoiceView(boolean darkMode, int health, Map<ItemType, List<InventoryItem>> inventory) {
        this.darkMode = darkMode;

        topBar.applyTheme(darkMode);
        setTop(topBar);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        promptLabel = new Label("Choose your inventory setup:");

        ImageView healthHeavyImg = createImageView("file:imgs/healthHeavyImg.jpg");
        ImageView attackHeavyImg = createImageView("file:imgs/attackHeavyImg.jpg");
        ImageView balancedImg = createImageView("file:imgs/balancedImg.jpg");

        if (healthHeavyImg != null) {
            healthHeavyImg.setFitHeight(300);
            healthHeavyImg.setPreserveRatio(true);
        }
        if (attackHeavyImg != null) {
            attackHeavyImg.setFitHeight(300);
            attackHeavyImg.setPreserveRatio(true);
        }
        if (balancedImg != null) {
            balancedImg.setFitHeight(300);
            balancedImg.setPreserveRatio(true);
        }

        healthHeavyButton = new Button("Health Heavy");
        attackHeavyButton = new Button("Attack Heavy");
        balancedButton = new Button("Balanced");

        VBox healthHeavyBox = new VBox(8, healthHeavyImg, healthHeavyButton);
        VBox attackHeavyBox = new VBox(8, attackHeavyImg, attackHeavyButton);
        VBox balancedBox = new VBox(8, balancedImg, balancedButton);

        healthHeavyBox.setAlignment(Pos.CENTER);
        attackHeavyBox.setAlignment(Pos.CENTER);
        balancedBox.setAlignment(Pos.CENTER);

        healthHeavyBox.setPadding(new Insets(10, 10, 10, 10));
        attackHeavyBox.setPadding(new Insets(10, 10, 10, 10));
        balancedBox.setPadding(new Insets(10, 10, 10, 10));

        HBox buttonBox = new HBox(30, healthHeavyBox, attackHeavyBox, balancedBox);
        buttonBox.setAlignment(Pos.CENTER);

        centerBox.getChildren().addAll(promptLabel, buttonBox);
        setCenter(centerBox);

        setBackground(new Background(new BackgroundFill(
                Color.web(darkMode ? DARK_BG : LIGHT_BG), CornerRadii.EMPTY, Insets.EMPTY)));

        applyTheme(darkMode);
    }

    private ImageView createImageView(String imagePath) {
        ImageView imageView = null;
        try {
            Image image = new Image(imagePath);
            imageView = new ImageView(image);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("[DEBUG] Failed to load image: " + imagePath);
        }
        return imageView;
    }

    public Button getHealthHeavyButton() {
        return healthHeavyButton;
    }

    public Button getAttackHeavyButton() {
        return attackHeavyButton;
    }

    public Button getBalancedButton() {
        return balancedButton;
    }

    public TopBarView getTopBar() {
        return topBar;
    }

    public void applyTheme(boolean darkMode) {
        this.darkMode = darkMode;

        topBar.applyTheme(darkMode);

        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " +
                (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");

        String btnStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 20; -fx-padding: 10 20;",
                darkMode ? DARK_BUTTON_BG : LIGHT_BUTTON_BG,
                darkMode ? DARK_BUTTON_TEXT : LIGHT_BUTTON_TEXT
        );
        healthHeavyButton.setStyle(btnStyle);
        attackHeavyButton.setStyle(btnStyle);
        balancedButton.setStyle(btnStyle);

        setBackground(new Background(new BackgroundFill(
                Color.web(darkMode ? DARK_BG : LIGHT_BG), CornerRadii.EMPTY, Insets.EMPTY)));
    }
}