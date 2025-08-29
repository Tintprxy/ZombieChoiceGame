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

public class StoryTurnstileView extends BorderPane {
    private final VBox story1Box;
    private final VBox story2Box;
    private final TopBarView topBar;
    private final HBox container;
    private final ScrollPane scrollPane;

    public StoryTurnstileView(boolean darkMode) {
        // Initialize local top bar
        topBar = new TopBarView();
        topBar.applyTheme(darkMode);
        setTop(topBar);

        setPadding(new Insets(20));

        // Create story boxes
        story1Box = createStoryBox("file:imgs/healthHeavyImg.jpg", "Story 1", "Play Story 1");
        story2Box = createStoryBox("file:imgs/attackHeavyImg.jpg", "Story 2", "Play Story 2");

        container = new HBox(50, story1Box, story2Box);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));

        scrollPane = new ScrollPane(container);
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

        // Apply initial theme
        applyTheme(darkMode);
    }

    // Creates a VBox with an image, a title, and a button.
    private VBox createStoryBox(String imagePath, String title, String buttonText) {
        // Load the image.
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(imagePath);
            imageView.setImage(image);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("[DEBUG] Failed to load image: " + imagePath);
        }
        // Create the title and button.
        Label titleLabel = new Label(title);
        Button button = new Button(buttonText);
        // Arrange the elements in a VBox.
        VBox box = new VBox(10, imageView, titleLabel, button);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        // Set a default light-mode style.
        box.setStyle("-fx-background-color: #cccccc; -fx-border-color: #888888; -fx-border-width: 2px;");
        return box;
    }

    // Applies dark or light theme to the view.
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
            if (box.getChildren().size() >= 3) {
                if (box.getChildren().get(1) instanceof Label titleLabel) {
                    titleLabel.setTextFill(Color.web(titleColor));
                }
                if (box.getChildren().get(2) instanceof Button button) {
                    button.setStyle(buttonStyle);
                }
            }
        };

        if (story1Box != null) {
            styleStoryBox.accept(story1Box);
            story1Box.getChildren().filtered(n -> n instanceof Label).forEach(n -> {
                ((Label)n).setStyle("-fx-text-fill: " + textColor);
            });
        }
        if (story2Box != null) {
            styleStoryBox.accept(story2Box);
            story2Box.getChildren().filtered(n -> n instanceof Label).forEach(n -> {
                ((Label)n).setStyle("-fx-text-fill: " + textColor);
            });
        }

        if (topBar != null) {
            topBar.applyTheme(darkMode);
        }
        if (container != null) {
            container.setBackground(new Background(new BackgroundFill(Color.web(containerColor), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        if (scrollPane != null) {
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        }
    }

    public TopBarView getTopBar() {
        return topBar;
    }

    // Getters for wiring story selection in your controller.
    public VBox getStory1Box() {
        return story1Box;
    }

    public VBox getStory2Box() {
        return story2Box;
    }
}