package model;

public class GameModel {
    private boolean isDarkMode;
    private GameState currentState = GameState.TITLE;
    private int health = 100;

    public GameModel() {
        isDarkMode = false;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void toggleDarkMode() {
        isDarkMode = !isDarkMode;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public void setHealth(int value) {
        health = Math.max(0, Math.min(100, value)); // clamp between 0–100
    }

    public int getHealth() {
        return health;
    }

    public void subtractHealth(int amount) {
        setHealth(health - amount);
    }

    public void addHealth(int amount) {
        setHealth(health + amount);
    }
}