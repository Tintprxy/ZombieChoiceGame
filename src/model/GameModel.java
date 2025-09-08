package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameModel {
    private boolean isDarkMode;
    private GameState currentState = GameState.TITLE;
    private int health;
    private Map<ItemType, List<InventoryItem>> inventory = new HashMap<>();
    private static final int MAX_CONSUMABLES = 3;
    private static final int MAX_KEY_ITEMS = 1;
    private static final int MAX_WEAPONS = 2;
    private static final int INITIAL_HEALTH = 100;
    private InventoryItem keyItem;
    private boolean antidoteUsed = false;

    public GameModel() {
        isDarkMode = false;
        this.health = INITIAL_HEALTH;

        for (ItemType type : ItemType.values()) {
            inventory.put(type, new ArrayList<>());
        }

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
        health = Math.max(0, Math.min(100, value));
    }

    public int getHealth() {
        return health;
    }

    public void subtractHealth(int amount) {
        health += amount;
        health = Math.max(0, Math.min(100, health));
    }

    public void addHealth(int amount) {
        health += amount;
        health = Math.max(0, Math.min(100, health));
    }
    
    public boolean addItem(InventoryItem item) {
        if (item.getType() == ItemType.KEY_ITEM) {
            if (item.getName().equalsIgnoreCase("Antidote")) {
                setAntidoteUsed(false);
                System.out.println("[DEBUG] Antidote used flag reset since a new antidote was found.");
            }
            List<InventoryItem> keyItems = inventory.get(ItemType.KEY_ITEM);
            if (keyItems != null && !keyItems.isEmpty()) {
                javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Key Item Swap Confirmation");
                confirm.setHeaderText("Key item already exists.");
                confirm.setContentText("You already have the key item \""
                    + keyItems.get(0).getName()
                    + "\". Are you sure you want to swap it for \""
                    + item.getName() + "\"?");

                java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    keyItems.clear();
                    System.out.println("[DEBUG] Existing key item removed for swap.");
                } else {
                    System.out.println("[DEBUG] User canceled key item swap. Skipping add for: " + item.getName());
                    return false;
                }
            }
        }
        inventory.putIfAbsent(item.getType(), new ArrayList<>());
        List<InventoryItem> items = inventory.get(item.getType());

        int limit = switch (item.getType()) {
            case WEAPON -> MAX_WEAPONS;
            case CONSUMABLE -> MAX_CONSUMABLES;
            case KEY_ITEM -> MAX_KEY_ITEMS;
        };

        if (item.isWeapon() && items.size() >= limit) {
            System.out.println("[DEBUG] Weapon limit reached, not adding: " + item.getName());
            return false;
        }
        items.add(item);
        System.out.println("[DEBUG] Added item: " + item.getName() + ", inventory now: " + inventory);
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

    public InventoryItem getKeyItem() {
        return keyItem;
    }

    public void setKeyItem(InventoryItem keyItem) {
        this.keyItem = keyItem;
    }

    public void reloadInventory() {
        this.inventory = InventoryLoader.load("src/data/inventory.json")
            .stream()
            .collect(Collectors.groupingBy(InventoryItem::getType));
    }

    public void clearInventory() {
        if (inventory != null) {
            inventory.clear();
        }
    }

    public void resetHealth() {
        this.health = INITIAL_HEALTH;
    }

    public boolean isAntidoteUsed() {
        return antidoteUsed;
    }

    public void setAntidoteUsed(boolean antidoteUsed) {
        this.antidoteUsed = antidoteUsed;
    }

    public void removeBrokenWeapons() {
        List<InventoryItem> weapons = inventory.get(ItemType.WEAPON);
        if (weapons != null) {
            weapons.removeIf(weapon -> weapon.getDurability() <= 0);
            System.out.println("[DEBUG] Broken weapons removed. Current weapons: " + weapons);
        }
    }
}
