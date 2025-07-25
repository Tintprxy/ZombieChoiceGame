package model;

import java.util.List;

public class GameScene {
    private String id;
    private String prompt;
    private int healthChange;
    private List<GameChoice> choices;

    public String getId() { return id; }
    public String getPrompt() { return prompt; }
    public int getHealthChange() { return healthChange; }
    public List<GameChoice> getChoices() { return choices; }
}
