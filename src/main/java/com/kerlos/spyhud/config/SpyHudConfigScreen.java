package com.kerlos.spyhud.config;

import com.kerlos.spyhud.ZoomManager;
import com.kerlos.spyhud.hud.anim.HudAnimationType;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SpyHudConfigScreen {

    public static Screen create(Screen parent) {
        var config = SpyHudConfigManager.get(); // récupère les valeurs du JSON

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("spyhud.config.title"))
                .setSavingRunnable(() -> {
                    SpyHudConfigManager.save();
                    ZoomManager.lerpSpeed = config.lerp_speed; // met à jour en direct
                    SpyHudConfig.setAnimationType(config.animationType);
                    SpyHudConfig.setZoomLevels(config.zoomLevels);
                    SpyHudConfig.setAnimationSpeed(config.animationSpeed / 100);
                    SpyHudConfig.setHudOpacity(config.hudOpacity);
                });

        ConfigCategory zoomCategory = builder.getOrCreateCategory(Text.translatable("spyhud.category.zoom"));
        ConfigCategory hudCategory = builder.getOrCreateCategory(Text.translatable("spyhud.category.hud"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // 🔹 Smooth Zoom
        zoomCategory.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("spyhud.option.smooth_zoom"), config.smoothZoom)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("spyhud.tooltip.smooth_zoom"))
                .setSaveConsumer(val -> config.smoothZoom = val)
                .build());

        // 🔹 Lerp Speed
        zoomCategory.addEntry(entryBuilder
                .startFloatField(Text.translatable("spyhud.option.lerp_speed"), config.lerp_speed)
                .setMin(0.02f)
                .setMax(10f)
                .setTooltip(Text.translatable("spyhud.tooltip.lerp_speed"))
                .setSaveConsumer(val -> config.lerp_speed = val)
                .build());

        // 🔹 Hold to Zoom
        zoomCategory.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("spyhud.option.hold_to_zoom"), config.holdToZoom)
                .setTooltip(Text.translatable("spyhud.tooltip.hold_to_zoom"))
                .setSaveConsumer(val -> config.holdToZoom = val)
                .build());

        // 🔹 Liste des niveaux de zoom
        zoomCategory.addEntry(entryBuilder
                .startFloatList(Text.translatable("spyhud.option.zoom_level"), config.zoomLevels)
                .setTooltip(Text.translatable("spyhud.tooltip.zoom_level"))
                .setSaveConsumer(list -> config.zoomLevels = list.stream().toList())
                .setDefaultValue(SpyHudConfig.getDefaultZoomLevels())
                .setMin(0.05f)
                .setMax(10.0f)
                .build());

        // 🔹 HUD visible ?
        hudCategory.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("spyhud.option.show_hud"), config.showHud)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showHud = val)
                .build());

        // 🔹 Type d’animation
        hudCategory.addEntry(entryBuilder
                .startEnumSelector(Text.translatable("spyhud.option.hud_animation"), HudAnimationType.class, config.animationType)
                .setTooltip(Text.translatable("spyhud.tooltip.hud_animation"))
                .setDefaultValue(HudAnimationType.FADE)
                .setSaveConsumer(val -> config.animationType = val)
                .build());

        hudCategory.addEntry(entryBuilder
                .startFloatField(Text.translatable("spyhud.option.hud_animation_speed"), config.animationSpeed)
                .setTooltip(Text.translatable("spyhud.tooltip.hud_animation_speed"))
                .setMin(1.0f)
                .setMax(10.0f)
                .setDefaultValue(10.0f)
                .setSaveConsumer(val -> config.animationSpeed = val)
                .build());

        hudCategory.addEntry(entryBuilder
                .startFloatField(Text.translatable("spyhud.option.hud_opacity"), config.hudOpacity)
                .setTooltip(Text.translatable("spyhud.tooltip.hud_opacity"))
                .setMin(0.0f)
                .setMax(1.0f)
                .setDefaultValue(1.0f)
                .setSaveConsumer(val -> config.hudOpacity = val)
                .build());

        return builder.build();
    }

    // ⚙️ Méthodes utilitaires si tu veux encore y accéder directement ailleurs

    public static boolean isZoomSmooth() {
        return SpyHudConfigManager.get().smoothZoom;
    }

    public static boolean isHoldZoom() {
        return SpyHudConfigManager.get().holdToZoom;
    }
}