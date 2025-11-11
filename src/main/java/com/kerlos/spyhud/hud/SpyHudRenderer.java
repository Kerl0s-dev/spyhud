package com.kerlos.spyhud.hud;

import com.kerlos.spyhud.config.SpyHudConfig;
import com.kerlos.spyhud.hud.anim.HudAnimationType;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

import static com.kerlos.spyhud.config.SpyHudConfig.getCurrentZoom;

public class SpyHudRenderer implements HudElementRegistry {

    // =====================================================
    // 🔧 CONFIGURATION DE BASE
    // =====================================================
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier SPYHUD_ID = Identifier.of("spyhud", "hud_element");

    public static boolean hudShown = true;
    private static float animProgress = 0f;

    // =====================================================
    // 🧩 REGISTRATION
    // =====================================================
    public static void register() {
        HudElementRegistry.addFirst(SPYHUD_ID, SpyHudRenderer::render);
    }

    // =====================================================
    // 🎨 RENDER LOOP
    // =====================================================
    private static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (mc.player == null || mc.world == null) return;

        // Animation progression
        float target = hudShown ? 1f : 0f;
        animProgress += (target - animProgress) * 0.1f;

        var matrices = drawContext.getMatrices();
        var tr = mc.textRenderer;
        int screenWidth = mc.getWindow().getScaledWidth();

        // Couleurs de base
        int color;
        int bgColor;

        // Récupère le type d’animation depuis la config
        HudAnimationType anim = SpyHudConfig.getAnimationType();

        // Applique les paramètres d’animation
        AnimationState animState = computeAnimation(anim);

        // Applique les transformations graphiques
        matrices.pushMatrix();
        matrices.translate(animState.offsetX, animState.offsetY);
        matrices.scale(animState.scale, animState.scale);

        // Met à jour l’opacité selon l’animation
        float opacity = SpyHudConfig.getHudOpacity();
        color = ((int)(animState.alpha * opacity * 255) << 24) | 0xFFFFFF;
        bgColor = ((int)(animState.alpha * opacity * 128) << 24);

        // Rendu des deux panneaux HUD
        renderPlayerInfo(drawContext, tr, bgColor, color);
        renderBlockInfo(drawContext, tr, bgColor, color, screenWidth);

