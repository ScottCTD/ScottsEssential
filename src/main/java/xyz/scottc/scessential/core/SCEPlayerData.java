package xyz.scottc.scessential.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.api.ISCEPlayerData;
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
public class SCEPlayerData implements ISCEPlayerData {

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

    public SCEPlayerData() {}

    private SCEPlayerData(@NotNull UUID uuid, @Nullable String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.statistics = PlayerStatistics.getInstance(this.uuid, this.playerName);
    }

    /**
     * This method should be used only after player loaded.
     * AttachCapability event happened before player loaded.
     */
    public static @NotNull SCEPlayerData getInstance(PlayerEntity player) {
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

    @Override
    public @NotNull CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        // Info
        nbt.putString("uuid", this.uuid.toString());

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

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        try {
            this.uuid = UUID.fromString(nbt.getString("uuid"));
        } catch (IllegalArgumentException ignore) {}

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

    @Override
    public PlayerStatistics getStatistics() {
        if (this.statistics == null) {
            this.statistics = PlayerStatistics.getInstance(this.player);
        }
        return this.statistics;
    }

    @Override
    public @Nullable CommandTrashcan.Trashcan getTrashcan() {
        return trashcan;
    }

    @Override
    public void setTrashcan(CommandTrashcan.Trashcan trashcan) {
        this.trashcan = trashcan;
    }

    @Override
    public boolean isFlyable() {
        return this.isFlyable;
    }

    /**
     * This method will do nothing if player is null
     * @param flyable flyable
     */
    @Override
    public void setFlyable(boolean flyable) {
        if (this.player.isCreative()) {
            this.isFlyable = true;
            return;
        }
        if (this.player != null) {
            if (flyable) {
                this.player.abilities.allowFlying = true;
                this.player.abilities.isFlying = true;
            } else {
                this.player.abilities.allowFlying = false;
                this.player.abilities.isFlying = false;
                this.canFlyUntil = -1;
            }
            this.player.sendPlayerAbilities();
            this.isFlyable = flyable;
        }
    }

    @Override
    public long getCanFlyUntil() {
        return canFlyUntil;
    }

    @Override
    public void setCanFlyUntil(long canFlyUntil) {
        this.canFlyUntil = canFlyUntil;
    }

    @Override
    public void addTeleportHistory(TeleportPos teleportPos) {
        System.arraycopy(this.teleportHistory, 0, this.teleportHistory, 1, CommandBack.maxBacks - 1);
        this.teleportHistory[0] = teleportPos;
        this.currentBackIndex = 0;
    }

    @Override
    public TeleportPos[] getAllTeleportHistory() {
        return this.teleportHistory;
    }

    @Override
    public @Nullable TeleportPos getTeleportHistory() {
        if (this.currentBackIndex < CommandBack.maxBacks) {
            return this.teleportHistory[this.currentBackIndex];
        } else {
            return null;
        }
    }

    @Override
    public int getCurrentBackIndex() {
        return this.currentBackIndex;
    }

    @Override
    public void setCurrentBackIndex(int index) {
        this.currentBackIndex = index;
    }

    @Override
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

    @Override
    public Map<String, TeleportPos> getHomes() {
        return this.homes;
    }

    @Override
    public @NotNull List<SCEPlayerData> getAllPlayerData() {
        return PLAYER_DATA_LIST;
    }

    @Override
    public @Nullable PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public @Nullable String getName() {
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

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
    @Override
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

    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {

        @SubscribeEvent
        public static void onPlayerSaved(PlayerEvent.SaveToFile event) {
            try {
                File dataFile = new File(Main.PLAYER_DATA_FOLDER.getAbsolutePath() + "/" + event.getPlayerUUID() + ".dat");
                ISCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
                CompressedStreamTools.writeCompressed(data.serializeNBT(), dataFile);
                Main.LOGGER.debug("Successfully save player " + data.getUuid() + " to file!");
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (data.getPlayer() == null) {
                        Main.LOGGER.debug("Removing " + event.getPlayer().getGameProfile().getName() + " from list!");
                        SCEPlayerData.PLAYER_DATA_LIST.remove(SCEPlayerData.getInstance(event.getPlayer()));
                        Main.LOGGER.debug("Removed!");
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Deserialize Player Data
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerLoaded(PlayerEvent.LoadFromFile event) {
            File dataFile = new File(Main.PLAYER_DATA_FOLDER.getAbsolutePath() + "/" + event.getPlayerUUID() + ".dat");
            if (dataFile.exists()) {
                try {
                    CompoundNBT dataNbt = CompressedStreamTools.readCompressed(dataFile);
                    ISCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
                    data.deserializeNBT(dataNbt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
