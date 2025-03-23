package com.tatnux.crafter.modules.crafter.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tatnux.crafter.modules.crafter.blocks.inventory.WorkingCraftingInventory;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrafterRecipe implements INBTSerializable<CompoundTag> {

    private final CraftingContainer inv = new WorkingCraftingInventory();
    private CraftingRecipe recipe;

    private Map<Integer, ItemStack> items = HashMap.newHashMap(9);
    private ItemStack output = ItemStack.EMPTY;
    private CraftMode craftMode = CraftMode.EXT;

    public static final Codec<CrafterRecipe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.INT, ItemStack.OPTIONAL_CODEC).fieldOf("items").forGetter(CrafterRecipe::getItems),
                    ItemStack.OPTIONAL_CODEC.fieldOf("output").forGetter(CrafterRecipe::getOutput),
                    CraftMode.CODEC.fieldOf("craftMode").forGetter(CrafterRecipe::getCraftMode)
            ).apply(instance, CrafterRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrafterRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.INT,
                    ItemStack.OPTIONAL_STREAM_CODEC
            ), CrafterRecipe::getItems,
            ItemStack.OPTIONAL_STREAM_CODEC, CrafterRecipe::getOutput,
            CraftMode.STREAM_CODEC, CrafterRecipe::getCraftMode,
            CrafterRecipe::new
    );

    public CrafterRecipe(Map<Integer, ItemStack> items, ItemStack output, CraftMode craftMode) {
        this.items = items;
        this.output = output;
        this.craftMode = craftMode;
    }

    public void setCraftingGrid(List<ItemStack> items, ItemStack output) {
        if (items.size() > this.items.size()) {
            throw new IllegalArgumentException("The size of the items array exceeds the size of the recipe items list.");
        }
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            this.inv.setItem(i, itemStack);
            this.items.put(i, itemStack);
        }
        this.output = output;
    }

    public static Optional<CraftingRecipe> findRecipe(Level level, CraftingContainer container) {
        return level.getRecipeManager().getRecipes().stream()
                .filter(Objects::nonNull)
                .map(RecipeHolder::value)
                .filter(recipe -> recipe instanceof CraftingRecipe craftingRecipe && craftingRecipe.matches(container.asCraftInput(), level))
                .map(CraftingRecipe.class::cast)
                .findFirst();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        ListTag listTag = new ListTag();
        this.items.forEach((i, itemStack) -> {
            if (!itemStack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                itemStack.save(provider, itemTag);
                listTag.add(itemTag);
            }
        });

        CompoundTag tag = new CompoundTag();
        tag.put("Items", listTag);
        if (!this.output.isEmpty()) {
            CompoundTag outputTag = new CompoundTag();
            this.output.save(provider, outputTag);
            tag.put("Output", outputTag);
        }
        tag.putByte("CraftMode", (byte) craftMode.ordinal());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag listTag = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTags = listTag.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < this.items.size()) {
                ItemStack.parse(provider, itemTags).ifPresent(itemStack -> {
                    this.inv.setItem(slot, itemStack);
                    this.items.put(slot, itemStack);
                });
            }
        }
        this.output = nbt.contains("Output") ? ItemStack.parse(provider, nbt.getCompound("Output")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        this.craftMode = CraftMode.values()[nbt.getByte("CraftMode")];
    }

    public Optional<CraftingRecipe> getCachedRecipe(Level level) {
        return Optional.ofNullable(this.recipe)
                .or(() -> findRecipe(level, this.inv).map(craftingRecipe -> this.recipe = craftingRecipe));
    }
}
