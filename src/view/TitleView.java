package view;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.Node;
import java.io.File;

import static view.Theme.*;

public class TitleView extends BorderPane {
    public final Button startButton = new Button("Start Game");
    public final Button instructionsButton = new Button("Instructions");
    public final TopBarView topBar = new TopBarView();

    private final VBox contentBox = new VBox(15);
    private final Text titleText = new Text("Zombie Choice Game");
    private final Text creditText = new Text("Created by Tyler J. Thomas");
    private final Text instructionText = new Text("Make your choices wisely.\nEvery decision affects your survival.\nCan you make it out alive?");

    public TitleView() {
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(20));

        File imgFile = new File("imgs/titleImg.jpg");
        ImageView imageView = null;
        if (imgFile.exists()) {
            Image titleImg = new Image(imgFile.toURI().toString());
            imageView = new ImageView(titleImg);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
        }

        titleText.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        creditText.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
       instructionText.setStyle("""
        -fx-font-size: 14px;
        -fx-text-alignment: center;
        """);
        instructionText.setWrappingWidth(400);
        instructionText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        if (imageView != null) contentBox.getChildren().add(imageView);
        contentBox.getChildren().addAll(titleText, creditText, instructionText, startButton, instructionsButton);

        // Add padding to the top bar for consistency
        VBox topBarContainer = new VBox(topBar);
        topBarContainer.setPadding(new Insets(20, 30, 0, 0)); // top, right, bottom, left
        topBarContainer.setAlignment(Pos.TOP_RIGHT);

        setTop(topBarContainer);
        setCenter(contentBox);
    }

    public void applyTheme(boolean isDarkMode) {
        setBackground(new Background(new BackgroundFill(
                Color.web(isDarkMode ? DARK_BG : LIGHT_BG),
                CornerRadii.EMPTY, Insets.EMPTY)));

        topBar.applyTheme(isDarkMode);

        for (Node node : contentBox.getChildren()) {
            if (node instanceof Text text) {
                text.setFill(Color.web(isDarkMode ? DARK_TEXT : LIGHT_TEXT));
            } else if (node instanceof Button button) {
                button.setStyle(String.format(
                    "-fx-background-color: %s; -fx-text-fill: %s;",
                    isDarkMode ? DARK_BUTTON_BG : LIGHT_BUTTON_BG,
                    isDarkMode ? DARK_BUTTON_TEXT : LIGHT_BUTTON_TEXT
                ));
            }
        }
    }
}