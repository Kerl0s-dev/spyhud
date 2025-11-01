package com.kerlos.spyhud.config;

import com.kerlos.spyhud.ZoomManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpyHudConfigScreen {

    private static boolean zoomSmooth = true;
    private static boolean showHud = true;


    // Zoom
    private static boolean holdZoom = true;

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("SpyHud Configuration"));

        ConfigCategory zoomCategory = builder.getOrCreateCategory(Text.of("Zoom"));
        ConfigCategory hudCategory = builder.getOrCreateCategory(Text.of("HUD"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Toggle : zoom fluide
        zoomCategory.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("spyhud.option.smooth_zoom"), zoomSmooth)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> zoomSmooth = newValue)
                .build());

        // Zoom
        zoomCategory.addEntry(entryBuilder
                .startBooleanToggle(Text.of("Hold to Zoom"), holdZoom)
                .setSaveConsumer(newValue -> holdZoom = newValue)
                .build());

        // ✅ Toggle : afficher le HUD
        hudCategory.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("spyhud.option.show_hud"), showHud)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> showHud = newValue)
                .build());

        // Float list pour les niveaux de zoom
        hudCategory.addEntry(
                entryBuilder.startFloatList(Text.translatable("spyhud.option.zoom_level"), SpyHudConfig.getZoomLevels())
                        .setSaveConsumer(SpyHudConfig::setZoomLevels)
                        .setDefaultValue(SpyHudConfig.getDefaultZoomLevels())
                        .setMin(0.05f) // Valeur de zoom minimale
                        .setMax(0.90f) // Valeur de zoom maximale
                        .build()
        );

        builder.setSavingRunnable(() -> {
            System.out.println("[Spy Hud] Configuration sauvegardée !");
        });

        ZoomManager.zoomLevel = SpyHudConfig.getCurrentZoom();

        return builder.build();
    }

    public static boolean isShowHud() { return showHud; }
    public static boolean isZoomSmooth() { return zoomSmooth; }
    public static float getZoomLevel() { return SpyHudConfig.getCurrentZoom(); }

    public static boolean isHoldZoom() {
        return holdZoom;
    }

    public static void setHoldZoom(boolean value) {
        holdZoom = value;
    }
}