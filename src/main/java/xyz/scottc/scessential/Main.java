package xyz.scottc.scessential;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.scottc.scessential.config.ModConfig;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TPARequest;
import xyz.scottc.scessential.core.TeleportPos;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "scessential";
    public static final String MOD_VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // ServerLifecycleHooks.getCurrentServer() seems not very good -> null pointer
    // SERVER initializer is in ForgeBusEventHandler.onServerAboutToStart
    public static MinecraftServer SERVER = ServerLifecycleHooks.getCurrentServer();

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SERVER_CONFIG);
    }

    public static void sendMessageToAllPlayers(ITextComponent message, boolean actionBar) {
        new Thread(() -> SERVER.getPlayerList().getPlayers().forEach(player -> player.sendStatusMessage(message, actionBar))).start();
    }

    public static void resetData() {
        SCEPlayerData.PLAYER_DATA_LIST.clear();
        TeleportPos.WARPS.clear();
        TPARequest.TPA_REQUEST.clear();
    }
}
