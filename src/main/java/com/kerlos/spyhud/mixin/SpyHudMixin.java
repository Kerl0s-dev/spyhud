package com.kerlos.spyhud.mixin;

import com.kerlos.spyhud.ZoomManager;
import com.kerlos.spyhud.config.SpyHudConfig;
import com.kerlos.spyhud.config.SpyHudConfigScreen;
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
    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F", at = @At("RETURN"), cancellable = true)
    public void getZoomLevel(CallbackInfoReturnable<Float> callbackInfo) {
        float fov = callbackInfo.getReturnValue();

        // lerp vers la valeur de zoom actuelle
        float targetZoom = ZoomManager.isZooming ? SpyHudConfig.getCurrentZoom() : 1.0f;
        ZoomManager.currentZoom = SpyHudConfigScreen.isZoomSmooth() ? ZoomManager.lerp(ZoomManager.currentZoom, targetZoom, ZoomManager.lerpSpeed) : targetZoom;

        callbackInfo.setReturnValue(fov * ZoomManager.currentZoom);

        ZoomManager.manageSmoothCamera();
    }
}