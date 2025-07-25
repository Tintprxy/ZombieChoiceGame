package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class SceneLoader {
    private final Map<String, GameScene> sceneMap = new HashMap<>();

    public SceneLoader(String pathToJson) {
        try (FileReader reader = new FileReader(pathToJson)) {
            Type sceneListType = new TypeToken<List<GameScene>>() {}.getType();
            List<GameScene> scenes = new Gson().fromJson(reader, sceneListType);
            for (GameScene scene : scenes) {
                sceneMap.put(scene.getId(), scene);
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

