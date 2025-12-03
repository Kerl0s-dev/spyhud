package com.kerlos.spyhud.hud.anim;

public enum HudAnimationType {
    NONE("None"),
    FADE("Fade"),
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