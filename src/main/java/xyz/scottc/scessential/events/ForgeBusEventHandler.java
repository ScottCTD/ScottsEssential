package xyz.scottc.scessential.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.api.ISCEPlayerData;
import xyz.scottc.scessential.capability.CapabilitySCEPlayerData;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.commands.teleport.CommandTPA;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TPARequest;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TextUtils;

import java.io.*;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEventHandler {

    private static File mainFolder;

    private static int counter = 0;

    /**
     * Make flyable player flyable again after respawn.
     * @param e PlayerEvent.PlayerRespawnEvent
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        SCEPlayerData data = SCEPlayerData.getInstance(e.getPlayer());
        if (data.isFlyable()) {
            data.setFlyable(true);
        }
    }

    /**
     * Determine if an tpa request was expired and if a player fly time expired.
     * @param event ServerTickEvent
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (counter >= 20) {
                new Thread(() -> {
                    long now = System.currentTimeMillis();
                    // TPA Request
                    TPARequest.getTpaRequest().values().forEach(request -> {
                        if ((request.getCreateTime() + CommandTPA.maxTPARequestTimeoutSeconds * 1000L) <= now) {
                            TPARequest.getTpaRequest().remove(request.getId());
                            request.getSource().sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "requesttimeout"), request.getTarget().getGameProfile().getName()), false
                            );
                        }
                    });

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

                    // Trashcan count down
                    SCEPlayerData.PLAYER_DATA_LIST.forEach(data -> {
                        CommandTrashcan.Trashcan trashcan = data.getTrashcan();
                        if (trashcan == null) return;
                        long nextCleanTime = trashcan.getLastCleanLong() + CommandTrashcan.cleanTrashcanIntervalSeconds * 1000L;
                        if (nextCleanTime <= now) {
                            trashcan.clear();
                            trashcan.setNextCleanSeconds(CommandTrashcan.cleanTrashcanIntervalSeconds);
                        } else {
                            trashcan.setNextCleanSeconds((int) (nextCleanTime - now) / 1000);
                        }
                    });
                }).start();
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

    @SubscribeEvent
    public static void onPlayerLoaded(PlayerEvent.LoadFromFile event) {
        LazyOptional<ISCEPlayerData> capability = event.getPlayer().getCapability(CapabilitySCEPlayerData.SCE_PLAYER_DATA_CAPABILITY);
        capability.ifPresent(cap -> {
            if (cap instanceof SCEPlayerData) {
                // If the data for current player exist, then just copy it to the cap
                SCEPlayerData instance = SCEPlayerData.getInstance(event.getPlayer());
                int index = SCEPlayerData.PLAYER_DATA_LIST.indexOf(instance);
                if (index != -1) {
                    cap.deserializeNBT(instance.serializeNBT());
                    SCEPlayerData.PLAYER_DATA_LIST.set(index, (SCEPlayerData) cap);
                } else {
                    SCEPlayerData.PLAYER_DATA_LIST.add((SCEPlayerData) cap);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ISCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
        data.setFlyable(data.isFlyable());
    }

    /**
     * Init mod folders and a MinecraftServer instance.
     * Also, deserialize warp data.
     * @param event FMLServerAboutToStartEvent
     */
    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        Main.resetData();
        Main.SERVER = event.getServer();
        // Bascially, this function return a path like .\saves\New World\scessential
        mainFolder = Main.SERVER.func_240776_a_(new FolderName(Main.MODID)).toFile();
        init();

        // Deserialize warps
        File warpDataFile = new File(mainFolder.getPath() + "/" + "warps.json");
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
        Main.LOGGER.info("SCE Successfully initialize directories!");
    }

    /**
     * Serialize warps data.
     * @param event WorldEvent.Save
     */
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        init();
        // Serialize warps
        File warpDataFile = new File(mainFolder.getPath() + "/" + "warps.json");
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

    /**
     * Init mod directory
     * /world/scessential
     * /world/scessential/worlddata
     */
    public static void init() {
        if (mainFolder == null) return;
        if (!mainFolder.exists()) {
            if (!mainFolder.mkdirs()) {
                throw new RuntimeException("Failed to create necessary scessential folder!");
            }
        }
    }

}
