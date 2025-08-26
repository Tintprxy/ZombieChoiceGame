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

public class StoryTurnstileView extends BorderPane {
    private final VBox story1Box;
    private final VBox story2Box;
    private final TopBarView topBar;

    public StoryTurnstileView(boolean darkMode) {
        // Initialize local top bar
        topBar = new TopBarView();
        topBar.applyTheme(darkMode);
        setTop(topBar);

        setPadding(new Insets(20));

        // Create story boxes
        story1Box = createStoryBox("file:imgs/story1.png", "Story 1", "Play Story 1");
        story2Box = createStoryBox("file:imgs/story2.png", "Story 2", "Play Story 2");

        HBox container = new HBox(50, story1Box, story2Box);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(container);
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
        Color bgColor = darkMode ? Color.web("#2e2e2e") : Color.WHITE;
        setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
        
        String boxStyle = darkMode
                ? "-fx-background-color: #444444; -fx-border-color: #777777; -fx-border-width: 2px; -fx-text-fill: white;"
                : "-fx-background-color: #cccccc; -fx-border-color: #888888; -fx-border-width: 2px; -fx-text-fill: black;";
        // Update the style of the story boxes.
        if (story1Box != null) {
            story1Box.setStyle(boxStyle);
        }
        if (story2Box != null) {
            story2Box.setStyle(boxStyle);
        }
        // Apply theme to the top bar.
        if (topBar != null) {
            topBar.applyTheme(darkMode);
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