package com.tatnux.crafter;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tatnux.crafter.config.Config;
import com.tatnux.crafter.lib.module.Modules;
import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterBlockEntity;
import com.tatnux.crafter.modules.network.NetworkHandler;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;

@Mod(SmartCrafter.MOD_ID)
public class SmartCrafter {

    public static final String MOD_ID = "smartcrafter";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    private final Modules modules = new Modules();

    public SmartCrafter(IEventBus modEventBus, ModContainer modContainer) {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        REGISTRATE.registerEventListeners(modEventBus);

        setupModules();
        Config.register(modLoadingContext, modContainer);

        modules.initConfig(modEventBus);

        modEventBus.addListener(NetworkHandler::register);
        modEventBus.addListener(EventPriority.LOWEST, SmartCrafterDatagen::gatherData);
    }

    private void setupModules() {
        modules.register(new SmartCrafterModule());
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}
