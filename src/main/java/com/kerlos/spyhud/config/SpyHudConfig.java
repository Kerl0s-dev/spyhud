// SpyHudConfig.java
package com.kerlos.spyhud.config;

import java.util.ArrayList;
import java.util.List;

public class SpyHudConfig {

    private final static List<Float> defaultZoomLevels = new ArrayList<>(List.of(0.66f, 0.5f, 0.33f, 0.25f, 0.2f));

    private static List<Float> zoomLevels = new ArrayList<>(List.of(0.66f, 0.5f, 0.33f, 0.25f, 0.2f));
    private static int currentZoomIndex = 0;

    public static List<Float> getZoomLevels() {
        return zoomLevels;
    }

    public static List<Float> getDefaultZoomLevels() {
        return defaultZoomLevels;
    }

    public static void setZoomLevels(List<Float> newLevels) {
        zoomLevels = new ArrayList<>(newLevels);
        if (currentZoomIndex >= zoomLevels.size()) currentZoomIndex = zoomLevels.size() - 1;
        if (currentZoomIndex < 0) currentZoomIndex = 0;
    }

    public static float getCurrentZoom() {
        return zoomLevels.get(currentZoomIndex);
    }

    public static void nextZoom() {
        currentZoomIndex = (currentZoomIndex + 1) % zoomLevels.size();
    }

    public static void prevZoom() {
        currentZoomIndex = (currentZoomIndex - 1 + zoomLevels.size()) % zoomLevels.size();
    }
}