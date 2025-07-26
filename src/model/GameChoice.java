package model;

public class GameChoice {
    private String label;
    private String imagePath;
    private String nextId;
    private int healthEffect;
    private String currentSceneId;

    public GameChoice(String label, String imagePath, String nextId, int healthEffect) {
        this.label = label;
        this.imagePath = imagePath;
        this.nextId = nextId;
        this.healthEffect = healthEffect;
        this.currentSceneId = null; // Initialize currentSceneId to null
    }

    public String getLabel() {
        return label;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getNextId() {
        return nextId;
    }

    public int getHealthEffect() {
        return healthEffect;
    }

    public void setCurrentSceneId(String sceneId) {
        this.currentSceneId = sceneId;
    }

    public String getCurrentSceneId() {
        return currentSceneId;
    }  
}