package model;

import java.util.*;
public class SaveData {
    public String playerName;
    public String storyFilePath;
    public String currentSceneId;
    public int health;
    public boolean darkMode;
    public List<String> addItemProcessedScenes = new ArrayList<>();
    public String lastHealthAppliedSceneId;
    public Map<ItemType, List<InventoryItem>> inventory = new HashMap<>();
    public long lastUpdatedEpochMillis;
    public List<String> completedWinSceneIds = new ArrayList<>();
}