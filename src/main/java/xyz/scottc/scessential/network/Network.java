package xyz.scottc.scessential.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import xyz.scottc.scessential.Main;

public class Network {

    private static final String PROTOCAL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Main.MODID, "main_network_channel"),
            () -> PROTOCAL_VERSION,
            PROTOCAL_VERSION::equals,
            PROTOCAL_VERSION::equals
    );
    private static int id = 0;

    public static void register() {
    }

    public static void sendToClient(ServerPlayerEntity player, Object packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    private static int nextId() {
        return ++id;
    }

}
