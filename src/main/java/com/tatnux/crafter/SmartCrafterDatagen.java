package com.tatnux.crafter;

import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static com.tatnux.crafter.SmartCrafter.MOD_ID;
import static com.tatnux.crafter.SmartCrafter.REGISTRATE;

public class SmartCrafterDatagen {

    @SuppressWarnings("UnstableApiUsage")
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(true, REGISTRATE.setDataProvider(new RegistrateDataProvider(REGISTRATE, MOD_ID, event)));
    }

}
