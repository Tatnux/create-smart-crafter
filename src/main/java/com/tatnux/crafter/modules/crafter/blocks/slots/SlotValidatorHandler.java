package com.tatnux.crafter.modules.crafter.blocks.slots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotValidatorHandler extends SlotItemHandler {

    public SlotValidatorHandler(ItemValidator itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (!((ItemValidator) this.getItemHandler()).isItemValidForSlot(getSlotIndex(), stack)) {
            return false;
        }
        return super.mayPlace(stack);
    }
}
