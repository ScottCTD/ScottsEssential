package xyz.scottc.scessential.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.scottc.scessential.core.SCEPlayerData;

import java.util.Optional;
import java.util.function.Supplier;

public class PacketClearTrashcan extends AbstractPacket {

    public PacketClearTrashcan(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public PacketClearTrashcan() {}

    @Override
    public void encode(FriendlyByteBuf buffer) {}

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Optional<ServerPlayer> sender = Optional.ofNullable(context.get().getSender());
            sender.ifPresent(player -> {
                SCEPlayerData data = SCEPlayerData.getInstance(player);
                Optional.ofNullable(data.getTrashcan()).ifPresent(trashcan -> {
                    trashcan.clear();
                    player.containerMenu.broadcastChanges();
                });
            });
            context.get().setPacketHandled(true);
        });
    }
}
