// SpyHudRenderer.java
package com.kerlos.spyhud.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import com.kerlos.spyhud.config.SpyHudConfig.*;

import static com.kerlos.spyhud.config.SpyHudConfig.getCurrentZoom;

public class SpyHudRenderer implements HudElementRegistry {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier SPYHUD_ID = Identifier.of("spyhud", "hud_element");

    public static boolean hudShown = true;
    private static float alpha = 0f; // pour fade
    private static float backgroundAlpha = 0f;

    public static void register() {
        HudElementRegistry.addFirst(SPYHUD_ID, SpyHudRenderer::render);
    }

    private static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (mc.player == null || mc.world == null) return;

        // Fade du HUD
        float targetAlpha = hudShown ? 1f : 0f;
        float targetBackgroundAlpha = hudShown ? 0.5f : 0f;

        alpha += (targetAlpha - alpha) * 0.1f;
        backgroundAlpha += (targetBackgroundAlpha - backgroundAlpha) * 0.1f;

        int color = ((int)(alpha * 255) << 24) | 0xFFFFFF;
        int backgroundColor = ((int)(backgroundAlpha * 255) << 24);

        var tr = mc.textRenderer;

        // --- Position et Zoom à gauche ---
        int baseY = 10;
        var pos = mc.player.getEntityPos();

        // Dessine le fond semi-transparent
        drawContext.fill(5, baseY - 5, 160, baseY + 35, backgroundColor);

        // Dessine le texte
        String posText = String.format("Pos §f(§c%.2f§f, §a%.2f§f, §b%.2f§f)", pos.getX(), pos.getY(), pos.getZ());
        drawContext.drawText(tr, posText, 10, baseY, color, true);

        String zoomText = "Zoom §d" + getCurrentZoom() + "x";
        drawContext.drawText(tr, zoomText, 10, baseY + 10, color, true);

        int fps = mc.getCurrentFps();
        int max = mc.options.getMaxFps().getValue();
        String colorCode;

        if (fps >= 0.9 * max) colorCode = "§a"; // vert
        else if (fps >= 0.6 * max) colorCode = "§e"; // jaune
        else colorCode = "§c"; // rouge

        String fpsText = "FPS " + colorCode + fps + "§f/§7" + max;

        drawContext.drawText(tr, fpsText, 10, baseY + 20, color, true);

        // --- Block info à droite ---
        HitResult hit = mc.crosshairTarget;
        String line1, line2, line3;

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos blockPos = blockHit.getBlockPos();
            var state = mc.world.getBlockState(blockPos);
            line1 = "Targeted Block §e" + state.getBlock().getName().getString();
            line2 = String.format("Block Pos §f(§c%d§f, §a%d§f, §b%d§f)", blockPos.getX(), blockPos.getY(), blockPos.getZ());
        } else {
            line1 = "§b---------------";
            line2 = "No block targeted";
        }

        int screenWidth = mc.getWindow().getScaledWidth();

        // Calcul largeur max du texte pour le rectangle
        int maxWidth = Math.max(tr.getWidth(line1), tr.getWidth(line2));

        int padding = 5; // espace autour du texte

        // Position X du rectangle
        int rectX1 = screenWidth - maxWidth - padding * 2 - 5; // bord gauche
        int rectY1 = baseY - padding;
        int rectX2 = screenWidth - 5; // bord droit
        int rectY2 = baseY + 20 + padding; // 2 lignes de 10px chacune

        // Dessine le rectangle transparent
        drawContext.fill(rectX1, rectY1, rectX2, rectY2, backgroundColor);

        drawContext.drawText(tr, line1, screenWidth - tr.getWidth(line1) - 10, baseY, color, true);
        drawContext.drawText(tr, line2, screenWidth - tr.getWidth(line2) - 10, baseY + 10, color, true);
    }
}