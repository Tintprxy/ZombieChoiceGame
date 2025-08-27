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
import java.util.Optional;

import static view.Theme.*;
import controller.MainController;

public class InventoryChoiceView extends BorderPane {
    private final TopBarView topBar = new TopBarView();
    private boolean darkMode;

    private final Label promptLabel;
    private final Button healthHeavyButton;
    private final Button attackHeavyButton;
    private final Button balancedButton;
    private final Label healthLabel;
    private final ProgressBar healthBar;
    private final VBox inventorySidebar;

    public InventoryChoiceView(boolean darkMode, int health, Map<ItemType, List<InventoryItem>> inventory) {
        this.darkMode = darkMode;

        topBar.applyTheme(darkMode);
        setTop(topBar);


        // Add padding to the top bar for consistency (same as TitleView)
        VBox topBarContainer = new VBox(topBar);
        topBarContainer.setPadding(new Insets(20, 30, 0, 0)); // top, right, bottom, left
        topBarContainer.setAlignment(Pos.TOP_RIGHT);
        
      

        inventorySidebar = buildInventoryUI(inventory, darkMode);
        setLeft(inventorySidebar);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        promptLabel = new Label("Choose your inventory setup:");
        // Style will be set in applyTheme

        // Create images
        ImageView healthHeavyImg = createImageView("file:imgs/healthHeavyImg.jpg");
        ImageView attackHeavyImg = createImageView("file:imgs/attackHeavyImg.jpg");
        ImageView balancedImg = createImageView("file:imgs/balancedImg.jpg");

        // Make the images bigger
        if (healthHeavyImg != null) {
            healthHeavyImg.setFitHeight(300); // or your preferred size
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

        // Create buttons without images
        healthHeavyButton = new Button("Health Heavy");
        attackHeavyButton = new Button("Attack Heavy");
        balancedButton = new Button("Balanced");

        // VBox for each option: image above button
        VBox healthHeavyBox = new VBox(8, healthHeavyImg, healthHeavyButton);
        VBox attackHeavyBox = new VBox(8, attackHeavyImg, attackHeavyButton);
        VBox balancedBox = new VBox(8, balancedImg, balancedButton);

        healthHeavyBox.setAlignment(Pos.CENTER);
        attackHeavyBox.setAlignment(Pos.CENTER);
        balancedBox.setAlignment(Pos.CENTER);

        // Optionally, add padding or min width for uniformity
        healthHeavyBox.setPadding(new Insets(10, 10, 10, 10));
        attackHeavyBox.setPadding(new Insets(10, 10, 10, 10));
        balancedBox.setPadding(new Insets(10, 10, 10, 10));

        HBox buttonBox = new HBox(30, healthHeavyBox, attackHeavyBox, balancedBox);
        buttonBox.setAlignment(Pos.CENTER);

        healthLabel = new Label("Health: " + health);
        // Style will be set in applyTheme

        healthBar = new ProgressBar(health / 100.0);
        healthBar.setPrefWidth(300);
        healthBar.setStyle("-fx-accent: green;");
        VBox healthBox = new VBox(10, healthLabel, healthBar);
        healthBox.setAlignment(Pos.CENTER);

        centerBox.getChildren().addAll(promptLabel, buttonBox, healthBox);
        setCenter(centerBox);

        // Set background using theme
        setBackground(new Background(new BackgroundFill(
                Color.web(darkMode ? DARK_BG : LIGHT_BG), CornerRadii.EMPTY, Insets.EMPTY)));

        // Apply theme to all components
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

    private VBox buildInventoryUI(Map<ItemType, List<InventoryItem>> inventory, boolean darkMode) {
        VBox inventoryBox = new VBox(10);
        inventoryBox.setAlignment(Pos.CENTER_LEFT);
        inventoryBox.setPadding(new Insets(10));
        inventoryBox.setMaxWidth(250);
        inventoryBox.setStyle(
                "-fx-background-color: " + (darkMode ? DARK_BOX : LIGHT_BOX) + ";" +
                "-fx-border-color: " + (darkMode ? DARK_BORDER : LIGHT_BORDER) + ";" +
                "-fx-border-width: 1px;"
        );
        Label invTitle = new Label("Inventory");
        invTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: "
                + (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");
        inventoryBox.getChildren().add(invTitle);

        for (ItemType type : inventory.keySet()) {
            Label catLabel = new Label("• " + type.name() + ":");
            catLabel.setStyle("-fx-text-fill: " + (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");
            VBox itemList = new VBox(5);
            List<InventoryItem> items = inventory.get(type);
            if (items == null || items.isEmpty()) {
                Label emptyLabel = new Label("- none");
                emptyLabel.setStyle("-fx-text-fill: " + (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");
                itemList.getChildren().add(emptyLabel);
            } else {
                for (InventoryItem item : items) {
                    Label itemLabel = new Label("- " + item.getName());
                    itemLabel.setStyle("-fx-text-fill: " + (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");
                    itemList.getChildren().add(itemLabel);
                }
            }
            VBox section = new VBox(3, catLabel, itemList);
            inventoryBox.getChildren().add(section);
        }
        return inventoryBox;
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

        // Top bar
        topBar.applyTheme(darkMode);

        // Prompt label
        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " +
                (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");

        // Button style with consistent padding and rounded corners
        String btnStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 20; -fx-padding: 10 20;",
                darkMode ? DARK_BUTTON_BG : LIGHT_BUTTON_BG,
                darkMode ? DARK_BUTTON_TEXT : LIGHT_BUTTON_TEXT
        );
        healthHeavyButton.setStyle(btnStyle);
        attackHeavyButton.setStyle(btnStyle);
        balancedButton.setStyle(btnStyle);

        // Health label
        healthLabel.setStyle("-fx-text-fill: " + (darkMode ? DARK_TEXT : LIGHT_TEXT) + "; -fx-font-size: 14px;");

        // Inventory sidebar
        inventorySidebar.setStyle(
                "-fx-background-color: " + (darkMode ? DARK_BOX : LIGHT_BOX) + ";" +
                "-fx-border-color: " + (darkMode ? DARK_BORDER : LIGHT_BORDER) + ";" +
                "-fx-border-width: 1px;"
        );
        // Update inventory sidebar text colors
        for (javafx.scene.Node node : inventorySidebar.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle(label.getStyle().replaceAll("-fx-text-fill: #[A-Fa-f0-9]{6};?", "")
                        + "-fx-text-fill: " + (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");
            } else if (node instanceof VBox vbox) {
                for (javafx.scene.Node sub : vbox.getChildren()) {
                    if (sub instanceof Label label) {
                        label.setStyle(label.getStyle().replaceAll("-fx-text-fill: #[A-Fa-f0-9]{6};?", "")
                                + "-fx-text-fill: " + (darkMode ? DARK_TEXT : LIGHT_TEXT) + ";");
                    }
                }
            }
        }

        // Background
        setBackground(new Background(new BackgroundFill(
                Color.web(darkMode ? DARK_BG : LIGHT_BG), CornerRadii.EMPTY, Insets.EMPTY)));
    }
}