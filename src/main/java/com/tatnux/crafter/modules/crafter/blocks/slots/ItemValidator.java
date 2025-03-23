package com.tatnux.crafter.modules.crafter.blocks.slots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public interface ItemValidator extends IItemHandler {

    boolean isItemValidForSlot(int slot, ItemStack stack);

}
