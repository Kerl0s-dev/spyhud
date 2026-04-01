package com.kerlos.spyhud.mixin;

import com.kerlos.spyhud.ZoomManager;
import com.kerlos.spyhud.config.SpyHudConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class SpyHudMixin {

    @Inject(
            method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F",
            at = @At("RETURN"),
            cancellable = true
    )
    public void getZoomLevel(CallbackInfoReturnable<Float> cir) {
        float fov = cir.getReturnValue();

        SpyHudConfig config = SpyHudConfig.INSTANCE;

        float targetZoom = ZoomManager.isZooming
                ? config.getCurrentZoom()
                : 1.0f;

        ZoomManager.currentZoom = config.smoothZoom
                ? ZoomManager.lerp(
                ZoomManager.currentZoom,
                targetZoom,
                config.lerpSpeed
        )
                : targetZoom;

        cir.setReturnValue(fov * ZoomManager.currentZoom);

        ZoomManager.manageSmoothCamera();
    }
}