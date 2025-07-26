package model;

public class InventoryItem {
    private String name;
    private ItemType type;

    public InventoryItem(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public ItemType getType() { return type; }

    
}