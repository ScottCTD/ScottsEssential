package xyz.scottc.scessential;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.scottc.scessential.config.ModConfig;
import xyz.scottc.scessential.core.PlayerStatistics;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TPARequest;
import xyz.scottc.scessential.core.TeleportPos;

import java.io.File;
import java.util.Optional;

@Mod(Main.MOD_ID)
public class Main {

    public static final String MOD_ID = "scessential";
    public static final String MOD_VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger();
    // SERVER initializer is in ForgeBusEventHandler.onServerAboutToStart
    public static MinecraftServer SERVER = ServerLifecycleHooks.getCurrentServer();

    public static File MAIN_FOLDER;
    public static File PLAYER_DATA_FOLDER;
    public static File INFO_STORAGE_FOLDER;
    public static File WARPS_FILE;
    public static File STATISTICS_FILE;

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);

        // Mod Bus
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SERVER_CONFIG);

        // Forge Bus
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onServerAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldSave);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
    }

    private void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        SERVER = event.getServer();
        // Basically, this function return a path like .\saves\New World\scessential
        MAIN_FOLDER = SERVER.getWorldPath(new LevelResource(MOD_ID)).toFile();
        init();
        Main.LOGGER.info("SCE Successfully initialize directories!");
    }

    private void onWorldSave(WorldEvent.Save event) {
        init();
    }

    private void onServerStopped(FMLServerStoppedEvent event) {
        resetData();
    }

    public static void sendMessageToAllPlayers(Component message, boolean actionBar) {
        new Thread(() -> Optional.ofNullable(SERVER).ifPresent(server -> server.getPlayerList().getPlayers()
                .forEach(player -> player.sendMessage(message, Util.NIL_UUID)))).start();
    }

    public static void resetData() {
        SCEPlayerData.PLAYER_DATA_LIST.clear();
        PlayerStatistics.ALL_STATISTICS.clear();
        TeleportPos.WARPS.clear();
        TPARequest.TPA_REQUEST.clear();
        LOGGER.debug("Successfully reset all data!");
    }

    public static void init() {
        if (MAIN_FOLDER == null) return;
        if (!MAIN_FOLDER.exists()) {
            if (!MAIN_FOLDER.mkdirs()) {
                throw new RuntimeException("Failed to create necessary scessential folder!");
            }
        }
        PLAYER_DATA_FOLDER = new File(MAIN_FOLDER.getAbsolutePath() + "/" + "playerData");
        if (!PLAYER_DATA_FOLDER.exists()) {
            if (!PLAYER_DATA_FOLDER.mkdirs()) {
                throw new RuntimeException("Failed to create necessary player data folder!");
            }
        }
        INFO_STORAGE_FOLDER = new File(MAIN_FOLDER.getAbsolutePath() + "/" + "infoRecorder");
        if (!INFO_STORAGE_FOLDER.exists()) {
            if (!INFO_STORAGE_FOLDER.mkdirs()) {
                throw new RuntimeException("Failed to create necessary info recorder folder!");
            }
        }
        WARPS_FILE = new File(MAIN_FOLDER.getAbsolutePath() + "/" + "warps.dat");
        STATISTICS_FILE = new File(MAIN_FOLDER.getAbsolutePath() + "/" + "statistics.dat");
    }
}
