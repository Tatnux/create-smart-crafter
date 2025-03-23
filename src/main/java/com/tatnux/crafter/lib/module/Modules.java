package com.tatnux.crafter.lib.module;


import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterBlockEntity;
import net.neoforged.bus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;


public class Modules {
    private final List<IModule> modules = new ArrayList<>();

    public void register(IModule module) {
        this.modules.add(module);
    }

    public void initConfig(IEventBus bus) {
        this.modules.forEach(module -> module.initConfig(bus));
    }
}