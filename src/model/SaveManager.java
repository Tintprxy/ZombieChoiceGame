package model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.Optional;

public class SaveManager {
    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    private static final Path SAVE_DIR = Paths.get("src", "data", "saves");

    private static Path pathForSlot(int slot) {
        if (slot < 1 || slot > 3) throw new IllegalArgumentException("slot must be 1..3");
        return SAVE_DIR.resolve("slot" + slot + ".json");
    }

    public static void ensureDir() {
        try {
            Files.createDirectories(SAVE_DIR);
        } catch (IOException e) {
            System.err.println("[DEBUG] Failed to ensure save dir: " + e);
        }
    }

    public static boolean exists(int slot) {
        ensureDir();
        return Files.exists(pathForSlot(slot));
    }

    public static void delete(int slot) {
        ensureDir();
        try {
            Files.deleteIfExists(pathForSlot(slot));
        } catch (IOException e) {
            System.err.println("[DEBUG] Failed to delete save (slot " + slot + "): " + e);
        }
    }

    public static void save(int slot, SaveData data) {
        ensureDir();
        Path p = pathForSlot(slot);
        try (Writer w = Files.newBufferedWriter(p)) {
            GSON.toJson(data, w);
        } catch (Exception e) {
            System.err.println("[DEBUG] Failed to save game (slot " + slot + "): " + e);
            e.printStackTrace();
        }
    }

    public static Optional<SaveData> load(int slot) {
        ensureDir();
        Path p = pathForSlot(slot);
        if (!Files.exists(p)) return Optional.empty();
        try (Reader r = Files.newBufferedReader(p)) {
            Type t = new TypeToken<SaveData>(){}.getType();
            SaveData data = GSON.fromJson(r, t);
            return Optional.ofNullable(data);
        } catch (Exception e) {
            System.err.println("[DEBUG] Failed to load save (slot " + slot + "): " + e);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<String> peekPlayerName(int slot) {
        return load(slot).map(d -> d.playerName);
    }

    public static Optional<String> peekSceneId(int slot) {
        return load(slot).map(d -> d.currentSceneId);
    }
}