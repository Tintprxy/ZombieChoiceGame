package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import static view.Theme.*;

import model.SaveData;
import model.SaveManager;
import model.SceneLoader;

public class StoryTurnstileView extends BorderPane {
    private static final String BADGE_ID = "completed-badge";

    private VBox story1Box;
    private VBox story2Box;
    private TopBarView topBar;
    private HBox container;
    private ScrollPane scrollPane;

    private Button story1Button;
    private Button story2Button;

    private final int activeSaveSlot; 

    public StoryTurnstileView(boolean darkMode, int activeSaveSlot) {
        this.activeSaveSlot = activeSaveSlot;

        topBar = new TopBarView();
        topBar.applyTheme(darkMode);
        setTop(topBar);

        setPrefSize(800, 600);
        setPadding(new Insets(20));

        story1Box = createStoryBox("file:imgs/armoredCarImg.jpg", "Drive", "A fast route to survival", "Play Drive", /*index*/1);
        story2Box = createStoryBox("file:imgs/walkingImg.jpg", "Walk",  "Steady and safe on foot",   "Play Walk",  /*index*/2);

        decorateCompletedBadge(story1Box, "src/data/drive_story1.json");
        decorateCompletedBadge(story2Box, "src/data/walk_story2.json");

        container = new HBox(50, story1Box, story2Box);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setPrefSize(800, 600);

        scrollPane = new ScrollPane(container);
        scrollPane.setPrefViewportWidth(800);
        scrollPane.setPrefViewportHeight(600);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            double deltaY = e.getDeltaY();
            scrollPane.setHvalue(scrollPane.getHvalue() - deltaY / container.getWidth());
            e.consume();
        });

        setCenter(scrollPane);
        applyTheme(darkMode);
    }

    public StoryTurnstileView(boolean darkMode) {
        this(darkMode, 0);
    }

    public Button getStory1Button() { return story1Button; }
    public Button getStory2Button() { return story2Button; }

    private VBox createStoryBox(String imagePath, String title, String subtitle, String buttonText, int index) {
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(imagePath);
            imageView.setImage(image);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("[DEBUG] Failed to load image: " + imagePath);
        }

        Label titleLabel = new Label(title);
        Label subtitleLabel = new Label(subtitle);
        Button button = new Button(buttonText);

        if (index == 1) story1Button = button;
        if (index == 2) story2Button = button;

        VBox box = new VBox(10, imageView, titleLabel, subtitleLabel, button);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #cccccc; -fx-border-color: #888888; -fx-border-width: 2px;");
        return box;
    }

    private void decorateCompletedBadge(VBox storyCard, String storyJsonPath) {
        storyCard.getChildren().removeIf(n -> BADGE_ID.equals(n.getId()));

        if (!isStoryCompleted(storyJsonPath)) return;

        HBox row = new HBox();
        row.setId(BADGE_ID);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label("✓");
        badge.setStyle(
            "-fx-background-color: #16a34a;" +  
            "-fx-text-fill: white;" +
            "-fx-font-weight: 800;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 2 6;" +
            "-fx-font-size: 12px;" +
            "-fx-opacity: 0.95;"
        );

        row.getChildren().addAll(spacer, badge);
        row.setPickOnBounds(false);
        storyCard.getChildren().add(0, row);
    }

    private boolean isStoryCompleted(String storyJsonPath) {
        if (activeSaveSlot <= 0) return false;

        var opt = SaveManager.load(activeSaveSlot);
        if (opt.isEmpty()) return false;

        SaveData data = opt.get();
        if (data.completedWinSceneIds == null || data.completedWinSceneIds.isEmpty()) return false;

        try {
            SceneLoader loader = new SceneLoader(storyJsonPath);
            var sceneIds = loader.getScenes().keySet();
            for (String winId : data.completedWinSceneIds) {
                if (sceneIds.contains(winId)) return true;
            }
        } catch (Exception ex) {
            System.err.println("[DEBUG] Failed to load story for completion check: " + storyJsonPath + " -> " + ex.getMessage());
        }
        return false;
    }

    public void applyTheme(boolean darkMode) {
        String bgColor = darkMode ? DARK_BG : LIGHT_BG;
        String boxColor = darkMode ? DARK_BOX : LIGHT_BOX;
        String borderColor = darkMode ? DARK_BORDER : LIGHT_BORDER;
        String titleColor = darkMode ? DARK_TEXT : LIGHT_TEXT;
        String containerColor = darkMode ? DARK_CONTAINER : LIGHT_CONTAINER;
        String buttonStyle = String.format(
            "-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 5px;",
            darkMode ? DARK_BUTTON_BG : LIGHT_BUTTON_BG,
            darkMode ? DARK_BUTTON_TEXT : LIGHT_BUTTON_TEXT
        );
        String textColor = darkMode ? DARK_TEXT : LIGHT_TEXT;

        setBackground(new Background(new BackgroundFill(Color.web(bgColor), CornerRadii.EMPTY, Insets.EMPTY)));

        String boxStyle = String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2px;",
            boxColor, borderColor
        );

        java.util.function.Consumer<VBox> styleStoryBox = box -> {
            box.setStyle(boxStyle);
            if (box.getChildren().size() >= 4) {
                if (box.getChildren().get(1) instanceof Label titleLabel) {
                    titleLabel.setTextFill(Color.web(titleColor));
                }
                if (box.getChildren().get(2) instanceof Label subtitleLabel) {
                    subtitleLabel.setTextFill(Color.web(textColor));
                }
                if (box.getChildren().get(3) instanceof Button btn) {
                    btn.setStyle(buttonStyle);
                }
            }
        };

        if (story1Box != null) styleStoryBox.accept(story1Box);
        if (story2Box != null) styleStoryBox.accept(story2Box);

        if (story1Button != null) Theme.applyButtonStyle(story1Button, darkMode);
        if (story2Button != null) Theme.applyButtonStyle(story2Button, darkMode);

        if (topBar != null) topBar.applyTheme(darkMode);
        if (container != null) container.setBackground(new Background(new BackgroundFill(Color.web(containerColor), CornerRadii.EMPTY, Insets.EMPTY)));
        if (scrollPane != null) scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
    }

    public TopBarView getTopBar() { return topBar; }
    public VBox getStory1Box() { return story1Box; }
    public VBox getStory2Box() { return story2Box; }
}