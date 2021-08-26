package xyz.scottc.scessential.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xyz.scottc.scessential.client.screen.ScreenLeaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketOpenLeaderboard extends AbstractPacket {

    private final Component title;
    private final List<Component> elements;
    private final int size;

    public PacketOpenLeaderboard(FriendlyByteBuf buffer) {
        this.title = buffer.readComponent();
        this.size = buffer.readInt();
        this.elements = new ArrayList<>(this.size);
        for (int i = 0; i < this.size; i++) {
            this.elements.add(buffer.readComponent());
        }
    }

    public PacketOpenLeaderboard(Component title, List<Component> elements, int size) {
        this.title = title;
        this.elements = elements;
        this.size = size;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeComponent(this.title);
        buffer.writeInt(this.size);
        for (Component element : this.elements) {
            buffer.writeComponent(element);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ScreenLeaderboard.open(this.title, this.elements);
            context.get().setPacketHandled(true);
        });

    }
}
