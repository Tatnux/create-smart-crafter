package com.tatnux.crafter.modules.crafter.blocks.inventory;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class AutomationInventory implements IItemHandlerModifiable {

    private final CrafterInventory inventory;


    @Override
    public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
        this.inventory.setStackInSlot(i, itemStack);
    }

    @Override
    public int getSlots() {
        return this.inventory.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
        return this.inventory.getStackInSlot(i);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack itemStack, boolean simulate) {
        return !this.inventory.canAutomationInsert(slot) ? itemStack : this.inventory.insertItem(slot, itemStack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return !this.inventory.canAutomationExtract(slot) ? ItemStack.EMPTY : this.inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int i) {
        return this.inventory.getSlotLimit(i);
    }

    @Override
    public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
        return this.inventory.isItemValid(i, itemStack);
    }

}
