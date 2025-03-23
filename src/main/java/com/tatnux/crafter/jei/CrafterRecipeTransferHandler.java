package com.tatnux.crafter.jei;

import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CrafterRecipeTransferHandler implements IRecipeTransferHandler<SmartCrafterMenu, RecipeHolder<CraftingRecipe>> {

    public static void register(IRecipeTransferRegistration transferRegistry) {
        transferRegistry.addRecipeTransferHandler(new CrafterRecipeTransferHandler(), RecipeTypes.CRAFTING);
    }

    @Override
    @Nonnull
    public Class<SmartCrafterMenu> getContainerClass() {
        return SmartCrafterMenu.class;
    }

    @Override
    public @NotNull Optional<MenuType<SmartCrafterMenu>> getMenuType() {
        return Optional.of(SmartCrafterModule.CRAFTER_MENU.get());
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }


    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(SmartCrafterMenu container, @NotNull RecipeHolder<CraftingRecipe> recipe, IRecipeSlotsView recipeLayout,
                                               @NotNull Player player, boolean maxTransfer, boolean doTransfer) {

        if (doTransfer) {
            List<IRecipeSlotView> slotViews = recipeLayout.getSlotViews();
            NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);
            for (int i = 0; i < slotViews.size(); i++) {
                List<ITypedIngredient<?>> allIngredients = slotViews.get(i).getAllIngredients().toList();
                if (!allIngredients.isEmpty()) {
                    Optional<ItemStack> ingredient = allIngredients.get(0).getIngredient(VanillaTypes.ITEM_STACK);
                    if (ingredient.isPresent()) {
                        items.set(i, ingredient.get());
                    }
                }
            }

            PacketDistributor.sendToServer(new PacketSendRecipe(items));
        }

        return null;
    }
}
