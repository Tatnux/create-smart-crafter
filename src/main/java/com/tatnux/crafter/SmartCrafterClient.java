package com.tatnux.crafter;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SmartCrafter.MOD_ID, dist = Dist.CLIENT)
public class SmartCrafterClient {

    public SmartCrafterClient(IEventBus modEventBus) {
        modEventBus.addListener(SmartCrafterClient::clientInit);
    }

    private static void clientInit(FMLClientSetupEvent event) {
        ModContainer modContainer = ModList.get()
                .getModContainerById(SmartCrafter.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("SmartCrafter mod container missing on LoadComplete"));
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (modContainer1, screen) -> new BaseConfigScreen(screen, modContainer1.getModId()));
    }
}
