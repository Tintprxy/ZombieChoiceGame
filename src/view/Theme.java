package view;

import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class Theme {
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

    public static final String DISABLED_BUTTON_STYLE_LIGHT = "-fx-opacity: 0.5; -fx-text-fill: derive(-fx-control-inner-background, -30%); -fx-background-color: #d3d3d3;";
    public static final String DISABLED_BUTTON_STYLE_DARK = "-fx-opacity: 0.5; -fx-text-fill: derive(-fx-control-inner-background, 50%); -fx-background-color: #666666;";

    public static String getDisabledButtonStyle(boolean isDarkMode) {
         return isDarkMode ? DISABLED_BUTTON_STYLE_DARK : DISABLED_BUTTON_STYLE_LIGHT;
    }

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

    public static void applyButtonStyle(Button b, boolean dark) {
        String base   = dark ? "#3c4149" : "#d4d9e1";  
        String text   = dark ? "#e8ecf2" : "#1f2937";  
        String style =
            "-fx-background-color: " + base + ";" +
            "-fx-text-fill: " + text + ";" +
            "-fx-background-radius: 16;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 8 16;" +
            "-fx-font-weight: 600;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 0;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;" +
            "-fx-effect: null;";

        b.setStyle(style);
    }

    public static void applyDisabledButtonStyle(Button button, boolean isDarkMode) {
        button.setDisable(true);
        button.setStyle(
            "-fx-background-radius: 20;" +
            "-fx-background-color: " + (isDarkMode ? "#444" : "#ccc") + ";" +
            "-fx-text-fill: #aaa;" +
            "-fx-opacity: 0.5;"
        );
    }

    public static void sizeToText(Labeled node, double horizontalPadding, double maxWidth) {
        Font font = node.getFont() != null ? node.getFont() : Font.getDefault();
        Text measure = new Text(node.getText() == null ? "" : node.getText());
        measure.setFont(font);
        double textW = Math.ceil(measure.getLayoutBounds().getWidth());
        double prefW = Math.min(textW + horizontalPadding, maxWidth > 0 ? maxWidth : Double.MAX_VALUE);

        node.setMinWidth(Region.USE_PREF_SIZE);
        node.setPrefWidth(prefW);
        node.setMaxWidth(Region.USE_PREF_SIZE);
    }

    public static String getInventoryBackground(boolean dark) {
        return dark ? DARK_CONTAINER : LIGHT_CONTAINER;
    }
    public static String getBorderColor(boolean dark) {
        return dark ? DARK_BORDER : LIGHT_BORDER;
    }
}