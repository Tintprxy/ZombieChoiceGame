package view;

import javafx.scene.control.Button;

public class Theme {
    // Centralized color palette
    public static final String DARK_BG = "#23272e";
    public static final String DARK_BOX = "#181a1b";
    public static final String DARK_BORDER = "#222222";
    public static final String DARK_TEXT = "#f8f8f2";
    public static final String DARK_BUTTON_BG = "#3c414aff";
    public static final String DARK_BUTTON_TEXT = "#f8f8f2";
    public static final String DARK_CONTAINER = "#181a1b";

    public static final String LIGHT_BG = "#f8f8f8";
    public static final String LIGHT_BOX = "#e0e0e0";
    public static final String LIGHT_BORDER = "#bbbbbb";
    public static final String LIGHT_TEXT = "#23272e"; 
    public static final String LIGHT_BUTTON_BG = "#f4f4f4";
    public static final String LIGHT_BUTTON_TEXT = "#23272e";
    public static final String LIGHT_CONTAINER = "#ffffff";

    public static String getBodyBackground(boolean dark) {
        return dark ? DARK_BG : LIGHT_BG;
    }

    public static String getTextColor(boolean dark) {
        return dark ? DARK_TEXT : LIGHT_TEXT;
    }

    public static String getTopBarBackground(boolean dark) {
        return dark ? DARK_BOX : LIGHT_BOX;
    }

    public static String getToggleButtonColor(boolean dark) {
        return dark ? DARK_BORDER : LIGHT_BORDER;
    }

    public static String getButtonStyle(boolean dark) {
        return dark
            ? "-fx-background-color: " + DARK_BUTTON_BG + "; -fx-text-fill: " + DARK_BUTTON_TEXT + "; -fx-background-radius: 20; -fx-padding: 10 20;"
            : "-fx-background-color: " + LIGHT_BUTTON_BG + "; -fx-text-fill: " + LIGHT_BUTTON_TEXT + "; -fx-background-radius: 20; -fx-padding: 10 20;";
    }

    public static void applyButtonStyle(Button button, boolean dark) {
        button.setStyle(getButtonStyle(dark));
    }
    public static String getInventoryBackground(boolean dark) {
        return dark ? DARK_CONTAINER : LIGHT_CONTAINER;
    }
    public static String getBorderColor(boolean dark) {
        return dark ? DARK_BORDER : LIGHT_BORDER;
    }
}