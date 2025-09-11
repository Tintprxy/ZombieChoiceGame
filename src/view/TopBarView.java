package view;

import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class TopBarView extends HBox {
    public Button toggleButton; // assumed existing
    public final Button resetButton;
    public final Button chooseStoryButton;

    private Node leftNode = null;
    private final Region spacer = new Region();
    private boolean darkMode = false;

    public TopBarView() {
        toggleButton = new Button("Theme"); // initial text (will be updated by applyTheme)
        this.resetButton = new Button("Reset");
        this.chooseStoryButton = new Button("Choose Story");
        chooseStoryButton.setVisible(false);
        chooseStoryButton.setManaged(false);

        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(12);
        setPadding(new Insets(10, 16, 10, 16));

        for (Button b : new Button[]{toggleButton, resetButton, chooseStoryButton}) {
            b.setMinWidth(120);
            b.setPrefWidth(140);
            b.setMaxWidth(180);
        }

        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(spacer, toggleButton, resetButton, chooseStoryButton);
        toggleButton.setFocusTraversable(false);
    }

    public void showChooseStoryButton(boolean show) {
        chooseStoryButton.setVisible(show);
        chooseStoryButton.setManaged(show);
    }

    public void setLeft(Node node) {
        if (leftNode != null) {
            getChildren().remove(leftNode);
        }
        leftNode = node;
        if (leftNode != null) {
            getChildren().add(0, leftNode);
            applyTextColorToLeftNode(leftNode, darkMode);
        }
    }

    private void applyTextColorToLeftNode(Node node, boolean dark) {
        String textColor = Theme.getTextColor(dark);
        if (node instanceof Labeled lbl) {
            String existing = lbl.getStyle() == null ? "" : lbl.getStyle();
            lbl.setStyle(existing + "; -fx-text-fill: " + textColor + ";");
        } else if (node instanceof Text txt) {
            txt.setFill(Color.web(textColor));
        } else {
            node.setStyle("-fx-text-fill: " + textColor + ";");
        }
    }

    public void applyTheme(boolean darkMode) {
        this.darkMode = darkMode;
        String bg = darkMode ? Theme.DARK_BG : Theme.LIGHT_BG;
        setStyle("-fx-background-color: " + bg + ";");

        try {
            Theme.applyButtonStyle(toggleButton, darkMode);
            Theme.applyButtonStyle(resetButton, darkMode);
            Theme.applyButtonStyle(chooseStoryButton, darkMode);
        } catch (Exception ignored) {
            String btnBg = darkMode ? "#374151" : "#e5e7eb";
            String btnText = darkMode ? "white" : "#111827";
            String btnStyle = String.format("-fx-background-color: %s; -fx-text-fill: %s;", btnBg, btnText);
            toggleButton.setStyle(btnStyle);
            resetButton.setStyle(btnStyle);
            chooseStoryButton.setStyle(btnStyle);
        }

        toggleButton.setText(darkMode ? "light mode" : "dark mode"); 
        if (leftNode != null) {
            applyTextColorToLeftNode(leftNode, darkMode);
        }
    }
}