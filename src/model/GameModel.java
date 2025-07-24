package model;

public class GameModel {
    private boolean isDarkMode;

    public GameModel() {
        isDarkMode = false;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void toggleDarkMode() {
        isDarkMode = !isDarkMode;
    }
}