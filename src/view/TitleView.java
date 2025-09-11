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
import java.io.File;
import java.util.function.IntConsumer;


import static view.Theme.*;

public class TitleView extends BorderPane {
    public final Button startButton = new Button("Start Game");
    public final Button loadButton = new Button("Load Game"); 
    public final Button instructionsButton = new Button("Instructions");
    public final TopBarView topBar = new TopBarView();

    private final VBox contentBox = new VBox(15);
    private final Text titleText = new Text("Zombie Choice Game");
    private final Text creditText = new Text("Created by Tyler J. Thomas");
    private final Text instructionText = new Text("Make your choices wisely.\nEvery decision affects your survival.\nCan you make it out alive?");

    private Button winningAlbumButton;
    private IntConsumer winningAlbumHandler; 

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
        contentBox.getChildren().addAll(
            titleText, creditText, instructionText,
            startButton,
            loadButton,           
            instructionsButton
        );

        winningAlbumButton = new Button("Winning Photo Album");
        winningAlbumButton.setOnAction(e -> {
            java.util.List<String> options = new java.util.ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                String label = "Slot " + i + " - " + model.SaveManager.peekPlayerName(i).orElse("Empty");
                options.add(label);
            }
            javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>(options.get(0), options);
            dialog.setTitle("Select Save Slot");
            dialog.setHeaderText("Choose a save slot to view its Winning Photo Album:");
            dialog.setContentText("Save Slot:");

            dialog.showAndWait().ifPresent(selected -> {
                int slot = options.indexOf(selected) + 1;
                if (winningAlbumHandler != null) {
                    winningAlbumHandler.accept(slot);
                }
            });
        });

        winningAlbumButton.setFocusTraversable(false);
        contentBox.getChildren().add(winningAlbumButton);

        setTop(topBar);

        setCenter(contentBox);
    }

    public void applyTheme(boolean isDarkMode) {
        setBackground(new Background(new BackgroundFill(
                Color.web(isDarkMode ? DARK_BG : LIGHT_BG),
                CornerRadii.EMPTY, Insets.EMPTY)));

        topBar.applyTheme(isDarkMode);

        Color textColor = isDarkMode ? Color.WHITE : Color.web("#1f2937");

        titleText.setFill(textColor);
        creditText.setFill(textColor);
        instructionText.setFill(textColor);

        Theme.applyButtonStyle(startButton, isDarkMode);
        Theme.applyButtonStyle(loadButton, isDarkMode);
        Theme.applyButtonStyle(instructionsButton, isDarkMode);
        Theme.applyButtonStyle(winningAlbumButton, isDarkMode);
    }

    public void setWinningAlbumHandler(IntConsumer handler) {
        this.winningAlbumHandler = handler;
    }
}