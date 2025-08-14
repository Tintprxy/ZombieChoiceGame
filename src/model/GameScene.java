package model;

import java.util.List;

public class GameScene {

    // new field for fight number; default to 1 if not set
    private int fightNumber = 1;

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

    public GameScene(String id, String prompt, int healthChange, List<GameChoice> choices, InventoryItem addItem) {
        this.id = id;
        this.prompt = prompt;
        this.healthChange = healthChange;
        this.choices = choices;
        this.addItem = addItem;
    }

    public GameScene(String id, String prompt, int healthChange, List<GameChoice> choices, InventoryItem addItem, int threatLevel) {
        this.id = id;
        this.prompt = prompt;
        this.healthChange = healthChange;
        this.choices = choices;
        this.addItem = addItem;
        this.threatLevel = threatLevel;
    }

    private String id;
    private String prompt;
    private int healthChange;
    private List<GameChoice> choices;
    private InventoryItem addItem;
    private int threatLevel = -1; 

    public String getId() { return id; }
    public String getPrompt() { return prompt; }
    public int getHealthChange() { return healthChange; }
    public List<GameChoice> getChoices() { return choices; }
    public int getThreatLevel() { return threatLevel; }

    // New getter for fightNumber
    public int getFightNumber() { 
        return fightNumber; 
    }
    
    // Optional setter for fightNumber
    public void setFightNumber(int fightNumber) {
        this.fightNumber = fightNumber;
    }

    public boolean hasAddItem() {
        return addItem != null;
    }

    public InventoryItem getAddItem() {
        return addItem;
    }

    public void setThreatLevel(int threatLevel) {
        this.threatLevel = threatLevel;
    }
}