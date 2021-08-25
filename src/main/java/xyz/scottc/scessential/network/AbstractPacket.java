package xyz.scottc.scessential.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AbstractPacket {

    // 等同于decode
    public AbstractPacket(FriendlyByteBuf buffer) {}

    public AbstractPacket() {}

    public abstract void encode(FriendlyByteBuf buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> context);
}
