package com.tatnux.crafter.modules.crafter.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

@Getter
@AllArgsConstructor
public enum CraftMode {
    EXT("Ext"),
    INT("Int"),
    EXTC("ExtC");

    public static final Codec<CraftMode> CODEC = Codec.STRING.xmap(
            CraftMode::valueOf,
            CraftMode::name
    );

    public static final StreamCodec<ByteBuf, CraftMode> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(
            CraftMode::ordinal,
            CraftMode.values(),
            ByIdMap.OutOfBoundsStrategy.ZERO
    ), CraftMode::ordinal);

    private final String description;
}
