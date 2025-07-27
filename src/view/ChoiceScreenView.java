package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.GameChoice;
import model.InventoryItem;
import model.ItemType;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChoiceScreenView extends VBox {
    private Label healthLabel;
    private ProgressBar healthBar;
    private Label promptLabel;
    private TopBarView topBar = new TopBarView();
    private int health;
    private String promptText;
    private List<GameChoice> choices;
    private boolean darkMode;
    private Map<ItemType, List<InventoryItem>> inventory;
    private Consumer<GameChoice> onChoiceSelected;
    private Runnable onToggleTheme;
    private Consumer<InventoryItem> onConsumeItem;

    public ChoiceScreenView(
        int health,
        String promptText,
        List<GameChoice> choices,
        boolean darkMode,
        Map<ItemType, List<InventoryItem>> inventory,
        Consumer<GameChoice> onChoiceSelected,
        Runnable onToggleTheme,
        Consumer<InventoryItem> onConsumeItem 
    ) {
        this.health = health;
        this.promptText = promptText;
        this.choices = choices;
        this.darkMode = darkMode;
        this.inventory = inventory;
        this.onChoiceSelected = onChoiceSelected;
        this.onToggleTheme = onToggleTheme;
        this.onConsumeItem = onConsumeItem;

        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);

        // Top bar
        topBar.toggleButton.setOnAction(e -> onToggleTheme.run());

        // After creating inventoryView in the constructor:
        VBox inventoryView = buildInventoryUI(inventory, darkMode);
        inventoryView.setPrefWidth(300); 
        inventoryView.setMinWidth(300);
        inventoryView.setMaxWidth(400);
        //style the consume buttons with is dark mode
        inventoryView.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                Theme.applyButtonStyle(button, darkMode);
            }
        });

        // Health and prompt
        healthLabel = new Label("Health: " + health);
        healthBar = new ProgressBar(health / 100.0);
        healthBar.setPrefWidth(300);
        healthBar.setStyle("-fx-accent: green;");
        healthBar.setTooltip(new Tooltip("Current Health: " + health));

        promptLabel = new Label(promptText);
        promptLabel.setWrapText(true);

        // Choices
        HBox choiceRow = new HBox(20);
        choiceRow.setAlignment(Pos.CENTER);

        for (GameChoice choice : choices) {
            if ("Fight".equalsIgnoreCase(choice.getLabel())) {
                System.out.println("[DEBUG] Creating button for choice: " + choice.getLabel() +
                    ", possible nextIds: fight_result_win_1 / fight_result_lose_1" +
                    ", imagePath: " + choice.getImagePath());
            } else {
                System.out.println("[DEBUG] Creating button for choice: " + choice.getLabel() +
                    ", nextId: " + choice.getNextId() +
                    ", imagePath: " + choice.getImagePath());
            }
            Image img = new Image("file:" + choice.getImagePath());
            if (img.isError()) {
                System.out.println("[DEBUG] Failed to load image: " + choice.getImagePath());
            }
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            Button button = new Button(choice.getLabel());
            button.setOnAction(e -> {
                if ("Fight".equalsIgnoreCase(choice.getLabel())) {
                    System.out.println("[DEBUG] Button clicked: " + choice.getLabel() +
                        " (combat resolution will determine next scene)");
                } else {
                    System.out.println("[DEBUG] Button clicked: " + choice.getLabel() +
                        ", nextId: " + choice.getNextId());
                }
                onChoiceSelected.accept(choice);
            });
            Theme.applyButtonStyle(button, darkMode);

            VBox column = new VBox(10, imageView, button);
            column.setAlignment(Pos.CENTER);
            choiceRow.getChildren().add(column);
        }

        // Right side content
        VBox rightSide = new VBox(20, healthLabel, healthBar, promptLabel, choiceRow);
        rightSide.setAlignment(Pos.TOP_CENTER);

        // Combine both sides
        HBox mainContent = new HBox(40, inventoryView, rightSide);
        mainContent.setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(topBar, mainContent);
        applyTheme(darkMode);
    }

    public int getHealth() {
        return health;
    }

    public String getPromptText() {
        return promptText;
    }

    public List<GameChoice> getChoices() {
        return choices;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public Map<ItemType, List<InventoryItem>> getInventory() {
        return inventory;
    }

    public Consumer<GameChoice> getOnChoiceSelected() {
        return onChoiceSelected;
    }

    public Runnable getOnToggleTheme() {
        return onToggleTheme;
    }

    public void applyTheme(boolean darkMode) {
        setStyle("-fx-background-color: " + Theme.getBodyBackground(darkMode));
        promptLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode));
        healthLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode));
        topBar.applyTheme(darkMode);
    }

    public TopBarView getTopBar() {
        return topBar;
    }

    public VBox getLayout() {
        return this;
    }

    private VBox buildInventoryUI(Map<ItemType, List<InventoryItem>> inventory, boolean darkMode) {
        VBox inventoryBox = new VBox(10);
        inventoryBox.setAlignment(Pos.CENTER_LEFT);
        inventoryBox.setPadding(new Insets(10));
        inventoryBox.setMaxWidth(250);
        inventoryBox.setStyle(
            "-fx-background-color: " + Theme.getInventoryBackground(darkMode) + ";" +
            "-fx-border-color: " + Theme.getBorderColor(darkMode) + ";" +
            "-fx-border-width: 1px;"
        );

        Label invTitle = new Label("Inventory");
        invTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + Theme.getTextColor(darkMode) + ";");
        inventoryBox.getChildren().add(invTitle);

        for (ItemType type : ItemType.values()) {
            Label categoryLabel = new Label("• " + type.name() + ":");
            categoryLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode) + ";");

            VBox itemList = new VBox(5);
            List<InventoryItem> items = inventory.get(type);
            if (items == null || items.isEmpty()) {
                Label emptyLabel = new Label("- none");
                emptyLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode) + ";");
                itemList.getChildren().add(emptyLabel);
            } else {
                for (InventoryItem item : items) {
                    HBox itemRow = new HBox(5);
                    StringBuilder itemDesc = new StringBuilder("- " + item.getName());

                    // Show healthRestore for consumables
                    if (item.isConsumable()) {
                        itemDesc.append(" (Restores: ").append(item.getHealthRestore()).append(" HP)");
                    }
                    // Show durability and power for weapons
                    if (item.isWeapon()) {
                        itemDesc.append(" (Durability: ").append(item.getDurability())
                                .append(", Power: ").append(item.getPower()).append(")");
                    }

                    Label itemLabel = new Label(itemDesc.toString());
                    itemLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode) + ";");
                    itemRow.getChildren().add(itemLabel);

                    // Add a Consume button for consumables
                    if (item.isConsumable()) {
                        Button consumeBtn = new Button("Consume");
                        consumeBtn.setOnAction(e -> {
                            if (onConsumeItem != null) {
                                onConsumeItem.accept(item);
                            }
                        });
                        Theme.applyButtonStyle(consumeBtn, darkMode); // <-- Apply theme here
                        itemRow.getChildren().add(consumeBtn);
                    }
                    itemList.getChildren().add(itemRow);
                }
            }
            VBox section = new VBox(3, categoryLabel, itemList);
            inventoryBox.getChildren().add(section);
        }
        return inventoryBox;
    }
}