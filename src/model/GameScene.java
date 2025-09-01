package model;

import java.util.List;
import com.google.gson.JsonObject;

public class GameScene {

    // new field for fight number; default to 1 if not set
    private int fightNumber = 1;
    private String newKeyItem;

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

    // Add a field to store the raw JSON object
    private JsonObject rawJson;

    private String id;
    private String prompt;
    private int healthChange;
    private List<GameChoice> choices;
    private InventoryItem addItem;
    private int threatLevel = -1; 
    private boolean bitten;

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

    public void setNewKeyItem(String newKeyItem) {
        this.newKeyItem = newKeyItem;
    }

    public String getNewKeyItem() {
        return newKeyItem;
    }

    public void setThreatLevel(int threatLevel) {
        this.threatLevel = threatLevel;
    }

    // Getter for rawJson
    public JsonObject getRawJson() {
        return rawJson;
    }

    // Optionally, a setter if you need to set it after construction
    public void setRawJson(JsonObject rawJson) {
        this.rawJson = rawJson;
    }

    public boolean isBitten() {
        return bitten;
    }

    public void setBitten(boolean bitten) {
        this.bitten = bitten;
    }
}