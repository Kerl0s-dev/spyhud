package com.kerlos.spyhud;

public class ZoomManager {

    private static boolean currentlyZoomed;
    private static boolean originalSmoothCameraEnabled;
    public static float zoomLevel = (float) 0.23;

    public static float currentZoom = 1.0f;  // Zoom actuel interpolé
    public static float lerpSpeed = 0.1f;    // Vitesse du lerp, ajustable

    public static boolean isZooming;

    public static void manageSmoothCamera() {
        if (zoomStarting()) {
            zoomStarted();
            enableSmoothCamera();
        }

        if (zoomStopping()) {
            zoomStopped();
            resetSmoothCamera();
        }
    }

    private static boolean isSmoothCamera() { return SpyHud.client.options.smoothCameraEnabled; }

    private static void enableSmoothCamera() { SpyHud.client.options.smoothCameraEnabled = true; }

    private static void disableSmoothCamera() { SpyHud.client.options.smoothCameraEnabled = false; }

    private static boolean zoomStarting() { return isZooming && !currentlyZoomed; }

    private static boolean zoomStopping() { return !isZooming && currentlyZoomed; }

    private static void zoomStarted() {
        originalSmoothCameraEnabled = isSmoothCamera();
        currentlyZoomed = true;
    }

    private static void zoomStopped() { currentlyZoomed = false; }

    private static void resetSmoothCamera() {
        if (originalSmoothCameraEnabled) {
            enableSmoothCamera();
        } else {
            disableSmoothCamera();
        }
    }

    static void setZoomLevel(float value) {
        zoomLevel = value;
    }

    public static float lerp(float start, float end, float t) {
        return start + t * (end - start);
    }
}
