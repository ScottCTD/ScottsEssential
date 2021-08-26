package xyz.scottc.scessential.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import xyz.scottc.scessential.Main;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Network {

    private static final String PROTOCAL_VERSION = "1.0";
    public static SimpleChannel INSTANCE;
    private static int id = 0;

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Main.MOD_ID, "main"),
                () -> PROTOCAL_VERSION,
                PROTOCAL_VERSION::equals,
                PROTOCAL_VERSION::equals
        );
        Network.register();
    }

    public static void register() {
        INSTANCE.registerMessage(
                nextId(),
                PacketClearTrashcan.class,
                AbstractPacket::encode,
                PacketClearTrashcan::new,
                PacketClearTrashcan::handle
        );
        INSTANCE.registerMessage(
                nextId(),
                PacketOpenLeaderboard.class,
                PacketOpenLeaderboard::encode,
                PacketOpenLeaderboard::new,
                PacketOpenLeaderboard::handle
        );
        INSTANCE.registerMessage(
                nextId(),
                PacketChangeLeaderboard.class,
                PacketChangeLeaderboard::encode,
                PacketChangeLeaderboard::new,
                PacketChangeLeaderboard::handle
        );
    }

    public static void sendToPlayerClient(ServerPlayer player, AbstractPacket packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(AbstractPacket packet) {
        INSTANCE.sendToServer(packet);
    }

    private static int nextId() {
        return ++id;
    }

}
