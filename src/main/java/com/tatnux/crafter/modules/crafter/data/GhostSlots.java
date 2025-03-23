package com.tatnux.crafter.modules.crafter.data;

import lombok.Data;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class GhostSlots implements INBTSerializable<ListTag> {

    private final Set<GhostSlotEntry> entries = new HashSet<>();

    public void addSlot(ItemStack itemStack, byte slot) {
        this.entries.stream()
                .filter(ghostSlotEntry -> ItemStack.isSameItem(ghostSlotEntry.getItem(), itemStack))
                .findFirst()
                .orElseGet(() -> {
                    GhostSlotEntry ghostSlotEntry = new GhostSlotEntry(itemStack);
                    this.entries.add(ghostSlotEntry);
                    return ghostSlotEntry;
                }).getSlots().add(slot);
    }

    public void clear() {
        this.entries.clear();
    }

    @Override
    public ListTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        ListTag listTag = new ListTag();
        this.entries.forEach(ghostSlotEntry -> listTag.add(ghostSlotEntry.serializeNBT(provider)));
        return listTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, ListTag listTag) {
        this.entries.clear();
        for (int i = 0; i < listTag.size(); i++) {
            this.entries.add(new GhostSlotEntry(provider, listTag.getCompound(i)));
        }
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public boolean mayPlace(byte slot, ItemStack stack) {
        return this.entries.stream()
                .filter(ghostSlotEntry -> ghostSlotEntry.getSlots().contains(slot))
                .findFirst()
                .map(ghostSlotEntry -> ItemStack.isSameItem(ghostSlotEntry.getItem(), stack.copyWithCount(1)))
                .orElse(true);
    }

    @Data
    public static class GhostSlotEntry implements INBTSerializable<CompoundTag> {
        private ItemStack item;
        private final Set<Byte> slots = new HashSet<>();

        public GhostSlotEntry(ItemStack item) {
            this.item = item;
        }

        public GhostSlotEntry(HolderLookup.Provider provider, CompoundTag tag) {
            this.deserializeNBT(provider, tag);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("Item", this.item.save(provider));
            compoundTag.put("Slots", new ByteArrayTag(new ArrayList<>(this.slots)));
            return compoundTag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag compoundTag) {
            this.item = ItemStack.parseOptional(provider, compoundTag.getCompound("Item"));
            this.slots.clear();
            for (byte b : compoundTag.getByteArray("Slots")) {
                this.slots.add(b);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;

            GhostSlotEntry ghostSlot = (GhostSlotEntry) o;
            return Objects.equals(this.item, ghostSlot.item);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.item);
        }
    }
}
