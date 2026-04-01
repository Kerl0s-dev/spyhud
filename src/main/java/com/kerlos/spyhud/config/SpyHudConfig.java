package com.kerlos.spyhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kerlos.spyhud.hud.SpyHudRenderer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SpyHudConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final File CONFIG_FILE =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("spyhud.json")
                    .toFile();

    public static SpyHudConfig INSTANCE = new SpyHudConfig();

    public boolean smoothZoom = true;
    public boolean holdToZoom = false;
    public boolean showHud = true;

    public List<Float> zoomLevels =
            new ArrayList<>(List.of(0.66f, 0.5f, 0.33f, 0.25f, 0.2f));

    public float lerpSpeed = 0.25f;
    public SpyHudRenderer.HudAnimationType animationType = SpyHudRenderer.HudAnimationType.SLIDE_UP;
    public float animationSpeed = 0.1f;

    private int currentZoomIndex = 0;

    public float getCurrentZoom() {
        return zoomLevels.get(currentZoomIndex);
    }

    public void nextZoom() {
        currentZoomIndex = (currentZoomIndex + 1) % zoomLevels.size();
    }

    public void prevZoom() {
        currentZoomIndex = (currentZoomIndex - 1 + zoomLevels.size()) % zoomLevels.size();
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (Reader reader = new InputStreamReader(
                new FileInputStream(CONFIG_FILE),
                StandardCharsets.UTF_8
        )) {
            INSTANCE = GSON.fromJson(reader, SpyHudConfig.class);

            if (INSTANCE == null) {
                INSTANCE = new SpyHudConfig();
            }

        } catch (Exception e) {
            System.err.println("[SpyHud] Failed to load config.");
            INSTANCE = new SpyHudConfig();
        }
    }

    public static void save() {
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(CONFIG_FILE),
                StandardCharsets.UTF_8
        )) {
            GSON.toJson(INSTANCE, writer);

        } catch (Exception e) {
            System.err.println("[SpyHud] Failed to save config.");
        }
    }
}