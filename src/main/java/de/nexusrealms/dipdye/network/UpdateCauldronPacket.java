package de.nexusrealms.dipdye.network;


import de.nexusrealms.dipdye.DipDye;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record UpdateCauldronPacket(BlockPos pos) implements ReceiverPacket<ClientPlayNetworking.Context> {
    public static final Id<UpdateCauldronPacket> ID = new Id<>(DipDye.id("update_cauldron"));
    public static final PacketCodec<ByteBuf, UpdateCauldronPacket> PACKET_CODEC = BlockPos.PACKET_CODEC.xmap(UpdateCauldronPacket::new, UpdateCauldronPacket::pos);
    @Override
    public void receive(ClientPlayNetworking.Context context) {
        context.player().clientWorld.updateListeners(pos, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
