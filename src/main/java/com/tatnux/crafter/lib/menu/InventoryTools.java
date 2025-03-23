package com.tatnux.crafter.lib.menu;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class InventoryTools {

    @Nonnull
    public static ItemStack insertItemRanged(IItemHandler dest, @Nonnull ItemStack stack, int start, int stop, boolean simulate) {
        if (dest != null && !stack.isEmpty()) {
            for(int i = start; i < stop; ++i) {
                stack = dest.insertItem(i, stack, simulate);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }

            return stack;
        } else {
            return stack;
        }
    }

}
