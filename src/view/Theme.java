package view;

import javafx.scene.control.Button;

public class Theme {
    public static String getBodyBackground(boolean dark) {
        return dark ? "#1e1e1e" : "white";
    }

    public static String getTextColor(boolean dark) {
        return dark ? "white" : "black";
    }

    public static String getTopBarBackground(boolean dark) {
        return dark ? "#2b2b2b" : "#f0f0f0";
    }

    public static String getToggleButtonColor(boolean dark) {
        return dark ? "#3c3c3c" : "#e0e0e0";
    }

    public static String getButtonStyle(boolean dark) {
        return dark
            ? "-fx-background-color: #444444; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 20;"
            : "-fx-background-color: #dddddd; -fx-text-fill: black; -fx-background-radius: 8; -fx-padding: 10 20;";
    }

    public static void applyButtonStyle(Button button, boolean dark) {
        button.setStyle(getButtonStyle(dark));
    }
    public static String getInventoryBackground(boolean dark) {
        return dark ? "#2e2e2e" : "#f0f0f0";
    }
    public static String getBorderColor(boolean dark) {
        return dark ? "#444444" : "#cccccc";
    }
}
