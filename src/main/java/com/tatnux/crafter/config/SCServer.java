package com.tatnux.crafter.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class SCServer extends ConfigBase {

    public final ConfigInt crafterProgressNeeded = i(100, 1, "crafterProgressNeeded", "Base progress needed to craft one item. Higher values slow crafting.");
    public final ConfigInt crafterStressImpact = i(6, 1, "crafterStressImpact", "Stress impact of the Smart Crafter. Higher values require more power to operate.");
    public final ConfigInt speedRatio = i(24, 1, 256, "speedRatio", "Multiplier for how RPM affects crafting speed. Higher values make speed increase crafting rate more.");

    @Override
    public @NotNull String getName() {
        return "server";
    }
}