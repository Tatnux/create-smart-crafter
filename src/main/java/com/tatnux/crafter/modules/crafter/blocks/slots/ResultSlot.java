package com.tatnux.crafter.modules.crafter.blocks.slots;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ResultSlot extends SlotValidatorHandler {

    public ResultSlot(ItemValidator itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }


}
