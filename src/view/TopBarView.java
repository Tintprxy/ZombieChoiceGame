package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class TopBarView extends HBox {
    public final Button toggleButton = new Button("Dark Mode");

    public TopBarView() {
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_RIGHT);
        getChildren().add(toggleButton);
    }

    public void applyTheme(boolean isDarkMode) {
        toggleButton.setText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
        toggleButton.setStyle(isDarkMode
                ? "-fx-background-color: #444444; -fx-text-fill: white;"
                : "-fx-background-color: #eeeeee; -fx-text-fill: black;");
    }
}