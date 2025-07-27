package view;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import static view.Theme.*;
public class TopBarView extends HBox {
    public Button toggleButton;

    public TopBarView() {
        toggleButton = new Button();
        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(10);
        getChildren().add(toggleButton);
    }

    public void applyTheme(boolean darkMode) {
        setStyle("-fx-background-color: " + getTopBarBackground(darkMode) + "; -fx-padding: 10;");
        toggleButton.setStyle("-fx-background-color: " + getToggleButtonColor(darkMode) +
                            "; -fx-text-fill: " + getTextColor(darkMode) + ";");
        toggleButton.setText(darkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
    }
}