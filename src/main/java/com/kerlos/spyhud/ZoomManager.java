package com.kerlos.spyhud;

public class ZoomManager {

    private static boolean currentlyZoomed;
    private static boolean originalSmoothCameraEnabled;

    public static float currentZoom = 1.0f;
    public static boolean isZooming;

    public static void manageSmoothCamera() {
        if (isZooming && !currentlyZoomed) {
            originalSmoothCameraEnabled =
                    SpyHud.client.options.smoothCameraEnabled;

            SpyHud.client.options.smoothCameraEnabled = true;
            currentlyZoomed = true;
        }

        if (!isZooming && currentlyZoomed) {
            SpyHud.client.options.smoothCameraEnabled =
                    originalSmoothCameraEnabled;

            currentlyZoomed = false;
        }
    }

    public static float lerp(float start, float end, float speed) {
        return start + speed * (end - start);
    }
}