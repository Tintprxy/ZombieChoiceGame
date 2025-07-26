package model;

import com.google.gson.*;
import java.io.FileReader;
import java.util.*;

public class InventoryLoader {
    public static List<InventoryItem> load(String path) {
        try (FileReader reader = new FileReader(path)) {
            InventoryItem[] items = new Gson().fromJson(reader, InventoryItem[].class);
            return Arrays.asList(items);
        } catch (Exception e) {
            System.err.println("Inventory load error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}