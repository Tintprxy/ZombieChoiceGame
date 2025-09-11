package view;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import model.SaveData;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.Scene;
import javafx.beans.value.ChangeListener;

public class WinningPhotoAlbumView extends BorderPane {
    private static final double WHEEL_SCROLL_SENSITIVITY = 12.0;

    private final EventHandler<ScrollEvent> windowScrollHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent e) {
            if (scrollPane == null || photoRow == null) return;
            double delta = -(e.getDeltaY() != 0 ? e.getDeltaY() : e.getDeltaX());
            double contentWidth = Math.max(photoRow.getWidth(), 1.0);
            double change = (delta * WHEEL_SCROLL_SENSITIVITY) / contentWidth;
            double newH = scrollPane.getHvalue() + change;
            if (newH < 0) newH = 0;
            if (newH > 1) newH = 1;
            scrollPane.setHvalue(newH);
            e.consume();
        }
    };

    private final ChangeListener<Scene> sceneAttachListener = (obs, oldScene, newScene) -> {
        if (oldScene != null) oldScene.removeEventFilter(ScrollEvent.SCROLL, windowScrollHandler);
        if (newScene != null) newScene.addEventFilter(ScrollEvent.SCROLL, windowScrollHandler);
    };

    private Map<String, String> winningScenesImageMap = new HashMap<>();

    private final TopBarView topBar = new TopBarView();

    private Label playerLabel;
    private VBox unlockedBox;
    private VBox lockedBox;
    private Button backButton;

    private ScrollPane scrollPane;

    private final List<ImageView> lockedImageViews = new ArrayList<>();
    private final List<Label> lockedPhotoLabels = new ArrayList<>();
    private final List<ImageView> unlockedImageViews = new ArrayList<>();
    private final List<Label> unlockedPhotoLabels = new ArrayList<>();

    private HBox photoRow;
    private final List<VBox> unlockedContainers = new ArrayList<>();
    private final List<VBox> lockedContainers = new ArrayList<>();

    private final Map<String, String> winningTitlesMap = new HashMap<>();
    private final Map<String, Label> photoLabelById = new HashMap<>();

    public WinningPhotoAlbumView(SaveData saveData, Runnable onBackPressed) {
        sceneProperty().addListener(sceneAttachListener);

        this.playerLabel = new Label("Player: " + (saveData.playerName != null ? saveData.playerName : "Unknown"));
        playerLabel.setStyle("-fx-font-size: 16px;");

        topBar.setLeft(playerLabel);
        topBar.showChooseStoryButton(false);
        topBar.applyTheme(saveData != null && saveData.darkMode);

        setTop(topBar);

        winningScenesImageMap.put("zombie_woman_win", "imgs/zombieWomanWinImg.jpg");
        winningScenesImageMap.put("win_infection_cured", "imgs/infectionCuredWinImg.jpg");
        winningScenesImageMap.put("fight_result_win_4", "imgs/fightTheHoardWinImg.jpg");
        winningScenesImageMap.put("fight_hoard_bitten_win", "imgs/bittenFromBehindImg.jpg");

        unlockedBox = new VBox(10);
        unlockedBox.setPadding(new Insets(10));
        unlockedBox.setAlignment(Pos.TOP_CENTER);
        Label unlockedLabel = new Label("Unlocked");
        unlockedBox.getChildren().add(unlockedLabel);

        lockedBox = new VBox(10);
        lockedBox.setPadding(new Insets(10));
        lockedBox.setAlignment(Pos.TOP_CENTER);
        Label lockedLabel = new Label("Locked");
        lockedBox.getChildren().add(lockedLabel);

        List<String> winningSceneIds = getWinningSceneIdsFromStory("src/data/drive_story1.json");

        List<String> completedWins = (saveData != null && saveData.completedWinSceneIds != null)
                ? saveData.completedWinSceneIds
                : Collections.emptyList();
        Set<String> completedSet = new HashSet<>(completedWins);

        if (winningSceneIds == null || winningSceneIds.isEmpty()) {
            Label noneLabel = new Label("No winning scenes available.");
            noneLabel.setStyle("-fx-font-size:16px; -fx-text-fill: " + Theme.getTextColor(saveData != null && saveData.darkMode) + ";");
            VBox centerBox = new VBox(10, noneLabel);
            centerBox.setAlignment(Pos.CENTER);
            setCenter(centerBox);

            backButton = new Button("Back to Title");
            backButton.setOnAction(e -> onBackPressed.run());
            setBottom(backButton);
            BorderPane.setAlignment(backButton, Pos.CENTER);
            BorderPane.setMargin(backButton, new Insets(10));
            return;
        }
        
        for (String sceneId : winningSceneIds) {
            String imagePath = getImagePathForSceneId(sceneId);
            System.out.println("SceneId: " + sceneId + ", imagePath: " + imagePath);
            Image image;
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    image = new Image(file.toURI().toString());
                } else {
                    image = new Image("file:imgs/defaultImg.jpg");
                }
            } catch (Exception ex) {
                image = new Image("file:imgs/defaultImg.jpg");
            }
            ImageView iv = new ImageView(image);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(true);
            iv.fitHeightProperty().bind(this.heightProperty().multiply(0.65));

            VBox photoContainer = new VBox(5);
            photoContainer.setAlignment(Pos.CENTER);
            Label photoLabel = createDisplayLabelForSceneId(sceneId);
            photoContainer.getChildren().addAll(photoLabel, iv);

            if (completedSet.contains(sceneId)) {
                iv.setEffect(null);
                iv.setOpacity(1.0);
                unlockedImageViews.add(iv);
                unlockedPhotoLabels.add(photoLabel);
                unlockedContainers.add(photoContainer);
            } else {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1.0);
                GaussianBlur heavyBlur = new GaussianBlur(24);
                heavyBlur.setInput(desaturate);
                iv.setEffect(heavyBlur);
                iv.setOpacity(0.28);

                photoLabel.setText("");
                photoLabel.setVisible(false);
                photoLabel.setManaged(false);

                lockedImageViews.add(iv);
                lockedPhotoLabels.add(photoLabel);
                lockedContainers.add(photoContainer);
            }
        }

        photoRow = new HBox(80);
        photoRow.setAlignment(Pos.CENTER_LEFT);
        photoRow.setPadding(new Insets(40, 80, 40, 80));
        photoRow.getChildren().addAll(unlockedContainers);
        if (!unlockedContainers.isEmpty() && !lockedContainers.isEmpty()) {
            Region gap = new Region();
            gap.setPrefWidth(40);
            photoRow.getChildren().add(gap);
        }
        photoRow.getChildren().addAll(lockedContainers);

        this.scrollPane = new ScrollPane(photoRow);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setPannable(true);
        this.scrollPane.setPadding(new Insets(10));
        setCenter(this.scrollPane);

        setFocusTraversable(true);

        backButton = new Button("Back to Title");
        backButton.setOnAction(e -> onBackPressed.run());
        setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);
        BorderPane.setMargin(backButton, new Insets(10));
    }

    public TopBarView getTopBar() {
        return topBar;
    }

    public void applyTheme(boolean darkMode) {
        topBar.applyTheme(darkMode);

        String bodyBg = Theme.getBodyBackground(darkMode);
        setBackground(new Background(new BackgroundFill(Color.web(bodyBg), CornerRadii.EMPTY, Insets.EMPTY)));

        if (playerLabel != null) {
            playerLabel.setStyle("-fx-font-size:16px; -fx-text-fill: " + Theme.getTextColor(darkMode) + ";");
        }

        java.util.function.Consumer<List<VBox>> styleContainers = list -> {
            for (VBox container : list) {
                for (javafx.scene.Node n : container.getChildren()) {
                    if (n instanceof Label lbl) {
                        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.getTextColor(darkMode) + "; -fx-font-size: 20px;");
                    }
                }
                container.setStyle("-fx-background-color: transparent;");
            }
        };
        styleContainers.accept(unlockedContainers);
        styleContainers.accept(lockedContainers);
        
        for (ImageView iv : unlockedImageViews) {
            if (iv != null) {
                iv.setEffect(null);
                iv.setOpacity(1.0);
            }
        }
        for (Label lbl : unlockedPhotoLabels) {
            if (lbl != null) {
                lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.getTextColor(darkMode) + "; -fx-opacity: 1.0; -fx-font-size: 20px;");
            }
        }
        
        for (ImageView iv : lockedImageViews) {
            if (iv != null) {
                ColorAdjust desaturate = new ColorAdjust();
                desaturate.setSaturation(-1.0);
                GaussianBlur heavyBlur = new GaussianBlur(24);
                heavyBlur.setInput(desaturate);
                iv.setEffect(heavyBlur);
                iv.setOpacity(0.28);
            }
        }
        for (Label lbl : lockedPhotoLabels) {
            if (lbl != null) {
                lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.getTextColor(darkMode) + "; -fx-font-size: 20px;");
                lbl.setOpacity(0.35);
                lbl.setEffect(new GaussianBlur(12));
            }
        }

        if (backButton != null) Theme.applyButtonStyle(backButton, darkMode);

        if (scrollPane != null) {
            String bg = Theme.getBodyBackground(darkMode);
            scrollPane.setStyle(
                "-fx-background: transparent; " +
                "-fx-background-color: transparent; " +
                "-fx-control-inner-background: " + bg + ";"
            );
        }

        styleScrollBars(darkMode);
    }

    private void styleScrollBars(boolean darkMode) {
        if (scrollPane == null) return;
        Platform.runLater(() -> {
            try {
                String thumbColor = darkMode ? Theme.DARK_BUTTON_BG : Theme.LIGHT_BUTTON_BG;
                String trackColor = darkMode ? Theme.DARK_BOX : Theme.LIGHT_BOX;
                String thumbRadius = "8px";

                for (javafx.scene.Node sbNode : scrollPane.lookupAll(".scroll-bar")) {
                    if (!(sbNode instanceof ScrollBar sb)) continue;

                    Node thumb = sb.lookup(".thumb");
                    if (thumb != null) {
                        thumb.setStyle(
                            "-fx-background-color: " + thumbColor + ";" +
                            "-fx-background-radius: " + thumbRadius + ";" +
                            "-fx-padding: 4px;"
                        );
                    }

                    Node track = sb.lookup(".track");
                    if (track != null) {
                        track.setStyle(
                            "-fx-background-color: " + trackColor + ";" +
                            "-fx-background-radius: " + thumbRadius + ";"
                        );
                    }

                    Node inc = sb.lookup(".increment-button");
                    Node dec = sb.lookup(".decrement-button");
                    if (inc != null) inc.setStyle("-fx-background-color: transparent;");
                    if (dec != null) dec.setStyle("-fx-background-color: transparent;");
                }
            } catch (Exception ex) {
                System.err.println("[DEBUG] styleScrollBars failed: " + ex);
            }
        });
    }

    public Label createDisplayLabelForSceneId(String sceneId) {
        String display = (sceneId == null) ? "" : winningTitlesMap.getOrDefault(sceneId, sceneId.replace('_', ' '));
        Label lbl = new Label(display);
        lbl.setWrapText(true);
        lbl.setMaxWidth(320);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-alignment: center;");
        photoLabelById.put(sceneId, lbl);
        return lbl;
    }

    public void setTitleForSceneId(String sceneId, String newTitle) {
        if (sceneId == null || newTitle == null) return;
        winningTitlesMap.put(sceneId, newTitle);
        Label lbl = photoLabelById.get(sceneId);
        if (lbl != null) lbl.setText(newTitle);
    }

    public void setTitles(Map<String,String> titles) {
        if (titles == null || titles.isEmpty()) return;
        winningTitlesMap.putAll(titles);
        for (Map.Entry<String,String> e : titles.entrySet()) {
            Label lbl = photoLabelById.get(e.getKey());
            if (lbl != null) lbl.setText(e.getValue());
        }
    }

    public boolean loadTitlesFromJson(File jsonFile) {
        if (jsonFile == null || !jsonFile.exists()) return false;
        try (FileReader r = new FileReader(jsonFile)) {
            JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
            Map<String,String> map = new HashMap<>();
            for (String key : o.keySet()) {
                if (o.get(key).isJsonNull()) continue;
                map.put(key, o.get(key).getAsString());
            }
            setTitles(map);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static List<String> getWinningSceneIdsFromStory(String storyFilePath) {
        List<String> winIds = new ArrayList<>();
        try (FileReader reader = new FileReader(storyFilePath)) {
            JsonArray scenes = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement elem : scenes) {
                if (!elem.isJsonObject()) continue;
                JsonObject obj = elem.getAsJsonObject();
                if ("WIN".equalsIgnoreCase(obj.has("ending") ? obj.get("ending").getAsString() : null)) {
                    if (obj.has("id")) {
                        winIds.add(obj.get("id").getAsString());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return winIds;
    }

    private String getImagePathForSceneId(String sceneId) {
        String[] storyFiles = {
            "src/data/drive_story1.json",
            "src/data/walk_story2.json"
        };
        for (String storyFile : storyFiles) {
            try {
                model.SceneLoader loader = new model.SceneLoader(storyFile);
                Map<String, model.GameScene> scenes = loader.getScenes();
                if (scenes.containsKey(sceneId)) {
                    String imagePath = scenes.get(sceneId).getImagePath();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        return imagePath;
                    }
                }
            } catch (Exception ex) {
            }
        }
        return "imgs/defaultImg.jpg";
    }
}
