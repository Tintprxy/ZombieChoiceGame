package model;

public class InventoryItem {
    private String name;
    private ItemType type;
    private int healthRestore;
    private int durability;
    private int power;

    public InventoryItem(String name, ItemType type, int healthRestore, int durability, int power) {
        this.name = name;
        this.type = type;
        this.healthRestore = healthRestore;
        this.durability = durability;
        this.power = power;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public int getHealthRestore() {
        return healthRestore;
    }

    public int getDurability() {
        return durability;
    }

    public int getPower() {
        return power;
    }

    public boolean isConsumable() {
        return type == ItemType.CONSUMABLE;
    }

    public boolean isWeapon() {
        return type == ItemType.WEAPON;
    }

    public boolean isKeyItem() {
        return type == ItemType.KEY_ITEM;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void decreaseDurability(int amount) {
        this.durability = Math.max(0, this.durability - amount);
    }

    public boolean isBroken() {
        return durability <= 0;
    }

    public void repair(int amount) {
        this.durability += amount;
    }

    public boolean isRepairable() {
        return type == ItemType.WEAPON && durability < 100;
    }

    public boolean isUsable() {
        return !isBroken() && (type == ItemType.CONSUMABLE || type == ItemType.WEAPON);
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", healthRestore=" + healthRestore +
                ", durability=" + durability +
                ", power=" + power +
                '}';
    }
}
