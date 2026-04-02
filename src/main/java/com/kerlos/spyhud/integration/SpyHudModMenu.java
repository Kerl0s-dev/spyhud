package com.kerlos.spyhud.integration;

import com.kerlos.spyhud.config.SpyHudConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class SpyHudModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SpyHudConfigScreen::create;
    }
}