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
        
        // Set up and wire the local top bar just like in ChoiceScreenView
        topBar.applyTheme(darkMode);
        setTop(topBar);
        wireTopBar();
        
        inventorySidebar = buildInventoryUI(inventory, darkMode);
        setLeft(inventorySidebar);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        promptLabel = new Label("Choose your inventory setup:");
        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + (darkMode ? "white" : "black") + ";");

        healthHeavyButton = new Button("Health Heavy", createImageView("file:imgs/healthHeavyImg.jpg"));
        attackHeavyButton = new Button("Attack Heavy", createImageView("file:imgs/healthHeavyImg.jpg"));
        balancedButton = new Button("Balanced", createImageView("file:imgs/balancedImg.jpg"));

        String btnStyle = darkMode
                ? "-fx-background-color: #444444; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;"
                : "-fx-background-color: #eeeeee; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10 20;";
        healthHeavyButton.setStyle(btnStyle);
        attackHeavyButton.setStyle(btnStyle);
        balancedButton.setStyle(btnStyle);

        HBox buttonBox = new HBox(20, healthHeavyButton, attackHeavyButton, balancedButton);
        buttonBox.setAlignment(Pos.CENTER);

        healthLabel = new Label("Health: " + health);
        healthLabel.setStyle("-fx-text-fill: " + (darkMode ? "white" : "black") + "; -fx-font-size: 14px;");
        healthBar = new ProgressBar(health / 100.0);
        healthBar.setPrefWidth(300);
        healthBar.setStyle("-fx-accent: green;");
        VBox healthBox = new VBox(10, healthLabel, healthBar);
        healthBox.setAlignment(Pos.CENTER);

        centerBox.getChildren().addAll(promptLabel, buttonBox, healthBox);
        setCenter(centerBox);

        setBackground(new Background(new BackgroundFill(
                darkMode ? Color.web("#2e2e2e") : Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    }
    
    // Wire the local top bar's dark mode toggle and reset buttons exactly as in ChoiceScreenView
    private void wireTopBar() {
        // Dark mode toggle: flip the local state and reapply the theme.
        topBar.toggleButton.setOnAction(e -> {
            darkMode = !darkMode;
            System.out.println("[DEBUG] Inventory topBar dark mode toggled: " + darkMode);
            applyTheme(darkMode);
        });
        // Reset button: show a confirmation dialog then call reset logic.
        topBar.resetButton.setOnAction(e -> {
            System.out.println("[DEBUG] Inventory topBar reset triggered.");
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Reset Game");
            confirm.setHeaderText("Return to the title screen?");
            confirm.setContentText("Any progress will be lost.");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                System.out.println("[DEBUG] Inventory reset confirmed.");
                // Insert reset logic, e.g., fire a callback to the controller.
                // For example: resetInventory(); or notify MainController to update view.
            }
        });
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
                "-fx-background-color: " + (darkMode ? "#333333" : "#f2f2f2") + ";" +
                "-fx-border-color: " + (darkMode ? "#777777" : "#cccccc") + ";" +
                "-fx-border-width: 1px;"
        );
        Label invTitle = new Label("Inventory");
        invTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: "
                + (darkMode ? "white" : "black") + ";");
        inventoryBox.getChildren().add(invTitle);
        
        for (ItemType type : inventory.keySet()) {
            Label catLabel = new Label("• " + type.name() + ":");
            catLabel.setStyle("-fx-text-fill: " + (darkMode ? "white" : "black") + ";");
            VBox itemList = new VBox(5);
            List<InventoryItem> items = inventory.get(type);
            if (items == null || items.isEmpty()) {
                Label emptyLabel = new Label("- none");
                emptyLabel.setStyle("-fx-text-fill: " + (darkMode ? "white" : "black") + ";");
                itemList.getChildren().add(emptyLabel);
            } else {
                for (InventoryItem item : items) {
                    Label itemLabel = new Label("- " + item.getName());
                    itemLabel.setStyle("-fx-text-fill: " + (darkMode ? "white" : "black") + ";");
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
        // Update the top bar, prompt, and buttons.
        topBar.applyTheme(darkMode);
        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + (darkMode ? "white" : "black") + ";");
        
        String btnStyle = darkMode
                ? "-fx-background-color: #444444; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;"
                : "-fx-background-color: #eeeeee; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10 20;";
        healthHeavyButton.setStyle(btnStyle);
        attackHeavyButton.setStyle(btnStyle);
        balancedButton.setStyle(btnStyle);
        
        // Update the inventory sidebar style.
        inventorySidebar.setStyle(
                "-fx-background-color: " + (darkMode ? "#333333" : "#f2f2f2") + ";" +
                "-fx-border-color: " + (darkMode ? "#777777" : "#cccccc") + ";" +
                "-fx-border-width: 1px;"
        );
        
        // Update overall view background.
        setBackground(new Background(new BackgroundFill(
                darkMode ? Color.web("#2e2e2e") : Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    }
}