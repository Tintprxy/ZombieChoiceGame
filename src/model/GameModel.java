package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameModel {
    private boolean isDarkMode;
    private GameState currentState = GameState.TITLE;
    private int health = 100;
    private Map<ItemType, List<InventoryItem>> inventory = new HashMap<>();
    private static final int MAX_CONSUMABLES = 3;
    private static final int MAX_KEY_ITEMS = 1;
    private static final int MAX_WEAPONS = 2;

    public GameModel() {
        isDarkMode = false;

        // Ensure all categories are initialized
        for (ItemType type : ItemType.values()) {
            inventory.put(type, new ArrayList<>());
        }

        // Load from JSON only
        List<InventoryItem> initialItems = InventoryLoader.load("src/data/inventory.json");
        for (InventoryItem item : initialItems) {
            addItem(item);
        }
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
        health += amount; // negative amount decreases health, positive heals
        health = Math.max(0, Math.min(100, health)); // clamp between 0–100
    }

    public void addHealth(int amount) {
        health += amount; // positive amount increases health, negative decreases health
        health = Math.max(0, Math.min(100, health)); // clamp between 0–100
    }
    
    public boolean addItem(InventoryItem item) {

        inventory.putIfAbsent(item.getType(), new ArrayList<>());

        List<InventoryItem> items = inventory.get(item.getType());

        int limit = switch (item.getType()) {
            case WEAPON -> MAX_WEAPONS;
            case CONSUMABLE -> MAX_CONSUMABLES;
            case KEY_ITEM -> MAX_KEY_ITEMS;
        };

        if (items.size() >= limit) return false;

        items.add(item);
        return true;
    }

    public boolean removeItem(String itemName) {
        for (List<InventoryItem> items : inventory.values()) {
            if (items.removeIf(i -> i.getName().equals(itemName))) return true;
        }
        return false;
    }

    public Map<ItemType, List<InventoryItem>> getInventory() {
        return inventory;
    }

    public boolean consumeItem(InventoryItem item) {
        if (item != null && item.isConsumable()) {
            addHealth(item.getHealthRestore());
            for (List<InventoryItem> itemList : inventory.values()) {
                if (itemList.remove(item)) {
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public void removeFromInventory(InventoryItem item) {
        inventory.get(item.getType()).remove(item);
    }
}