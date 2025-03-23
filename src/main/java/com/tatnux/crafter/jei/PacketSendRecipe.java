package com.tatnux.crafter.jei;

import com.tatnux.crafter.SmartCrafter;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import com.tatnux.crafter.modules.crafter.packet.CrafterPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public record PacketSendRecipe(List<ItemStack> stacks) implements CrafterPacket {

    public static final CustomPacketPayload.Type<PacketSendRecipe> TYPE = new CustomPacketPayload.Type<>(SmartCrafter.asResource("send_recipe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSendRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, PacketSendRecipe::stacks,
            PacketSendRecipe::new
    );

    @Override
    public void handleSmartCrafterMenu(SmartCrafterMenu menu) {
        menu.contentHolder.transferRecipe(this.stacks);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}