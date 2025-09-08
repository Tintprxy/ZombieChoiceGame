package model;

import java.util.List;
import com.google.gson.JsonObject;

public class GameScene {
    private int fightNumber = 1;
    private String newKeyItem;
    private boolean eaten;

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

    public GameScene(String id, String prompt, int healthChange, List<GameChoice> choices, InventoryItem addItem, int threatLevel, boolean eaten) {
        this.id = id;
        this.prompt = prompt;
        this.healthChange = healthChange;
        this.choices = choices;
        this.addItem = addItem;
        this.threatLevel = threatLevel;
        this.eaten = eaten;
    }

    private JsonObject rawJson;

    private String id;
    private String prompt;
    private int healthChange;
    private List<GameChoice> choices;
    private InventoryItem addItem;
    private int threatLevel = -1; 
    private boolean bitten;

    private String ending = "NEUTRAL"; 

    public String getId() { return id; }
    public String getPrompt() { return prompt; }
    public int getHealthChange() { return healthChange; }
    public List<GameChoice> getChoices() { return choices; }
    public int getThreatLevel() { return threatLevel; }

    public int getFightNumber() { 
        return fightNumber; 
    }
    
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

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    public boolean isWinEnding() {
        return "WIN".equalsIgnoreCase(ending);
    }
    public String getEnding() { return ending; }
    public void setEnding(String ending) { this.ending = ending; }
}