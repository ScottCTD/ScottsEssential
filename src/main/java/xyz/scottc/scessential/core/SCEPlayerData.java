package xyz.scottc.scessential.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.commands.teleport.CommandBack;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 01/02/2021 22:34
 * Every player should have an instance of this class.
 * It contains all the data that a player need like the last teleport time, uuid, name, homes, teleport history, and etc.
 */
public class SCEPlayerData {

    // All the player data are stored in it.
    // It will be refilled everytime the server restart.
    public static final List<SCEPlayerData> PLAYER_DATA_LIST = new ArrayList<>();

    private PlayerEntity player;
    private UUID uuid;
    private String playerName;

    private PlayerStatistics statistics;

    private final Map<String, TeleportPos> homes = new HashMap<>(5);

    private final TeleportPos[] teleportHistory = new TeleportPos[CommandBack.maxBacks];
    // 0 -> The most recent teleport
    private int currentBackIndex = 0;

    private boolean isFlyable;
    private long canFlyUntil = -1;

    private CommandTrashcan.Trashcan trashcan;

    private long lastSpawnTime = 0;
    private long lastHomeTime = 0;
    private long lastHomeOtherTime = 0;
    private long lastBackTime = 0;
    private long lastRTPTime = 0;
    private long lastWarpTime = 0;
    private long lastTPATime = 0;

