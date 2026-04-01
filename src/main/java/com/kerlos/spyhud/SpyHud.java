package com.kerlos.spyhud;

import com.kerlos.spyhud.config.SpyHudConfig;
import com.kerlos.spyhud.hud.SpyHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SpyHud implements ClientModInitializer {

    public static KeyBinding zoomKey;
    public static KeyBinding toggleHudKey;
    public static KeyBinding zoomIncrease;
    public static KeyBinding zoomDecrease;

    public static final MinecraftClient client =
            MinecraftClient.getInstance();

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(
                    Identifier.of("spyhud", "title")
            );

    @Override
    public void onInitializeClient() {
        SpyHudConfig.load();
        SpyHudRenderer.register();

        registerKeybinds();
        registerTickEvents();
    }

    private void registerKeybinds() {
        zoomKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.spyhud.zoom",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_C,
                        CATEGORY
                )
        );

        toggleHudKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.spyhud.toggle_hud",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_H,
                        CATEGORY
                )
        );

        zoomIncrease = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.spyhud.zoom_in",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_KP_ADD,
                        CATEGORY
                )
        );

        zoomDecrease = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.spyhud.zoom_out",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_KP_SUBTRACT,
                        CATEGORY
                )
        );
    }

    private void registerTickEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            SpyHudConfig config = SpyHudConfig.INSTANCE;

            // Zoom mode
            if (config.holdToZoom) {
                ZoomManager.isZooming = zoomKey.isPressed();
            } else {
                if (zoomKey.wasPressed()) {
                    ZoomManager.isZooming =
                            !ZoomManager.isZooming;
                }
            }

            // Toggle HUD
            if (toggleHudKey.wasPressed()) {
                SpyHudRenderer.hudShown =
                        !SpyHudRenderer.hudShown;
            }

            // Zoom levels
            if (zoomIncrease.wasPressed()) {
                config.nextZoom();
            }

            if (zoomDecrease.wasPressed()) {
                config.prevZoom();
            }
        });
    }
}