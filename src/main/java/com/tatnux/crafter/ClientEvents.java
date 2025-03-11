package com.tatnux.crafter;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ClientEvents {

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModContainer createContainer = ModList.get()
                .getModContainerById(SmartCrafter.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("SmartCrafter mod container missing on LoadComplete"));
        createContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, previousScreen) -> new BaseConfigScreen(previousScreen, SmartCrafter.MOD_ID)));
    }

}
