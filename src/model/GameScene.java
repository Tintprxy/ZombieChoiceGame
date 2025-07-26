package model;

import java.util.List;

public class GameScene {

    public GameScene(String id, String prompt, List<GameChoice> choices) {
    this.id = id;
    this.prompt = prompt;
    this.choices = choices;
    }

    public GameScene(String id, String prompt, int healthChange, List<GameChoice> choices) {
        this.id = id;
        this.prompt = prompt;
        this.healthChange = healthChange;
        this.choices = choices;
    }

    private String id;
    private String prompt;
    private int healthChange;
    private List<GameChoice> choices;

    public String getId() { return id; }
    public String getPrompt() { return prompt; }
    public int getHealthChange() { return healthChange; }
    public List<GameChoice> getChoices() { return choices; }
}