package xyz.scottc.scessential;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TPARequest;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TextUtils;

import java.io.*;
import java.util.Collection;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    private static MinecraftServer server;

    private static File mainFolder;
    private static File worldDataFolder;
    private static File playersDataFolder;

    private static int counter = 0;

    /**
     * Determine if an tpa request was expired and if a player fly time expired.
     * @param event ServerTickEvent
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (counter >= 20) {
                long now = System.currentTimeMillis();
                // TPA Request
                Collection<TPARequest> requests = TPARequest.getTpaRequest().values();
                for (TPARequest request : requests) {
                    if ((request.getCreateTime() + Config.maxTPARequestTimeoutSeconds * 1000L) <= now) {
                        TPARequest.getTpaRequest().remove(request.getId());
                        request.getSource().sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                                TextUtils.getTranslationKey("message", "requesttimeout"), request.getTarget().getGameProfile().getName()), false
                        );
                    }
                }
                // Player Fly Time
                SCEPlayerData.PLAYER_DATA_LIST.stream().filter(player -> player.getPlayer() != null &&
                                                              player.isFlyable() &&
                                                              player.getCanFlyUntil() != -1 &&
                                                              player.getCanFlyUntil() <= now)
                        .forEach(player -> {
                            player.setFlyable(false);
                            player.setCanFlyUntil(-1);
                            player.getPlayer().sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "cantflynow")), false);

                        });
            }
            counter++;
        }
    }

    /**
     * Listen this event for adding a new back history for a player when that player died, allowing player use /back
     * to return to the death pos.
     * @param event Player Death event
     */
    @SubscribeEvent
    public static void onPlayerDied(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof ServerPlayerEntity) {
            SCEPlayerData.getInstance(((ServerPlayerEntity) entity)).addTeleportHistory(new TeleportPos(((ServerPlayerEntity) entity).getServerWorld().getDimensionKey(), entity.getPosition()));
        }
    }

    /**
     * Init mod folders and a MinecraftServer instance.
     * Also, deserialize warp data.
     * @param event FMLServerAboutToStartEvent
     */
    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        server = event.getServer();
        // Bascially, this function return a path like .\saves\New World\scessential
        mainFolder = server.func_240776_a_(new FolderName(Main.MODID)).toFile();
        worldDataFolder = new File(mainFolder.getPath() + "/" + "worlddata");
        playersDataFolder = new File(mainFolder.getPath() + "/" + "playersdata");
        init();

        // Deserialize warps
        File warpDataFile = new File(worldDataFolder.getPath() + "/" + "warps.json");
        if (warpDataFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(warpDataFile)))) {
                JsonObject jsonObject = Main.GSON.fromJson(reader, JsonObject.class);
                if (jsonObject.get("name").getAsString().equals("warps")) {
                    JsonArray warps = jsonObject.get("data").getAsJsonArray();
                    for (JsonElement warp : warps) {
                        JsonObject temp = warp.getAsJsonObject();
                        TeleportPos pos = new TeleportPos();
                        pos.fromJSON(temp.get("pos").getAsJsonObject());
                        TeleportPos.WARPS.put(temp.get("name").getAsString(), pos);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Serialize warps data.
     * @param event WorldEvent.Save
     */
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        init();
        // Serialize warps
        File warpDataFile = new File(worldDataFolder.getPath() + "/" + "warps.json");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(warpDataFile)))) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", "warps");
            JsonArray warps = new JsonArray();
            for (Map.Entry<String, TeleportPos> warp : TeleportPos.WARPS.entrySet()) {
                JsonObject temp = new JsonObject();
                temp.addProperty("name", warp.getKey());
                temp.add("pos", warp.getValue().toJSON());
                warps.add(temp);
            }
            jsonObject.add("data", warps);
            Main.GSON.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoaded(PlayerEvent.LoadFromFile event) {
        init();
        String uuid = event.getPlayerUUID();
        File file = new File(playersDataFolder.getPath() + "/" + uuid + ".json");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                JsonObject jsonObject = Main.GSON.fromJson(reader, JsonObject.class);
                SCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer().getGameProfile());
                data.fromJson(jsonObject);
                SCEPlayerData.PLAYER_DATA_LIST.add(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSaved(PlayerEvent.SaveToFile event) {
        init();
        String uuid = event.getPlayerUUID();
        File file = new File(playersDataFolder + "/" + uuid + ".json");
        SCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer().getGameProfile());
        JsonObject jsonObject = data.toJson();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            Main.GSON.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Init mod directory
     * /world/scessential
     * /world/scessential/playersdata
     * /world/scessential/worlddata
     */
    public static void init() {
        if (mainFolder == null || worldDataFolder == null || playersDataFolder == null) return;
        if (!mainFolder.exists()) {
            if (!mainFolder.mkdirs()) {
                throw new RuntimeException("Failed to create necessary scessential folder!");
            }
        }
        if (!worldDataFolder.exists()) {
            if (!worldDataFolder.mkdirs()) {
                throw new RuntimeException("Failed to create necessary scessential/worlddata folder!");
            }
        }
        if (!playersDataFolder.exists()) {
            if (!playersDataFolder.mkdirs()) {
                throw new RuntimeException("Failed to create necessary scessential/playersdata folder!");
            }
        }
    }

}
