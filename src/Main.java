import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
public class Main extends Application {
    private boolean isDarkMode = false;
    private Stage primaryStage;
    private VBox contentArea;
    private BorderPane root;
    private Scene scene;
    private Button toggleBtn = new Button("Dark Mode");

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Root BorderPane
        root = new BorderPane();

        // Main content area
        contentArea = new VBox(20);
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setPadding(new Insets(10, 20, 20, 20));
        root.setCenter(contentArea);

        // Top bar with dark mode button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.getChildren().add(toggleBtn);
        topBar.setPadding(new Insets(0, 0, 0, 0)); 
        root.setTop(topBar);

        // Toggle action
        toggleBtn.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            applyTheme();
        });

    // Scene
    scene = new Scene(root, 500, 300);
    stage.setScene(scene);
    stage.setTitle("Zombie Choice Game");

    applyTheme();
    showStartMenu();
    stage.show();
}

    private void showStartMenu() {
        contentArea.getChildren().clear();

        // Load and display zombie image (make sure file exists in your project)
        File imgFile = new File("imgs/titleImg.jpg");
        System.out.println("Image exists? " + imgFile.exists() + " | Path: " + imgFile.getAbsolutePath()); // debug: does image exist?
        Image titleImg = new Image(imgFile.toURI().toString()); 
        ImageView imageView = new ImageView(titleImg);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Title and credit
        Text title = new Text("Zombie Choice Game");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Text credit = new Text("Created by Tyler J. Thomas");
        credit.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        // Instructions
        Text instructions = new Text("Make your choices wisely.\nEvery decision affects your survival.\nCan you make it out alive?");
        instructions.setWrappingWidth(400);
        instructions.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");

        // Start Button
        Button startBtn = new Button("Start Game");
        startBtn.setOnAction(e -> showGameIntro());

        // Add all to content area
        contentArea.getChildren().addAll(imageView , title, credit, instructions, startBtn);
        contentArea.setSpacing(15);
        contentArea.setAlignment(Pos.TOP_CENTER);

        // Apply dark/light mode styles
        applyContentStyle(title, startBtn);
        applyContentStyle(credit);
        applyContentStyle(instructions);
    }

    private void showGameIntro() {
        contentArea.getChildren().clear();

        Text gameText = new Text("You wake up in a dark hospital room...\nA growl echoes from the hallway.");
        Button nextBtn = new Button("Next");

        nextBtn.setOnAction(e -> {
            // Here you can load the next scene or decision point
            gameText.setText("A door creaks open... something is coming.");
            nextBtn.setText("Continue...");
        });

        contentArea.getChildren().addAll(gameText, nextBtn);
        applyContentStyle(gameText, nextBtn);
    }

    private void applyTheme() {
    Color bg = isDarkMode ? Color.web("#2e2e2e") : Color.WHITE;
    root.setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));

    toggleBtn.setText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");

    // Style content area
    contentArea.getChildren().forEach(node -> {
        if (node instanceof Text) {
            ((Text) node).setFill(isDarkMode ? Color.LIGHTGRAY : Color.BLACK);
        } else if (node instanceof Button && node != toggleBtn) {
            Button b = (Button) node;
            b.setStyle(isDarkMode
                    ? "-fx-background-color: #444444; -fx-text-fill: white;"
                    : "-fx-background-color: #eeeeee; -fx-text-fill: black;");
        }
    });
}

    private void applyContentStyle(Text text, Button... buttons) {
        text.setFill(isDarkMode ? Color.LIGHTGRAY : Color.BLACK);
        for (Button b : buttons) {
            b.setStyle(isDarkMode
                    ? "-fx-background-color: #444444; -fx-text-fill: white;"
                    : "-fx-background-color: #eeeeee; -fx-text-fill: black;");
        }
    }
    public static void main(String[] args) {
        launch();
    }
}