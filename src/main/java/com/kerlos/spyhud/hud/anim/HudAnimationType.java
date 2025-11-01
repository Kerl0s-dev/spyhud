package com.kerlos.spyhud.hud.anim;

public enum HudAnimationType {
    FADE("Fade"),
    SLIDE_LEFT("Slide Left"),
    SLIDE_RIGHT("Slide Right"),
    POP_IN("Pop In"),
    POP_OUT("Pop Out");

    private final String label;

    HudAnimationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}