    private SCEPlayerData(@NotNull UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    /**
     * This method should be used only after player loaded.
     * AttachCapability event happened before player loaded.
     */
    public static @NotNull SCEPlayerData getInstance(@NotNull PlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        SCEPlayerData data = new SCEPlayerData(gameProfile.getId(), gameProfile.getName());
        int i = PLAYER_DATA_LIST.indexOf(data);
        if (i != -1) {
            data = PLAYER_DATA_LIST.get(i);
        } else {
            PLAYER_DATA_LIST.add(data);
        }
        data.player = player;
        data.setFlyable(data.isFlyable);
        return data;
    }

    public @NotNull CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        // Info
        nbt.putString("uuid", this.uuid.toString());
        nbt.putString("name", this.playerName);

        // Fly
        nbt.putBoolean("flyable", this.isFlyable);
        nbt.putLong("canFlyUntil", this.canFlyUntil);

        // Homes
        ListNBT nbtHomes = new ListNBT();
        for (Map.Entry<String, TeleportPos> home : this.homes.entrySet()) {
            CompoundNBT nbtHome = new CompoundNBT();
            nbtHome.putString("name", home.getKey());
            nbtHome.put("pos", home.getValue().serializeNBT());
            nbtHomes.add(nbtHome);
        }
        nbt.put("homes", nbtHomes);

        // Backs
        nbt.putInt("currentBackIndex", this.currentBackIndex);
        ListNBT nbtBacks = new ListNBT();
        for (TeleportPos backPos : this.teleportHistory) {
            if (backPos == null) break;
            nbtBacks.add(backPos.serializeNBT());
        }
        nbt.put("backHistory", nbtBacks);

        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        try {
            this.uuid = UUID.fromString(nbt.getString("uuid"));
        } catch (IllegalArgumentException ignore) {}
        this.playerName = nbt.getString("name");

        this.isFlyable = nbt.getBoolean("flyable");
        this.canFlyUntil = nbt.getLong("canFlyUntil");

        Optional.ofNullable((ListNBT) nbt.get("homes")).ifPresent((nbtHomes) -> {
            for (INBT home : nbtHomes) {
                CompoundNBT temp = (CompoundNBT) home;
                TeleportPos pos = new TeleportPos();
                pos.deserializeNBT(temp.getCompound("pos"));
                this.homes.put(temp.getString("name"), pos);
            }
        });

        this.currentBackIndex = nbt.getInt("currentBackIndex");
        Optional.ofNullable((ListNBT) nbt.get("backHistory")).ifPresent(backs -> {
            int i = 0;
            for (INBT back : backs) {
                CompoundNBT temp = (CompoundNBT) back;
                TeleportPos pos = new TeleportPos();
                pos.deserializeNBT(temp);
                try {
                    this.teleportHistory[i] = pos;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                i++;
            }
        });
    }

    public PlayerStatistics getStatistics() {
        if (!this.player.world.isRemote) {
            this.statistics = PlayerStatistics.getInstance((ServerPlayerEntity) this.player);
        }
        return this.statistics;
    }

    public @Nullable CommandTrashcan.Trashcan getTrashcan() {
        return trashcan;
    }

    public void setTrashcan(CommandTrashcan.Trashcan trashcan) {
        this.trashcan = trashcan;
    }

    public boolean isFlyable() {
        return this.isFlyable;
    }

    /**
     * This method will do nothing if player is null
     * @param flyable flyable
     */
    public void setFlyable(boolean flyable) {
        if (this.player.isCreative()) {
            this.isFlyable = true;
            return;
        }
        if (this.player != null) {
            if (flyable) {
                this.player.abilities.allowFlying = true;
            } else {
                this.player.abilities.allowFlying = false;
                this.player.abilities.isFlying = false;
                this.canFlyUntil = -1;
            }
            this.player.sendPlayerAbilities();
            this.isFlyable = flyable;
        }
    }

    public long getCanFlyUntil() {
        return canFlyUntil;
    }

    public void setCanFlyUntil(long canFlyUntil) {
        this.canFlyUntil = canFlyUntil;
    }

    public void addTeleportHistory(TeleportPos teleportPos) {
        System.arraycopy(this.teleportHistory, 0, this.teleportHistory, 1, CommandBack.maxBacks - 1);
        this.teleportHistory[0] = teleportPos;
        this.currentBackIndex = 0;
    }

    public @Nullable TeleportPos getTeleportHistory() {
        if (this.currentBackIndex < CommandBack.maxBacks) {
            return this.teleportHistory[this.currentBackIndex];
        } else {
            return null;
        }
    }

    public void moveCurrentBackIndex() {
        this.currentBackIndex++;
    }

    public @Nullable TeleportPos getHomePos(String homeName) {
        return this.homes.get(homeName);
    }

    public void delHome(String name) {
        this.homes.remove(name);
    }

    public void setHome(String name, TeleportPos newPos) {
        this.homes.put(name, newPos);
    }

    public Map<String, TeleportPos> getHomes() {
        return this.homes;
    }

    public @Nullable PlayerEntity getPlayer() {
        return this.player;
    }

    public static @Nullable PlayerEntity getPlayer(String playerName) {
        for (SCEPlayerData data : PLAYER_DATA_LIST) {
            if (data.getName().equals(playerName)) {
                return data.getPlayer();
            }
        }
        return null;
    }

    /**
     * Get all names of online players
     * If the name of a player could not be input without quotation marks, format a quotation mark.
     * @return Formatted player names
     */
    public static @NotNull List<String> getAllPlayerNamesFormatted() {
        List<String> result = new ArrayList<>();
        PLAYER_DATA_LIST.forEach(data -> {
            StringBuilder name = new StringBuilder(data.getName());
            for (char c : name.toString().toCharArray()) {
                if (!StringReader.isAllowedInUnquotedString(c)) {
                    name = new StringBuilder("\"" + name + "\"");
                    break;
                }
            }
            result.add(name.toString());
        });
        return result;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public String getName() {
        if (this.playerName == null) {
            this.playerName =  this.player.getGameProfile().getName();
        }
        return this.playerName;
    }

    public long getLastSpawnTime() {
        return this.lastSpawnTime;
    }

    public void setLastSpawnTime(long lastSpawnTime) {
        this.lastSpawnTime = lastSpawnTime;
    }

    public long getLastHomeTime() {
        return lastHomeTime;
    }

    public void setLastHomeTime(long lastHomeTime) {
        this.lastHomeTime = lastHomeTime;
    }

    public long getLastHomeOtherTime() {
        return lastHomeOtherTime;
    }

    public void setLastHomeOtherTime(long lastHomeOtherTime) {
        this.lastHomeOtherTime = lastHomeOtherTime;
    }

    public long getLastBackTime() {
        return lastBackTime;
    }

    public void setLastBackTime(long lastBackTime) {
        this.lastBackTime = lastBackTime;
    }

    public long getLastRTPTime() {
        return lastRTPTime;
    }

    public void setLastRTPTime(long lastRTPTime) {
        this.lastRTPTime = lastRTPTime;
    }

    public long getLastWarpTime() {
        return lastWarpTime;
    }

    public void setLastWarpTime(long lastWarpTime) {
        this.lastWarpTime = lastWarpTime;
    }

    public long getLastTPATime() {
        return lastTPATime;
    }

    public void setLastTPATime(long lastTPATime) {
        this.lastTPATime = lastTPATime;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Deprecated
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", this.uuid.toString());
        jsonObject.addProperty("name", this.playerName);

        jsonObject.addProperty("flyable", this.isFlyable);
        jsonObject.addProperty("canFlyUntil", this.canFlyUntil);

        JsonArray jsonHomes = new JsonArray();
        for (Map.Entry<String, TeleportPos> home : this.homes.entrySet()) {
            JsonObject jsonHome = new JsonObject();
            jsonHome.addProperty("name", home.getKey());
            jsonHome.add("pos", home.getValue().toJSON());
            jsonHomes.add(jsonHome);
        }
        jsonObject.add("homes", jsonHomes);

        jsonObject.addProperty("currentBackIndex", this.currentBackIndex);
        JsonArray jsonBacks = new JsonArray();
        for (TeleportPos backPos : this.teleportHistory) {
            if (backPos == null) break;
            jsonBacks.add(backPos.toJSON());
        }
        jsonObject.add("backHistory", jsonBacks);

        return jsonObject;
    }

    @Deprecated
    public void fromJson(JsonObject jsonObject) {
        if (this.uuid == null || this.playerName == null) {
            this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
            this.playerName = jsonObject.get("name").getAsString();
        }

        this.isFlyable = jsonObject.get("flyable").getAsBoolean();
        this.canFlyUntil = jsonObject.get("canFlyUntil").getAsLong();

        JsonArray jsonHomes = jsonObject.get("homes").getAsJsonArray();
        for (JsonElement home : jsonHomes) {
            JsonObject temp = home.getAsJsonObject();
            TeleportPos pos = new TeleportPos();
            pos.fromJSON(temp.get("pos").getAsJsonObject());
            this.homes.put(temp.get("name").getAsString(), pos);
        }

        this.currentBackIndex = jsonObject.get("currentBackIndex").getAsInt();
        JsonArray backs = jsonObject.get("backHistory").getAsJsonArray();
        int i = 0;
        for (JsonElement back : backs) {
            JsonObject temp = back.getAsJsonObject();
            TeleportPos pos = new TeleportPos();
            pos.fromJSON(temp);
            try {
                this.teleportHistory[i] = pos;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            i++;
        }
    }

    /**
     * Compare two player data based on uuid.
     * @param o Object being compared.
     * @return true if equal
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SCEPlayerData)) return false;
        SCEPlayerData that = (SCEPlayerData) o;
        return this.uuid.equals(that.uuid);
    }

    /**
     * To UUID
     * @return The uuid string of this player
     */
    @Override
    public String toString() {
        return this.uuid.toString();
    }

    @Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {

        @SubscribeEvent
        public static void onPlayerSaved(PlayerEvent.SaveToFile event) {
            try {
                File dataFile = new File(Main.PLAYER_DATA_FOLDER.getAbsolutePath() + "/" + event.getPlayerUUID() + ".dat");
                SCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
                CompressedStreamTools.writeCompressed(data.serializeNBT(), dataFile);
                Main.LOGGER.debug("Successfully save player " + data.getUuid() + " to file!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            // delay to remove player because this event is fired before SaveToFile event.
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SCEPlayerData.PLAYER_DATA_LIST.remove(SCEPlayerData.getInstance(event.getPlayer()));
            }).start();
        }

        // Deserialize Player Data
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerLoaded(PlayerEvent.LoadFromFile event) {
            File dataFile = new File(Main.PLAYER_DATA_FOLDER.getAbsolutePath() + "/" + event.getPlayerUUID() + ".dat");
            if (dataFile.exists()) {
                try {
                    CompoundNBT dataNbt = CompressedStreamTools.readCompressed(dataFile);
                    SCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
                    data.deserializeNBT(dataNbt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            if (!entity.world.isRemote) {
                if (entity instanceof PlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity) entity;
                    SCEPlayerData data = SCEPlayerData.getInstance(player);
                    data.addTeleportHistory(new TeleportPos(player));
                }
            }
        }

        /**
         * Fix the bug that flyable player not fly after logged in.
         */
        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            SCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
            data.setFlyable(data.isFlyable());
        }

        /**
         * Let flyable player flyable again after respawn.
         */
        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
            SCEPlayerData data = SCEPlayerData.getInstance(e.getPlayer());
            data.setFlyable(data.isFlyable());
        }

        /**
         * Let creative player flyable or not after he/she switching to survival mode.
         */
        @SubscribeEvent
        public static void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
            if (event.getCurrentGameMode().isCreative() && event.getNewGameMode().isSurvivalOrAdventure()) {
                new Thread(() -> {
                    // after game mode changed, if player is flyable, then flyable
                    SCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    data.setFlyable(data.isFlyable());
                }).start();
            }
        }
    }
}