        matrices.popMatrix();
    }

    // =====================================================
    // 🌀 ANIMATION HANDLING
    // =====================================================
    private static AnimationState computeAnimation(HudAnimationType anim) {
        AnimationState state = new AnimationState();

        switch (anim) {
            case NONE -> state.alpha = hudShown ? 1f : 0f;

            case SLIDE_LEFT -> state.offsetX = (1f - animProgress) * -120;
            case SLIDE_RIGHT -> state.offsetX = (1f - animProgress) * 120;
            case SLIDE_UP -> state.offsetY = (1f - animProgress) * -80;
            case SLIDE_DOWN -> state.offsetY = (1f - animProgress) * 80;

            case POP_IN -> state.scale = 0.8f + (0.2f * animProgress);
            case POP_OUT -> state.scale = 1.2f - (0.2f * animProgress);

            case BOUNCE_IN -> {
                float t = animProgress;
                float bounce = (float) Math.sin(t * Math.PI) * 0.2f;
                state.scale = 0.8f + (0.2f * t) + bounce * (1f - t);
            }

            case SHAKE -> state.offsetX = (float)Math.sin(animProgress * 20f) * (1f - animProgress) * 5f;
        }
        // Applique un fade progressif pour toutes les animations sauf NONE
        if (anim != HudAnimationType.NONE) {
            state.alpha = animProgress;

            // Récupère la vitesse depuis la config
            float speed = SpyHudConfig.getAnimationSpeed();

            // Animation progression avec la vitesse personnalisée
            float target = hudShown ? 1f : 0f;
            animProgress += (target - animProgress) * speed;

            // Snap à la cible si trop proche pour éviter le micro-bug de fade
            if (Math.abs(target - animProgress) < 0.001f) animProgress = target;
        }
        return state;
    }

    // =====================================================
    // 📍 RENDU DU PANNEAU GAUCHE (PLAYER INFO)
    // =====================================================
    private static void renderPlayerInfo(DrawContext drawContext, TextRenderer tr, int bgColor, int color) {
        int baseY = 10;
        var pos = Objects.requireNonNull(mc.player).getEntityPos();

        // Fond
        drawContext.fill(5, baseY - 5, 160, baseY + 45, bgColor);

        // Position
        String posText = String.format("Pos §f(§c%.2f§f, §a%.2f§f, §b%.2f§f)", pos.getX(), pos.getY(), pos.getZ());
        drawContext.drawText(tr, posText, 10, baseY, color, true);

        String dir = mc.player.getHorizontalFacing().asString().toUpperCase();
        drawContext.drawText(tr, "Facing §b" + dir, 10, baseY + 10, color, true);

        // Zoom
        String zoomText = "Zoom §d" + getCurrentZoom() + "x";
        drawContext.drawText(tr, zoomText, 10, baseY + 20, color, true);

        // FPS
        int fps = mc.getCurrentFps();
        int max = mc.options.getMaxFps().getValue();

        String fpsLimit = max <= 0 || max >= 260 ? "∞" : String.valueOf(max);
        String fpsColor = (fps >= 0.9 * max) ? "§a" : (fps >= 0.6 * max) ? "§e" : "§c";
        String fpsText = "FPS " + fpsColor + fps + "§f/§7" + fpsLimit;
        drawContext.drawText(tr, fpsText, 10, baseY + 30, color, true);
    }

    // =====================================================
    // 🧱 RENDU DU PANNEAU DROIT (BLOCK INFO)
    // =====================================================
    private static void renderBlockInfo(DrawContext drawContext, TextRenderer tr, int bgColor, int color, int screenWidth) {
        int baseY = 10;
        HitResult hit = mc.crosshairTarget;
        String line1, line2;

        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = blockHit.getBlockPos();
            var state = Objects.requireNonNull(mc.world).getBlockState(blockPos);
            line1 = "Targeted Block §e" + state.getBlock().getName().getString();
            line2 = String.format("Block Pos §f(§c%d§f, §a%d§f, §b%d§f)", blockPos.getX(), blockPos.getY(), blockPos.getZ());
        } else {
            line1 = "§b---------------";
            line2 = "No block targeted";
        }

        assert mc.world != null;
        assert mc.player != null;

        var biome = mc.world.getBiome(mc.player.getBlockPos());
        String biomeName = biome.getKey().map(k -> k.getValue().getPath()).orElse("unknown");
        String biomeText = "Biome §a" + biomeName;

        int maxWidth = Math.max(tr.getWidth(line1), Math.max(tr.getWidth(biomeText) ,tr.getWidth(line2)));
        int rectX1 = screenWidth - maxWidth - 20;
        int rectY1 = baseY - 5;
        int rectX2 = screenWidth - 5;
        int rectY2 = baseY + 35;

        drawContext.fill(rectX1, rectY1, rectX2, rectY2, bgColor);
        drawContext.drawText(tr, line1, screenWidth - tr.getWidth(line1) - 10, baseY, color, true);
        drawContext.drawText(tr, line2, screenWidth - tr.getWidth(line2) - 10, baseY + 10, color, true);
        drawContext.drawText(tr, biomeText, screenWidth - tr.getWidth(biomeText) - 10, baseY + 20, color, true);
    }

    // =====================================================
    // ⚙️ STRUCTURE INTERNE (ÉTAT D'ANIMATION)
    // =====================================================
    private static class AnimationState {
        float offsetX = 0;
        float offsetY = 0;
        float scale = 1f;
        float alpha = 1f;
    }
}