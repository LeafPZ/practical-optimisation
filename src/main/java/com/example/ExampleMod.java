package com.example;

import dev.aoqia.leaf.api.ModInitializer;

import zombie.debug.DebugType;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "modid";
    public static final DebugType LOGGER = DebugType.General;

    @Override
    public void onInitialize() {
        LOGGER.debugln("[%s] %s", MOD_ID, "Hello Leaf World!!! >w<");
    }
}
