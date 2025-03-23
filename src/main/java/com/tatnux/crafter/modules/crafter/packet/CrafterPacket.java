package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface CrafterPacket extends CustomPacketPayload {

    default void handle(IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof SmartCrafterMenu menu) {
            this.handleSmartCrafterMenu(menu);
        }
    }

    void handleSmartCrafterMenu(SmartCrafterMenu menu);

}
