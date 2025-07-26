package model;

import com.google.gson.*;
import java.io.FileReader;
import java.util.*;

public class InventoryLoader {
    public static List<InventoryItem> load(String path) {
        try (FileReader reader = new FileReader(path)) {
            JsonArray arr = JsonParser.parseReader(reader).getAsJsonArray();
            List<InventoryItem> items = new ArrayList<>();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String name = obj.get("name").getAsString();
                ItemType type = ItemType.valueOf(obj.get("type").getAsString());
                int healthRestore = obj.has("healthRestore") ? obj.get("healthRestore").getAsInt() : 0;
                int durability = obj.has("durability") ? obj.get("durability").getAsInt() : 0;
                int power = obj.has("power") ? obj.get("power").getAsInt() : 0;
                items.add(new InventoryItem(name, type, healthRestore, durability, power));
            }
            return items;
        } catch (Exception e) {
            System.err.println("Inventory load error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}