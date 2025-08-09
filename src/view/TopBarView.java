package view;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import static view.Theme.*;
public class TopBarView extends HBox {
    public final Button toggleButton;
    public final Button resetButton; // NEW

    public TopBarView() {
        // ...existing code...
        this.toggleButton = new Button("Toggle Theme");
        this.resetButton = new Button("Reset"); // NEW
        getChildren().addAll(toggleButton, resetButton); // add reset button to the bar
        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(10);
    }

    public void applyTheme(boolean darkMode) {
        // ...existing code...
        Theme.applyButtonStyle(toggleButton, darkMode);
        Theme.applyButtonStyle(resetButton, darkMode); // NEW: style reset too
        toggleButton.setText(darkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
    }
}