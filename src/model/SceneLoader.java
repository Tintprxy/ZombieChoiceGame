package model;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SceneLoader {
    private final Map<String, GameScene> sceneMap = new HashMap<>();

    public SceneLoader(String pathToJson) {
        try (FileReader reader = new FileReader(pathToJson)) {
            JsonArray sceneArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement sceneElem : sceneArray) {
                JsonObject sceneObj = sceneElem.getAsJsonObject();
                String sceneId = sceneObj.get("id").getAsString();
                String prompt = sceneObj.get("prompt").getAsString();
                int healthChange = sceneObj.has("healthChange") ? sceneObj.get("healthChange").getAsInt() : 0;
                List<GameChoice> choices = new ArrayList<>();
                JsonArray choicesArray = sceneObj.getAsJsonArray("choices");
                for (JsonElement choiceElem : choicesArray) {
                    JsonObject choiceObj = choiceElem.getAsJsonObject();
                    String label = choiceObj.get("label").getAsString();
                    String imagePath = choiceObj.get("imagePath").getAsString();
                    // Support both "nextId" and "id" for compatibility
                    String nextId = choiceObj.has("nextId") ? choiceObj.get("nextId").getAsString() :
                                    (choiceObj.has("id") ? choiceObj.get("id").getAsString() : null);
                    int healthEffect = choiceObj.has("healthEffect") ? choiceObj.get("healthEffect").getAsInt() : 0;
                    GameChoice choice = new GameChoice(label, imagePath, nextId, healthEffect);
                    choice.setCurrentSceneId(sceneId);
                    choices.add(choice);
                }
                InventoryItem addItem = null;
                if (sceneObj.has("addItem")) {
                    JsonObject addItemObj = sceneObj.getAsJsonObject("addItem");
                    String name = addItemObj.get("name").getAsString();
                    ItemType type = ItemType.valueOf(addItemObj.get("type").getAsString());
                    int healthRestore = addItemObj.has("healthRestore") ? addItemObj.get("healthRestore").getAsInt() : 0;
                    int durability = addItemObj.has("durability") ? addItemObj.get("durability").getAsInt() : 0;
                    int power = addItemObj.has("power") ? addItemObj.get("power").getAsInt() : 0;
                    addItem = new InventoryItem(name, type, healthRestore, durability, power);
                }

                GameScene scene;
                if (addItem != null) {
                    scene = new GameScene(sceneId, prompt, healthChange, choices, addItem);
                } else {
                    scene = new GameScene(sceneId, prompt, healthChange, choices);
                }
                sceneMap.put(sceneId, scene);
            }
            System.out.println("SceneLoader: Loaded " + sceneMap.size() + " scenes.");
        } catch (IOException e) {
            System.err.println("Failed to load scenes: " + e.getMessage());
        }
    }

    public GameScene getSceneById(String id) {
        return sceneMap.get(id);
    }
}