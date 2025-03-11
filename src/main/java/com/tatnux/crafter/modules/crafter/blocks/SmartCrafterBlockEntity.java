package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tatnux.crafter.config.Config;
import com.tatnux.crafter.lib.list.NonNullListFactory;
import com.tatnux.crafter.lib.menu.InventoryTools;
import com.tatnux.crafter.lib.menu.UndoableItemHandler;
import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.inventory.AutomationInventory;
import com.tatnux.crafter.modules.crafter.blocks.inventory.CrafterInventory;
import com.tatnux.crafter.modules.crafter.blocks.inventory.WorkingCraftingInventory;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import com.tatnux.crafter.modules.crafter.data.GhostSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu.*;
import static com.tatnux.crafter.modules.crafter.data.CraftMode.EXTC;
import static com.tatnux.crafter.modules.crafter.data.CraftMode.INT;

public class SmartCrafterBlockEntity extends KineticBlockEntity implements MenuProvider {

    public final CrafterInventory inventory;
    private final AutomationInventory automationInventory;
    private final LazyOptional<IItemHandler> inventoryProvider;

    public final NonNullList<CrafterRecipe> recipes;
    public byte selectedRecipeIndex = 0;
    public boolean keepMode = false;
    public GhostSlots ghostSlots;
    private final CraftingContainer workInventory = new WorkingCraftingInventory();
    private float craftingProgress = 0; // Crafting progress accumulator, starts at 0
    private boolean cannotCraftCurrently = false;

