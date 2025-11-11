package com.kerlos.spyhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SpyHudConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("spyhud.json").toFile();

    private static SpyHudConfigData config = new SpyHudConfigData();

    public static SpyHudConfigData get() {
        return config;
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save(); // crée un fichier par défaut
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            config = GSON.fromJson(reader, SpyHudConfigData.class);
        } catch (Exception e) {
            System.err.println("[SpyHud] Failed to load config, using defaults.");
        }
    }

    public static void save() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (Exception e) {
            System.err.println("[SpyHud] Failed to save config.");
        }
    }
}