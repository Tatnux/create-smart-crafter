package com.tatnux.crafter.modules.network;

import com.tatnux.crafter.jei.PacketSendRecipe;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import com.tatnux.crafter.modules.crafter.packet.SelectCraftModePacket;
import com.tatnux.crafter.modules.crafter.packet.SelectRecipePacket;
import com.tatnux.crafter.modules.crafter.packet.SetKeepMode;
import com.tatnux.crafter.modules.crafter.packet.UpdateGhostItemsPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {

    private NetworkHandler() {
    }


    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        register(registrar, PacketSendRecipe.TYPE, PacketSendRecipe.STREAM_CODEC, PacketSendRecipe::handle);
        register(registrar, SelectRecipePacket.TYPE, SelectRecipePacket.STREAM_CODEC, SelectRecipePacket::handle);
        register(registrar, SelectCraftModePacket.TYPE, SelectCraftModePacket.STREAM_CODEC, SelectCraftModePacket::handle);
        register(registrar, SetKeepMode.TYPE, SetKeepMode.STREAM_CODEC, SetKeepMode::handle);
        register(registrar, UpdateGhostItemsPacket.TYPE, UpdateGhostItemsPacket.STREAM_CODEC, UpdateGhostItemsPacket::handle);
    }

    private static <T extends CustomPacketPayload> void register(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
        registrar.playBidirectional(type, reader, new DirectionalPayloadHandler<>(handler, handler));
    }


    public static void resetRecipe(byte slot) {
        PacketDistributor.sendToServer(new SelectRecipePacket(slot, true));
    }

    public static void selectSlot(byte slot) {
        PacketDistributor.sendToServer(new SelectRecipePacket(slot, false));
    }

    public static void selectCraftMode(CraftMode mode) {
        PacketDistributor.sendToServer(new SelectCraftModePacket(mode));
    }

    public static void setKeepMode(boolean keepMode) {
        PacketDistributor.sendToServer(new SetKeepMode(keepMode));
    }

    public static void updateGhostItems(boolean reset) {
        PacketDistributor.sendToServer(new UpdateGhostItemsPacket(reset));
    }
}