    public SmartCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new CrafterInventory(this);
        this.automationInventory = new AutomationInventory(this.inventory);
        this.inventoryProvider = LazyOptional.of(() -> this.automationInventory);
        this.ghostSlots = new GhostSlots();
        this.recipes = NonNullListFactory.fill(9, CrafterRecipe::new);
    }

    /// From Client
    public void select(byte index) {
        // Already selected, return
        if (this.selectedRecipeIndex == index) return;

        // The recipe is already cached so we just have to update the inventory
        this.selectedRecipeIndex = index;
        CrafterRecipe crafterRecipe = this.getSelectedRecipe();
        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, crafterRecipe.getOutput());
        for (int i = 0; i < crafterRecipe.getItems().size(); i++) {
            this.inventory.setStackInSlot(CRAFT_SLOT_START + i, crafterRecipe.getItems().get(i));
        }
    }

    public void reset(byte index) {
        if (this.selectedRecipeIndex != index) return; // Illegal State

        this.transferRecipe(NonNullList.withSize(10, ItemStack.EMPTY));
        this.retryCrafting();
    }

    public void retryCrafting() {
        this.cannotCraftCurrently = false;
    }

    // From Client
    public void setCraftMode(CraftMode mode) {
        this.getSelectedRecipe().setCraftMode(mode);
        this.notifyUpdate();
    }

    // From Client
    public void setKeepMode(boolean keep) {
        this.keepMode = keep;
        this.notifyUpdate();
    }

    public CrafterRecipe getSelectedRecipe() {
        return this.recipes.get(this.selectedRecipeIndex);
    }

    public void saveRecipe(CraftingRecipe recipe, List<ItemStack> items, ItemStack output) {
        this.getSelectedRecipe().setCraftingGrid(items, output);
        this.getSelectedRecipe().setRecipe(recipe);

        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, output);
        this.retryCrafting();
    }

    public void updateGhostItems(boolean reset) {
        if (reset) {
            this.ghostSlots.clear();
        } else {
            for (byte i = CONTAINER_START; i < RESULT_SLOT_START + RESULT_SLOT_SIZE; i++) {
                ItemStack stack = this.inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    this.ghostSlots.addSlot(stack.copyWithCount(1), i);
                }
            }
        }
        this.notifyUpdate();
        this.retryCrafting();
    }

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot >= CONTAINER_START && slot < RESULT_SLOT_START + RESULT_SLOT_SIZE) {
            return this.ghostSlots.mayPlace((byte) slot, stack);
        }
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (this.isItemHandlerCap(cap)) {
            return this.inventoryProvider.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inventoryProvider.invalidate();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return SmartCrafterModule.SMART_CRAFTER.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return SmartCrafterMenu.create(id, inv, this);
    }

    @Override
    public void tick() {
        super.tick();
        // Skip processing if on the client, no speed, crafting is blocked, or redstone is active
        if (this.level.isClientSide() || this.getSpeed() == 0 || this.cannotCraftCurrently ||
                this.level.getBestNeighborSignal(this.worldPosition) > 0) {
            return;
        }

        // Calculate speed factor based on RPM and configuration
        float speedFactor = Math.abs(this.getSpeed()) / Config.common().speedRatio.get();
        // Accumulate crafting progress each tick
        this.craftingProgress += speedFactor;

        // Craft as many times as progress allows
        float progressNeeded = Config.common().crafterProgressNeeded.get();
        while (this.craftingProgress >= progressNeeded) {
            if (!attemptCrafting()) {
                this.cannotCraftCurrently = true; // Stop if crafting fails (e.g., no resources or space)
                break;
            }
            this.craftingProgress -= progressNeeded; // Deduct progress per craft
        }
    }

    private boolean attemptCrafting() {
        return this.recipes.stream().anyMatch(this::craftRecipe);
    }

    private boolean craftRecipe(CrafterRecipe recipe) {
        Optional<CraftingRecipe> optionalRecipe = recipe.getCachedRecipe(this.level);
        // If the current recipe has no recipe, return
        if (optionalRecipe.isEmpty()) return false;

        UndoableItemHandler undoHandler = new UndoableItemHandler(this.inventory);
        if (!canCraft(recipe, undoHandler)) {
            undoHandler.restore();
            return false;
        }

        CraftingRecipe craftingRecipe = optionalRecipe.get();
        ItemStack result = craftingRecipe.assemble(this.workInventory, this.level.registryAccess());

        CraftMode mode = recipe.getCraftMode();
        if (!result.isEmpty() && this.placeResult(mode, undoHandler, result)) {
            List<ItemStack> remainingItems = craftingRecipe.getRemainingItems(this.workInventory);
            CraftMode remainingMode = mode == EXTC ? INT : mode;
            for (ItemStack item : remainingItems) {
                if (!item.isEmpty() && !this.placeResult(remainingMode, undoHandler, item)) {
                    return false;
                }
            }
            return true;
        }
        // We don't have place. Undo the operation.
        undoHandler.restore();
        return false;
    }

    private boolean canCraft(CrafterRecipe crafterRecipe, UndoableItemHandler undoHandler) {
        int keepCount = this.keepMode ? 1 : 0; //The number of item that will remain in the container slots
        for (int i = 0; i < this.workInventory.getContainerSize(); i++) {
            this.workInventory.setItem(i, ItemStack.EMPTY);
        }

        CraftingRecipe recipe = crafterRecipe.getRecipe();
        int width = recipe instanceof ShapedRecipe shaped ? shaped.getRecipeWidth() : 3;
        int height = recipe instanceof ShapedRecipe shaped ? shaped.getRecipeHeight() : 3;
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = y * width + x;
                if (index < ingredients.size() && ingredients.get(index) != Ingredient.EMPTY) {
                    Ingredient ingredient = ingredients.get(index);
                    for (int j = 0; j < CONTAINER_SIZE; j++) {
                        int slot = CONTAINER_START + j;
                        ItemStack input = undoHandler.getStackInSlot(slot);
                        if (!input.isEmpty() && input.getCount() > keepCount && ingredient.test(input)) {
                            undoHandler.remember(slot);
                            ItemStack consumed = input.split(1);
                            this.workInventory.setItem(y * 3 + x, consumed);
                            break;
                        }
                    }
                }
            }
        }
        return recipe.matches(this.workInventory, this.level);
    }

    private boolean placeResult(CraftMode mode, IItemHandlerModifiable undoHandler, ItemStack result) {
        // Set the target container slots depending on the current mode
        int start = mode == INT ? CONTAINER_START : RESULT_SLOT_START;
        int stop = mode == INT ? CONTAINER_START + CONTAINER_SIZE : RESULT_SLOT_START + RESULT_SLOT_SIZE;
        // Test if the operation is ok
        ItemStack remaining = InventoryTools.insertItemRanged(undoHandler, result, start, stop, true);
        if (remaining.isEmpty()) {
            // Same method but for real (not simulated)
            InventoryTools.insertItemRanged(undoHandler, result, start, stop, false);
            return true;
        }
        return false;
    }

    public void updateWorkInventory() {
        if (this.getLevel().isClientSide) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            this.workInventory.setItem(i, this.inventory.getStackInSlot(i + CRAFT_SLOT_START));
        }
        CrafterRecipe.findRecipe(this.getLevel(), this.workInventory).ifPresentOrElse(recipe -> {
            ItemStack result = recipe.assemble(this.workInventory, this.getLevel().registryAccess());
            this.saveRecipe(recipe, this.workInventory.getItems(), result);
        }, () -> this.saveRecipe(null, this.workInventory.getItems(), ItemStack.EMPTY)); // If no recipe was found, reset the recipe to empty
    }

    public void transferRecipe(NonNullList<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            return;
        }
        this.inventory.setStackInSlot(CRAFT_RESULT_SLOT, stacks.get(0));
        for (int i = 1; i < stacks.size(); i++) {
            this.inventory.setStackInSlot(CRAFT_SLOT_START + i - 1, stacks.get(i));
        }
        this.updateWorkInventory();
        this.setChanged();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("Inventory", this.inventory.serializeNBT()); // Inventory
        tag.putByte("SelectedRecipe", this.selectedRecipeIndex); // SelectedRecipe

        // Create the recipe list
        ListTag recipesTag = new ListTag();
        for (CrafterRecipe recipe : this.recipes) {
            CompoundTag recipeTag = new CompoundTag();
            recipeTag.put("Recipe", recipe.serializeNBT());
            recipesTag.add(recipeTag);
        }
        tag.put("Recipes", recipesTag); // Recipes
        tag.putBoolean("KeepMode", this.keepMode); // KeepMode
        tag.put("GhostSlots", this.ghostSlots.serializeNBT()); // GhostSlots
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.inventory.deserializeNBT(tag.getCompound("Inventory")); // Inventory
        this.selectedRecipeIndex = tag.getByte("SelectedRecipe"); // SelectedRecipe

        // Retrieve the recipes list
        ListTag recipesTag = tag.getList("Recipes", Tag.TAG_COMPOUND); // Recipes
        for (int i = 0; i < recipesTag.size(); i++) {
            CompoundTag recipeTag = recipesTag.getCompound(i);
            this.recipes.get(i).deserializeNBT(recipeTag.getCompound("Recipe"));
        }
        this.keepMode = tag.getBoolean("KeepMode"); // KeepMode
        this.ghostSlots.deserializeNBT(tag.getList("GhostSlots", Tag.TAG_COMPOUND)); // GhostsSlots
    }
}