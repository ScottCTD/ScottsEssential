package xyz.scottc.scessential;

import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.core.SEPlayerData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    private static final FolderName SE_FOLDER = new FolderName(Main.MODID);
    private static MinecraftServer server;


    private static Path main;
    private static Path worldData;
    private static Path playersData;

    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        server = event.getServer();
        init();
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        init();
    }

    @SubscribeEvent
    public static void onPlayerLoaded(PlayerEvent.LoadFromFile event) {
        init();
        Path playerData = playersData.resolve(event.getPlayerUUID() + ".json");
        if (Files.exists(playerData)) {
            try (BufferedReader reader = Files.newBufferedReader(playerData)) {
                JsonObject jsonObject = Main.GSON.fromJson(reader, JsonObject.class);
                SEPlayerData data = SEPlayerData.getInstance(event.getPlayerUUID());
                data.fromJson(jsonObject);
                SEPlayerData.PLAYER_DATA_LIST.add(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSaved(PlayerEvent.SaveToFile event) {
        init();
        Path playerData = playersData.resolve(event.getPlayerUUID() + ".json");
        SEPlayerData data = SEPlayerData.getInstance(event.getPlayerUUID());
        JsonObject jsonObject = data.toJson();
        try (BufferedWriter writer = Files.newBufferedWriter(playerData)) {
            Main.GSON.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多次init防止有人在游戏进行中删掉文件夹
     */
    public static void init() {
        main = createFolder("");
        worldData = createFolder("worlddata");
        playersData = createFolder("playersdata");
    }

    private static Path createFolder(String name) {
        // Bascially, this function return a path like .\saves\New World\scessential
        Path dir = server.func_240776_a_(SE_FOLDER);
        if (!name.isEmpty()) {
            Path realPath = dir.resolve(name);
            if (Files.notExists(realPath)) {
                try {
                    Files.createDirectories(realPath);
                    return realPath;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to create scessential folder!");
                }
            }
        }
        return dir;
    }

}
