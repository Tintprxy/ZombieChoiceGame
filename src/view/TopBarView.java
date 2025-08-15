package view;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
public class TopBarView extends HBox {
    public final Button toggleButton;
    public final Button resetButton;

    public TopBarView() {
        this.toggleButton = new Button("Toggle Theme");
        this.resetButton = new Button("Reset");
        getChildren().addAll(toggleButton, resetButton);
        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(10);
    }

    public void applyTheme(boolean darkMode) {
        Theme.applyButtonStyle(toggleButton, darkMode);
        Theme.applyButtonStyle(resetButton, darkMode);
        toggleButton.setText(darkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
    }
}