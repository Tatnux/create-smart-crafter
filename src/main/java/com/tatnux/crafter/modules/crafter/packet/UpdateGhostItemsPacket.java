package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.SmartCrafter;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record UpdateGhostItemsPacket(boolean reset) implements CrafterPacket {

    public static final CustomPacketPayload.Type<UpdateGhostItemsPacket> TYPE = new CustomPacketPayload.Type<>(SmartCrafter.asResource("update_ghost_items"));

    public static final StreamCodec<ByteBuf, UpdateGhostItemsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, UpdateGhostItemsPacket::reset,
            UpdateGhostItemsPacket::new
    );

    @Override
    public void handleSmartCrafterMenu(SmartCrafterMenu menu) {
        menu.contentHolder.updateGhostItems(this.reset);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
