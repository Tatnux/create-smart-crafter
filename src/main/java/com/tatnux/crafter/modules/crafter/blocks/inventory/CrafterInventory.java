package com.tatnux.crafter.modules.crafter.blocks.inventory;

import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterBlockEntity;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import com.tatnux.crafter.modules.crafter.blocks.slots.ItemValidator;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CrafterInventory extends ItemStackHandler implements ItemValidator {

    private final SmartCrafterBlockEntity blockEntity;

    public CrafterInventory(SmartCrafterBlockEntity blockEntity) {
        super(32);
        this.blockEntity = blockEntity;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        super.setStackInSlot(slot, stack);
        this.blockEntity.sendData();
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.blockEntity.sendData();
        this.blockEntity.setChanged();
        this.blockEntity.retryCrafting();
    }

    public boolean canAutomationInsert(int slot) {
        return slot >= SmartCrafterMenu.CONTAINER_START && slot < SmartCrafterMenu.CONTAINER_START + SmartCrafterMenu.CONTAINER_SIZE;
    }

    public boolean canAutomationExtract(int slot) {
        return slot >= SmartCrafterMenu.RESULT_SLOT_START && slot < SmartCrafterMenu.RESULT_SLOT_START + SmartCrafterMenu.RESULT_SLOT_SIZE;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return this.blockEntity.isItemValidForSlot(slot, stack);
    }
}
