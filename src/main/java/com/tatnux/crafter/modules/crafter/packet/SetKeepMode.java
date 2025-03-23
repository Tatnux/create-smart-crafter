package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.SmartCrafter;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SetKeepMode(boolean keep) implements CrafterPacket {

    public static final CustomPacketPayload.Type<SetKeepMode> TYPE = new CustomPacketPayload.Type<>(SmartCrafter.asResource("keep_mode"));

    public static final StreamCodec<ByteBuf, SetKeepMode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetKeepMode::keep,
            SetKeepMode::new
    );

    @Override
    public void handleSmartCrafterMenu(SmartCrafterMenu menu) {
        menu.contentHolder.setKeepMode(this.keep);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
