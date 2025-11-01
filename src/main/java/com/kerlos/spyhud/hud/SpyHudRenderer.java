package com.kerlos.spyhud.hud;

import com.kerlos.spyhud.config.SpyHudConfig;
import com.kerlos.spyhud.hud.anim.HudAnimationType;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import static com.kerlos.spyhud.config.SpyHudConfig.getCurrentZoom;

public class SpyHudRenderer implements HudElementRegistry {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier SPYHUD_ID = Identifier.of("spyhud", "hud_element");

    public static boolean hudShown = true;
    private static float animProgress = 0f;

    public static void register() {
        HudElementRegistry.addFirst(SPYHUD_ID, SpyHudRenderer::render);
    }

    private static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (mc.player == null || mc.world == null) return;

        // Animation
        float target = hudShown ? 1f : 0f;
        animProgress += (target - animProgress) * 0.1f;
        float backgroundAlpha = 0.5f * animProgress;

        var tr = mc.textRenderer;
        int color = ((int)(animProgress * 255) << 24) | 0xFFFFFF;
        int bgColor = ((int)(backgroundAlpha * 255) << 24);
        int screenWidth = mc.getWindow().getScaledWidth();
        int baseY = 10;

        // Gestion du type d’animation
        HudAnimationType anim = SpyHudConfig.getAnimationType();
        int offsetX = 0;
        float scale = 1f;

        switch (anim) {
            case SLIDE_LEFT -> offsetX = (int)((1f - animProgress) * -120);
            case SLIDE_RIGHT -> offsetX = (int)((1f - animProgress) * 120);
            case POP_IN -> scale = 0.8f + (0.2f * animProgress);
            case POP_OUT -> scale = 1.2f - (0.2f * animProgress);
            case FADE -> {} // déjà géré par alpha
        }

        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(offsetX, 0);
        drawContext.getMatrices().scale(scale, scale);

        // --- Position et Zoom à gauche ---
        var pos = mc.player.getEntityPos();
        drawContext.fill(5, baseY - 5, 160, baseY + 35, bgColor);

        String posText = String.format("Pos §f(§c%.2f§f, §a%.2f§f, §b%.2f§f)", pos.getX(), pos.getY(), pos.getZ());
        drawContext.drawText(tr, posText, 10, baseY, color, true);

        String zoomText = "Zoom §d" + getCurrentZoom() + "x";
        drawContext.drawText(tr, zoomText, 10, baseY + 10, color, true);

        int fps = mc.getCurrentFps();
        int max = mc.options.getMaxFps().getValue();
        String fpsColor = (fps >= 0.9 * max) ? "§a" : (fps >= 0.6 * max) ? "§e" : "§c";
        String fpsText = "FPS " + fpsColor + fps + "§f/§7" + max;
        drawContext.drawText(tr, fpsText, 10, baseY + 20, color, true);

        // --- Block info à droite ---
        HitResult hit = mc.crosshairTarget;
        String line1, line2;

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

        int maxWidth = Math.max(tr.getWidth(line1), tr.getWidth(line2));
        int rectX1 = screenWidth - maxWidth - 20;
        int rectY1 = baseY - 5;
        int rectX2 = screenWidth - 5;
        int rectY2 = baseY + 25;

        drawContext.fill(rectX1, rectY1, rectX2, rectY2, bgColor);
        drawContext.drawText(tr, line1, screenWidth - tr.getWidth(line1) - 10, baseY, color, true);
        drawContext.drawText(tr, line2, screenWidth - tr.getWidth(line2) - 10, baseY + 10, color, true);

        drawContext.getMatrices().popMatrix();
    }
}