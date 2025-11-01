package com.kerlos.spyhud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;

public class BlockInfoProvider {

    public record BlockData(String name, BlockPos pos) {}

    public static BlockData getTargetBlockData() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;

        if (player == null || world == null || mc.crosshairTarget == null)
            return null;

        // On vérifie que la visée est bien un bloc
        if (mc.crosshairTarget.getType() != HitResult.Type.BLOCK)
            return null;

        BlockHitResult hit = (BlockHitResult) mc.crosshairTarget;
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.isAir())
            return null;

        String name = Text.translatable(state.getBlock().getTranslationKey()).getString();

        return new BlockData(name, pos);
    }
}