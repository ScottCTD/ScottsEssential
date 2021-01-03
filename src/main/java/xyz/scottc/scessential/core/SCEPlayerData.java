package xyz.scottc.scessential.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import xyz.scottc.scessential.Config;

import javax.annotation.Nullable;
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

    private final Map<String, TeleportPos> homes = new HashMap<>(5);

    private final TeleportPos[] teleportHistory = new TeleportPos[Config.maxBacks];
    // 0 -> The most recent teleport
    public int currentBackIndex = 0;

    private boolean isFlyable;
    private long canFlyUntil = -1;

    private long lastSpawnTime = 0;
    private long lastHomeTime = 0;
    private long lastHomeOtherTime = 0;
    private long lastBackTime = 0;
    private long lastRTPTime = 0;
    private long lastWarpTime = 0;
    private long lastTPATime = 0;

    private SCEPlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public static SCEPlayerData getInstance(PlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        SCEPlayerData instance = getInstance(gameProfile.getId(), gameProfile.getName());
        instance.player = player;
        instance.setFlyable(instance.isFlyable);
        return instance;
    }

    public static SCEPlayerData getInstance(GameProfile gameProfile) {
        return getInstance(gameProfile.getId(), gameProfile.getName());
    }

    public static SCEPlayerData getInstance(UUID uuid, String playerName) {
        SCEPlayerData data = new SCEPlayerData(uuid, playerName);
        int i = PLAYER_DATA_LIST.indexOf(data);
        if (i != -1) {
            data = PLAYER_DATA_LIST.get(i);
        } else {
            PLAYER_DATA_LIST.add(data);
        }
        return data;
    }

    public boolean isFlyable() {
        return this.isFlyable;
    }

    /**
     * This method will do nothing if player is null
     * @param flyable flyable
     */
    public void setFlyable(boolean flyable) {
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
        System.arraycopy(this.teleportHistory, 0, this.teleportHistory, 1, Config.maxBacks - 1);
        this.teleportHistory[0] = teleportPos;
        this.currentBackIndex = 0;
    }

    public TeleportPos getTeleportHistory() {
        if (this.currentBackIndex < Config.maxBacks) {
            return this.teleportHistory[this.currentBackIndex];
        } else {
            return null;
        }
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

    public PlayerEntity getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getPlayerName() {
        return playerName;
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
            this.teleportHistory[i] = pos;
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
}
