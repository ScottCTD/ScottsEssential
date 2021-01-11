package xyz.scottc.scessential.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.scottc.scessential.client.screen.ScreenLeaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketOpenLeaderboard extends AbstractPacket {

    private final ITextComponent title;
    private final List<ITextComponent> elements;
    private final int size;

    public PacketOpenLeaderboard(PacketBuffer buffer) {
        this.title = buffer.readTextComponent();
        this.size = buffer.readInt();
        this.elements = new ArrayList<>(this.size);
        for (int i = 0; i < this.size; i++) {
            this.elements.add(buffer.readTextComponent());
        }
    }

    public PacketOpenLeaderboard(ITextComponent title, List<ITextComponent> elements, int size) {
        this.title = title;
        this.elements = elements;
        this.size = size;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeTextComponent(this.title);
        buffer.writeInt(this.size);
        for (ITextComponent element : this.elements) {
            buffer.writeTextComponent(element);
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
