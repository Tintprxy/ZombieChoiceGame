package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.GameChoice;
import model.InventoryItem;
import model.ItemType;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import model.GameModel;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Region;
import java.util.ArrayList;

public class ChoiceScreenView extends BorderPane {
    private static final int CHOICE_IMG_WIDTH = 160;
    private final TopBarView topBar = new TopBarView();
    private Label healthLabel;
    private ProgressBar healthBar;
    private Label promptLabel;
    private int health;
    private String promptText;
    private List<GameChoice> choices;
    private boolean darkMode;
    private Map<ItemType, List<InventoryItem>> inventory;
    private Consumer<GameChoice> onChoiceSelected;
    private Runnable onToggleTheme;
    private Runnable onReset;         
    private Consumer<InventoryItem> onConsumeItem;
    private GameModel model; 
    private final List<Button> choiceButtons = new ArrayList<>();

    public ChoiceScreenView(
            int health,
            String prompt,
            List<GameChoice> choices,
            boolean darkMode,
            Map<ItemType, List<InventoryItem>> inventory,
            GameModel model,
            Consumer<GameChoice> onChoice,
            Runnable onToggleTheme,
            Runnable onReset,
            Runnable onChooseStory,
            Consumer<InventoryItem> onConsumeItem
    ) {
        this.health = health;
        this.promptText = prompt;
        this.choices = choices;
        this.darkMode = darkMode;
        this.inventory = inventory;
        this.model = model;
        this.onChoiceSelected = onChoice;
        this.onToggleTheme = onToggleTheme;
        this.onReset = onReset;        
        this.onConsumeItem = onConsumeItem;

        topBar.toggleButton.setOnAction(e -> onToggleTheme.run());
        topBar.resetButton.setOnAction(e -> onReset.run());
        topBar.showChooseStoryButton(true);
        topBar.chooseStoryButton.setOnAction(e -> onChooseStory.run());

        Label left = new Label("Current Scene: " + "");
        left.setStyle("-fx-font-size:16px;");
        topBar.setLeft(left);

        VBox inventoryView = buildInventoryUI(inventory, darkMode);
        inventoryView.setPrefWidth(300); 
        inventoryView.setMinWidth(300);
        inventoryView.setMaxWidth(400);

        inventoryView.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                Theme.applyButtonStyle(button, darkMode);
            }
        });

        healthLabel = new Label("Health: " + health);
        healthBar = new ProgressBar(health / 100.0);
        healthBar.setPrefWidth(300);
        healthBar.setStyle("-fx-accent: green;");
        healthBar.setTooltip(new Tooltip("Current Health: " + health));

        promptLabel = new Label(promptText);
        promptLabel.setWrapText(true);

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
            imageView.setFitWidth(CHOICE_IMG_WIDTH);
            imageView.setPreserveRatio(true);

            Button button = new Button(choice.getLabel());

            if (choice.getLabel().toLowerCase().contains("use antidote") || choice.getLabel().toLowerCase().contains("administer antidote")) {
                boolean hasAntidote = false;
                List<InventoryItem> keyItems = inventory.get(ItemType.KEY_ITEM);
                if (keyItems != null) {
                    hasAntidote = keyItems.stream().anyMatch(
                        item -> item.getName().equalsIgnoreCase("Antidote")
                    );
                }
                if (!hasAntidote && !model.isAntidoteUsed()) {
                    button.setDisable(true);
                    button.setStyle("-fx-opacity: 0.5;");
                }
            }
            
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

            styleChoiceButton(button, darkMode);
            choiceButtons.add(button);

            VBox column = new VBox(8, imageView, button);
            column.setAlignment(Pos.CENTER);
            choiceRow.getChildren().add(column);
        }

        VBox centerContent = new VBox(20, healthLabel, healthBar, promptLabel, choiceRow);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setPadding(new Insets(20)); 

        setTop(topBar);
        setCenter(centerContent);
        setLeft(inventoryView);
        BorderPane.setMargin(inventoryView, new Insets(20, 10, 20, 20));

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

                    if (item.isConsumable()) {
                        itemDesc.append(" (Restores: ").append(item.getHealthRestore()).append(" HP)");
                    }
                    if (item.isWeapon()) {
                        itemDesc.append(" (Durability: ").append(item.getDurability())
                                .append(", Power: ").append(item.getPower()).append(")");
                    }

                    Label itemLabel = new Label(itemDesc.toString());
                    itemLabel.setStyle("-fx-text-fill: " + Theme.getTextColor(darkMode) + ";");
                    itemRow.getChildren().add(itemLabel);

                    if (item.isConsumable()) {
                        Button consumeBtn = new Button("Consume");
                        consumeBtn.setOnAction(e -> {
                            if (onConsumeItem != null) {
                                onConsumeItem.accept(item);
                            }
                        });
                        Theme.applyButtonStyle(consumeBtn, darkMode);
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

    private void styleChoiceButton(Button b, boolean darkMode) {
        Theme.applyButtonStyle(b, darkMode);

        b.setWrapText(false);
        b.setTextOverrun(OverrunStyle.CLIP);
        b.setFont(Font.font(13));

        b.setMinWidth(Region.USE_PREF_SIZE);
        b.setPrefWidth(Region.USE_COMPUTED_SIZE);
        b.setMaxWidth(Region.USE_PREF_SIZE);
    
        Runnable resize = () -> Theme.sizeToText(b, 32 + 10, 360); 
        Platform.runLater(resize);
        b.textProperty().addListener((obs, o, n) -> resize.run());
    }

    public List<Button> getChoiceButtons() {
        return choiceButtons;
    }
}
