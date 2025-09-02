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
        topBar = new TopBarView();
        topBar.applyTheme(darkMode);
        setTop(topBar);

        // Set a fixed size for this view.
        setPrefSize(800, 600); // adjust width and height as needed

        setPadding(new Insets(20));

        story1Box = createStoryBox("file:imgs/armoredCarImg.jpg", "Drive", "A fast route to survival", "Play Drive");
        story2Box = createStoryBox("file:imgs/walkingImg.jpg", "Walk", "Steady and safe on foot", "Play Walk");

        container = new HBox(50, story1Box, story2Box);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));

        // You can set a fixed preferred size for the container if desired.
        container.setPrefSize(800, 600);

        scrollPane = new ScrollPane(container);
        // Set viewport size for the internal ScrollPane.
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

    private VBox createStoryBox(String imagePath, String title, String subtitle, String buttonText) {
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

        VBox box = new VBox(10, imageView, titleLabel, subtitleLabel, button);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #cccccc; -fx-border-color: #888888; -fx-border-width: 2px;");
        return box;
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
                if (box.getChildren().get(3) instanceof Button button) {
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

    public VBox getStory1Box() {
        return story1Box;
    }

    public VBox getStory2Box() {
        return story2Box;
    }
}