package com.kerlos.spyhud.config;

import com.kerlos.spyhud.hud.SpyHudRenderer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SpyHudConfigScreen {

    public static Screen create(Screen parent) {
        SpyHudConfig config = SpyHudConfig.INSTANCE;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("spyhud.config.title"))
                .setSavingRunnable(SpyHudConfig::save);

        ConfigCategory zoomCategory =
                builder.getOrCreateCategory(
                        Text.translatable("spyhud.category.zoom")
                );

        ConfigCategory hudCategory =
                builder.getOrCreateCategory(
                        Text.translatable("spyhud.category.hud")
                );

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // 🔹 Smooth Zoom
        zoomCategory.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable("spyhud.option.smooth_zoom"),
                        config.smoothZoom
                )
                .setDefaultValue(true)
                .setTooltip(
                        Text.translatable("spyhud.tooltip.smooth_zoom")
                )
                .setSaveConsumer(val -> config.smoothZoom = val)
                .build());

        // 🔹 Lerp Speed
        zoomCategory.addEntry(entryBuilder
                .startFloatField(
                        Text.translatable("spyhud.option.lerp_speed"),
                        config.lerpSpeed
                )
                .setMin(0.02f)
                .setMax(1.0f)
                .setTooltip(
                        Text.translatable("spyhud.tooltip.lerp_speed")
                )
                .setSaveConsumer(val -> config.lerpSpeed = val)
                .build());

        // 🔹 Hold to Zoom
        zoomCategory.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable("spyhud.option.hold_to_zoom"),
                        config.holdToZoom
                )
                .setTooltip(
                        Text.translatable("spyhud.tooltip.hold_to_zoom")
                )
                .setSaveConsumer(val -> config.holdToZoom = val)
                .build());

        // 🔹 Zoom levels
        zoomCategory.addEntry(entryBuilder
                .startFloatList(
                        Text.translatable("spyhud.option.zoom_level"),
                        config.zoomLevels
                )
                .setTooltip(
                        Text.translatable("spyhud.tooltip.zoom_level")
                )
                .setSaveConsumer(list -> config.zoomLevels = list)
                .setMin(0.05f)
                .setMax(10.0f)
                .build());

        // 🔹 HUD visible
        hudCategory.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable("spyhud.option.show_hud"),
                        config.showHud
                )
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showHud = val)
                .build());

        // 🔹 Animation type
        hudCategory.addEntry(entryBuilder
                .startEnumSelector(
                        Text.translatable("spyhud.option.hud_animation"),
                        SpyHudRenderer.HudAnimationType.class,
                        config.animationType
                )
                .setTooltip(
                        Text.translatable("spyhud.tooltip.hud_animation")
                )
                .setDefaultValue(SpyHudRenderer.HudAnimationType.SLIDE_UP)
                .setSaveConsumer(val -> config.animationType = val)
                .build());

        // 🔹 Animation speed
        hudCategory.addEntry(entryBuilder
                .startFloatField(
                        Text.translatable("spyhud.option.hud_animation_speed"),
                        config.animationSpeed
                )
                .setTooltip(
                        Text.translatable("spyhud.tooltip.hud_animation_speed")
                )
                .setMin(0.01f)
                .setMax(1.0f)
                .setDefaultValue(0.1f)
                .setSaveConsumer(val -> config.animationSpeed = val)
                .build());

        return builder.build();
    }
}