package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.SmartCrafter;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SelectRecipePacket(byte slot, boolean reset) implements CrafterPacket {

    public static final CustomPacketPayload.Type<SelectRecipePacket> TYPE = new CustomPacketPayload.Type<>(SmartCrafter.asResource("select_recipe"));

    public static final StreamCodec<ByteBuf, SelectRecipePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, SelectRecipePacket::slot,
            ByteBufCodecs.BOOL, SelectRecipePacket::reset,
            SelectRecipePacket::new
    );

    @Override
    public void handleSmartCrafterMenu(SmartCrafterMenu menu) {
        if (this.reset) {
            menu.contentHolder.reset(this.slot);
        } else {
            menu.contentHolder.select(this.slot);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
