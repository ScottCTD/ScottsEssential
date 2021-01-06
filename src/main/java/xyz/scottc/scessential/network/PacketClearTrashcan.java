package xyz.scottc.scessential.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.scottc.scessential.core.SCEPlayerData;

import java.util.Optional;
import java.util.function.Supplier;

public class PacketClearTrashcan extends AbstractPacket<PacketClearTrashcan> {

    public PacketClearTrashcan(PacketBuffer buffer) {
        super(buffer);
    }

    public PacketClearTrashcan() {}

    @Override
    public void encode(PacketBuffer buffer) {}

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Optional<ServerPlayerEntity> sender = Optional.ofNullable(context.get().getSender());
            sender.ifPresent(player -> {
                SCEPlayerData data = SCEPlayerData.getInstance(player);
                data.getTrashcan().clear();
                data.getPlayer().openContainer.detectAndSendChanges();
            });
        });
    }
}
