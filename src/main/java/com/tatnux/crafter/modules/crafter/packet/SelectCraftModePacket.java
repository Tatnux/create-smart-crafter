package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.SmartCrafter;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SelectCraftModePacket(CraftMode mode) implements CrafterPacket {

    public static final CustomPacketPayload.Type<SelectCraftModePacket> TYPE = new CustomPacketPayload.Type<>(SmartCrafter.asResource("craft_mode"));

    public static final StreamCodec<ByteBuf, SelectCraftModePacket> STREAM_CODEC = StreamCodec.composite(
            CraftMode.STREAM_CODEC, SelectCraftModePacket::mode,
            SelectCraftModePacket::new
    );

    @Override
    public void handleSmartCrafterMenu(SmartCrafterMenu menu) {
        menu.contentHolder.setCraftMode(this.mode);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
