package com.kerlos.spyhud.config;

import com.kerlos.spyhud.hud.anim.HudAnimationType;
import java.util.Arrays;
import java.util.List;

public class SpyHudConfigData {
    public boolean smoothZoom = true;
    public boolean holdToZoom = false;
    public boolean showHud = true;
    public List<Float> zoomLevels = Arrays.asList(.66f, .5f, .33f, .25f, .2f, .05f);
    public float lerp_speed = 0.25f;
    public HudAnimationType animationType = HudAnimationType.NONE;
    public float animationSpeed = 10.0f;

    // Tu peux aussi ajouter d’autres champs : couleur du HUD, position, etc.
}