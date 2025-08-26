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
                try {
                    JsonObject sceneObj = sceneElem.getAsJsonObject();
                    String sceneId = sceneObj.get("id").getAsString();
                    System.out.println("[DEBUG] Parsing scene ID: " + sceneId);
                    String prompt = sceneObj.get("prompt").getAsString();
                    int healthChange = sceneObj.has("healthChange") ? sceneObj.get("healthChange").getAsInt() : 0;
                    List<GameChoice> choices = new ArrayList<>();
                    JsonArray choicesArray = sceneObj.getAsJsonArray("choices");
                    for (JsonElement choiceElem : choicesArray) {
                        JsonObject choiceObj = choiceElem.getAsJsonObject();
                        String label = choiceObj.get("label").getAsString();
                        String imagePath = choiceObj.get("imagePath").getAsString();
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
                    int threatLevel = sceneObj.has("threatLevel") ? sceneObj.get("threatLevel").getAsInt() : -1;

                    GameScene scene;
                    if (addItem != null) {
                        scene = new GameScene(sceneId, prompt, healthChange, choices, addItem);
                    } else {
                        scene = new GameScene(sceneId, prompt, healthChange, choices);
                    }

                    scene.setThreatLevel(threatLevel);

                    int fightNumber = sceneObj.has("fightNumber") ? sceneObj.get("fightNumber").getAsInt() : 1;
                    scene.setFightNumber(fightNumber);

                    sceneMap.put(sceneId, scene);
                } catch (Exception ex) {
                    System.err.println("[ERROR] Failed to parse scene: " + sceneElem);
                    ex.printStackTrace();
                }
            }
            System.out.println("SceneLoader: Loaded " + sceneMap.size() + " scenes.");
            System.out.println("[DEBUG] Scene IDs loaded: " + sceneMap.keySet());
        } catch (IOException e) {
            System.err.println("Failed to load scenes: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON syntax: " + e.getMessage());
        }
    }

    public GameScene getSceneById(String id) {
        return sceneMap.get(id); // Ensure sceneMap contains the "start" scene
    }

    public Map<String, GameScene> getScenes() {
        return sceneMap;
    }

    public static void main(String[] args) {
        SceneLoader sceneLoader = new SceneLoader("src/data/scenes_story1.json");
        System.out.println("[DEBUG] Loaded scenes: " + sceneLoader.getScenes().size());
        GameScene next = sceneLoader.getSceneById("start");
        if (next != null) {
            System.out.println("[DEBUG] Starting scene: " + next.getId());
            // showSceneView(next, sceneLoader); // Uncomment this line when showSceneView method is available
        } else {
            System.out.println("[DEBUG] Failed to load starting scene.");
        }
    }
}