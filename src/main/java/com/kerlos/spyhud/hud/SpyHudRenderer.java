package com.kerlos.spyhud.hud;

import com.kerlos.spyhud.config.SpyHudConfig;
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

import static com.kerlos.spyhud.config.SpyHudConfig.INSTANCE;

public class SpyHudRenderer implements HudElementRegistry {

    public enum HudAnimationType {
        NONE("None"),
        SLIDE_LEFT("Slide Left"),
        SLIDE_RIGHT("Slide Right"),
        SLIDE_UP("Slide Up"),
        SLIDE_DOWN("Slide Down"),
        POP_IN("Pop In"),
        POP_OUT("Pop Out"),
        BOUNCE_IN("Bounce In"),
        SHAKE("Shake");

        private final String label;

        HudAnimationType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier SPYHUD_ID = Identifier.of("spyhud", "hud_element");

    public static boolean hudShown = true;
    private static float animProgress = 0f;

    public static void register() {
        HudElementRegistry.addFirst(SPYHUD_ID, SpyHudRenderer::render);
    }

    private static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (mc.player == null || mc.world == null) return;

        // Progression animation
        animProgress = updateAnimProgress(animProgress, hudShown, SpyHudConfig.INSTANCE.animationSpeed);

        var matrices = drawContext.getMatrices();
        var tr = mc.textRenderer;
        int screenWidth = mc.getWindow().getScaledWidth();

        AnimationState animState = computeAnimation(SpyHudConfig.INSTANCE.animationType, animProgress);

        matrices.pushMatrix();
        matrices.translate(animState.offsetX, animState.offsetY);
        matrices.scale(animState.scale, animState.scale);

        int color = withAlpha(0xFFFFFF, animState.alpha);
        int bgColor = withAlpha(0x000000, animState.alpha * 0.5f);

        renderPlayerInfo(drawContext, tr, bgColor, color);
        renderBlockInfo(drawContext, tr, bgColor, color, screenWidth);

        matrices.popMatrix();
    }

    private static float updateAnimProgress(float progress, boolean shown, float speed) {
        float target = shown ? 1f : 0f;
        progress += (target - progress) * speed;
        if (Math.abs(target - progress) < 0.001f) progress = target;
        return progress;
    }

    private static AnimationState computeAnimation(HudAnimationType anim, float progress) {
        AnimationState state = new AnimationState();
        switch (anim) {
            case NONE -> state.alpha = hudShown ? 1f : 0f;
            case SLIDE_LEFT -> state.offsetX = (1f - progress) * -120;
            case SLIDE_RIGHT -> state.offsetX = (1f - progress) * 120;
            case SLIDE_UP -> state.offsetY = (1f - progress) * -80;
            case SLIDE_DOWN -> state.offsetY = (1f - progress) * 80;
            case POP_IN -> state.scale = 0.8f + 0.2f * progress;
            case POP_OUT -> state.scale = 1.2f - 0.2f * progress;
            case BOUNCE_IN -> {
                float bounce = (float) Math.sin(progress * Math.PI) * 0.2f;
                state.scale = 0.8f + 0.2f * progress + bounce * (1f - progress);
            }
            case SHAKE -> state.offsetX = (float) Math.sin(progress * 20f) * (1f - progress) * 5f;
        }
        if (anim != HudAnimationType.NONE) state.alpha = progress;
        return state;
    }

    private static void renderPlayerInfo(DrawContext drawContext, TextRenderer tr, int bgColor, int color) {
        int baseY = 10;
        var pos = Objects.requireNonNull(mc.player).getEntityPos();

        drawRect(drawContext, 5, baseY - 5, 160, baseY + 45, bgColor);

        drawText(drawContext, tr, String.format("Pos §f(§c%.2f§f, §a%.2f§f, §b%.2f§f)", pos.getX(), pos.getY(), pos.getZ()), 10, baseY, color);
        drawText(drawContext, tr, "Facing §b" + mc.player.getHorizontalFacing().asString().toUpperCase(), 10, baseY + 10, color);
        drawText(drawContext, tr, "Zoom §d" + INSTANCE.getCurrentZoom() + "x", 10, baseY + 20, color);

        int fps = mc.getCurrentFps();
        int max = mc.options.getMaxFps().getValue();
        String fpsLimit = max <= 0 || max >= 260 ? "∞" : String.valueOf(max);
        String fpsColor = (fps >= 0.9 * max) ? "§a" : (fps >= 0.6 * max) ? "§e" : "§c";
        drawText(drawContext, tr, "FPS " + fpsColor + fps + "§f/§7" + fpsLimit, 10, baseY + 30, color);
    }

    private static void renderBlockInfo(DrawContext drawContext, TextRenderer tr, int bgColor, int color, int screenWidth) {
        int baseY = 10;
        String[] lines = getBlockInfoText();
        int maxWidth = 0;
        for (String line : lines) maxWidth = Math.max(maxWidth, tr.getWidth(line));

        int rectX1 = screenWidth - maxWidth - 20;
        int rectY1 = baseY - 5;
        int rectX2 = screenWidth - 5;
        int rectY2 = baseY + 35;

        drawRect(drawContext, rectX1, rectY1, rectX2, rectY2, bgColor);

        for (int i = 0; i < lines.length; i++) {
            drawText(drawContext, tr, lines[i], screenWidth - tr.getWidth(lines[i]) - 10, baseY + 10 * i, color);
        }
    }

    private static String[] getBlockInfoText() {
        HitResult hit = mc.crosshairTarget;
        String line1, line2;

        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockHit.getBlockPos();
            var state = Objects.requireNonNull(mc.world).getBlockState(pos);
            line1 = "Targeted Block §e" + state.getBlock().getName().getString();
            line2 = String.format("Block Pos §f(§c%d§f, §a%d§f, §b%d§f)", pos.getX(), pos.getY(), pos.getZ());
        } else {
            line1 = "§b---------------";
            line2 = "No block targeted";
        }

        assert mc.world != null && mc.player != null;
        var biome = mc.world.getBiome(mc.player.getBlockPos());
        String biomeName = biome.getKey().map(k -> k.getValue().getPath()).orElse("unknown");
        String biomeText = "Biome §a" + biomeName;

        return new String[]{line1, line2, biomeText};
    }

    private static void drawRect(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        ctx.fill(x1, y1, x2, y2, color);
    }

    private static void drawText(DrawContext ctx, TextRenderer tr, String text, int x, int y, int color) {
        ctx.drawText(tr, text, x, y, color, true);
    }

    private static int withAlpha(int baseColor, float alpha) {
        return ((int)(alpha * 255) << 24) | (baseColor & 0xFFFFFF);
    }

    private static class AnimationState {
        float offsetX = 0;
        float offsetY = 0;
        float scale = 1f;
        float alpha = 1f;
    }
}