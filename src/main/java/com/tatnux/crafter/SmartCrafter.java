package com.tatnux.crafter;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tatnux.crafter.config.Config;
import com.tatnux.crafter.lib.module.Modules;
import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import com.tatnux.crafter.modules.network.NetworkHandler;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(SmartCrafter.MOD_ID)
public class SmartCrafter {

    public static final String MOD_ID = "smartcrafter";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("smartcrafter",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Smart Crafter"))
                    .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getId())
                    .icon(SmartCrafterModule.SMART_CRAFTER::asStack)
                    .displayItems((pParameters, output) -> {
                        output.accept(SmartCrafterModule.SMART_CRAFTER.asStack());
                    })
                    .build());

    private final Modules modules = new Modules();

    public SmartCrafter() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        REGISTRATE.registerEventListeners(bus);
        CREATIVE_MODE_TABS.register(bus);

        setupModules();
        Config.register(modLoadingContext);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(ClientEvents::onLoadComplete));
    }

    private void setupModules() {
        modules.register(new SmartCrafterModule());
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
