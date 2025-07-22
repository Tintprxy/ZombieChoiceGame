import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class Main extends Application {
    private boolean isDarkMode = false;
    private Stage primaryStage;
    private VBox contentArea;
    private BorderPane root;
    private Scene scene;

    private Button toggleBtn = new Button("Switch to Dark Mode");

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Outer layout
        root = new BorderPane();
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-alignment: center;");

        // Top bar: Dark mode toggle
        HBox topBar = new HBox(toggleBtn);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-alignment: top-right;");

        toggleBtn.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            applyTheme();
        });

        root.setTop(topBar);
        root.setCenter(contentArea);

        scene = new Scene(root, 500, 300);
        stage.setScene(scene);
        stage.setTitle("Zombie Choice Game");
        applyTheme();
        showStartMenu();
        stage.show();
    }

    private void showStartMenu() {
        contentArea.getChildren().clear();

        Text title = new Text("🧟 Zombie Choice Game");
        Button startBtn = new Button("Start Game");

        startBtn.setOnAction(e -> showGameIntro());

        contentArea.getChildren().addAll(title, startBtn);
        applyContentStyle(title, startBtn);
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

        // Update existing content area styling
        if (!contentArea.getChildren().isEmpty()) {
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
