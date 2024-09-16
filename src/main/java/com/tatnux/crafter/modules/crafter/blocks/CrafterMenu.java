package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.tatnux.crafter.lib.menu.SlotItemHandlerFactory;
import com.tatnux.crafter.modules.crafter.CrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.slots.InfoSlot;
import com.tatnux.crafter.modules.crafter.blocks.slots.ResultSlot;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CrafterMenu extends MenuBase<CrafterBlockEntity> {

    private final CraftingContainer workInventory = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
        @SuppressWarnings("NullableProblems")
        @Override
        public boolean stillValid(Player var1) {
            return false;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int slot) {
            return ItemStack.EMPTY;
        }
    }, 3, 3);

    private static final int CRAFT_RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CONTAINER_START = 10;
    private static final int RESULT_SLOT = 28;

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public CrafterMenu(MenuType<?> type, int id, Inventory inv, CrafterBlockEntity be) {
        super(type, id, inv, be);

    }

    @Override
    protected void initAndReadInventory(CrafterBlockEntity contentHolder) {

    }

    public static CrafterMenu create(int id, Inventory inv, CrafterBlockEntity be) {
        return new CrafterMenu(CrafterModule.CRAFTER_MENU.get(), id, inv, be);
    }

    @Override
    protected CrafterBlockEntity createOnClient(FriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        CompoundTag readNbt = extraData.readNbt();
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null || readNbt == null) {
            return null;
        }

        BlockEntity blockEntity = world.getBlockEntity(readBlockPos);
        if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
            crafterBlockEntity.readClient(readNbt);
            return crafterBlockEntity;
        }
        return null;
    }

    @Override
    protected void addSlots() {
        this.contentHolder.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            this.addSlot(new InfoSlot(itemHandler, CRAFT_RESULT_SLOT, 222, 57));

            this.addSlots(itemHandler, SlotItemHandler::new, CRAFT_SLOT_START, 186, -2, 3, 3);
            this.addSlots(itemHandler, SlotItemHandler::new, CONTAINER_START, 78, 80, 2, 9);
            this.addSlots(itemHandler, ResultSlot::new, RESULT_SLOT, 38, 80, 2, 2);
        });
        this.addPlayerSlots(58, 167);
    }

    private void addSlots(IItemHandler itemHandler, SlotItemHandlerFactory factory, int index, int x, int y, int row, int col) {
        for (int iRow = 0; iRow < row; ++iRow)
            for (int iCol = 0; iCol < col; ++iCol)
                this.addSlot(factory.on(itemHandler, index + iCol + iRow * col, x + iCol * 18, y + iRow * 18));
    }

    @Override
    protected void saveData(CrafterBlockEntity contentHolder) {

    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickTypeIn, @NotNull Player player) {
        if (slotId < CRAFT_SLOT_START || slotId >= CONTAINER_START) {
            super.clicked(slotId, dragType, clickTypeIn, player);
            return;
        }
        if (clickTypeIn == ClickType.THROW)
            return;

        ItemStack held = this.getCarried();
        if (clickTypeIn == ClickType.CLONE) {
            if (player.isCreative() && held.isEmpty()) {
                ItemStack stackInSlot = this.contentHolder.inventory.getStackInSlot(slotId)
                        .copy();
                stackInSlot.setCount(stackInSlot.getMaxStackSize());
                this.setCarried(stackInSlot);
                return;
            }
            return;
        }

        ItemStack insert;
        if (held.isEmpty()) {
            insert = ItemStack.EMPTY;
        } else {
            insert = held.copy();
            insert.setCount(1);
        }
        this.contentHolder.inventory.setStackInSlot(slotId, insert);
        this.getSlot(slotId).setChanged();

        for (int i = 0; i < 9; i++) {
            this.workInventory.setItem(i, this.contentHolder.inventory.getStackInSlot(i + CRAFT_SLOT_START));
        }
        CrafterRecipe.findRecipe(this.contentHolder.getLevel(), this.workInventory).ifPresentOrElse(recipe -> {
            ItemStack result = recipe.assemble(this.workInventory, this.contentHolder.getLevel().registryAccess());
            this.contentHolder.inventory.setStackInSlot(CRAFT_RESULT_SLOT, result);
        }, () -> this.contentHolder.inventory.setStackInSlot(CRAFT_RESULT_SLOT, ItemStack.EMPTY));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    public void transferRecipe(NonNullList<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            return;
        }

        this.contentHolder.inventory.setStackInSlot(CRAFT_RESULT_SLOT, stacks.get(0));

        for (int i = 1; i < stacks.size(); i++) {
            this.contentHolder.inventory.setStackInSlot(CRAFT_SLOT_START + i - 1, stacks.get(i));
        }
        this.broadcastChanges();
    }
}
