package com.kerlos.spyhud.integration;

import com.kerlos.spyhud.SpyHud;
import com.kerlos.spyhud.config.SpyHudConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class SpyHudModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // On renvoie la méthode qui crée ton écran de config
        return SpyHudConfigScreen::create;
    }
}