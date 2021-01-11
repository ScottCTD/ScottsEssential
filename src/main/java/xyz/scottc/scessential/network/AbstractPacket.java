package xyz.scottc.scessential.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AbstractPacket {

    // 等同于decode
    public AbstractPacket(PacketBuffer buffer) {}

    public AbstractPacket() {}

    public abstract void encode(PacketBuffer buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> context);
}
