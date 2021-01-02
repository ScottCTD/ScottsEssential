package xyz.scottc.scessential.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import xyz.scottc.scessential.Config;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 01/02/2021 22:34
 * Every player should have an instance of this class.
 * It contains all the data that a player need like the last teleport time, uuid, name, homes, teleport history, and etc.
 */
public class SEPlayerData {

    // All the player data are stored in it.
    // It will be refilled everytime the server restart.
    public static final List<SEPlayerData> PLAYER_DATA_LIST = new ArrayList<>();

    private UUID uuid;
    private String playerName;

    private final Map<String, TeleportPos> homes = new HashMap<>(Config.maxHomes);

    private final TeleportPos[] teleportHistory = new TeleportPos[Config.maxBacks];
    // 0 -> The most recent teleport
    public int currentBackIndex = 0;

    private long lastSpawnTime = 0;
    private long lastHomeTime = 0;
    private long lastHomeOtherTime = 0;
    private long lastBackTime = 0;
    private long lastRTPTime = 0;
    private long lastWarpTime = 0;
    private long lastTPATime = 0;

    private SEPlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public static SEPlayerData getInstance(GameProfile gameProfile) {
        return getInstance(gameProfile.getId(), gameProfile.getName());
    }

    public static SEPlayerData getInstance(String uuid, String playerName) {
        return getInstance(UUID.fromString(uuid), playerName);
    }

    public static SEPlayerData getInstance(UUID uuid, String playerName) {
        SEPlayerData data = new SEPlayerData(uuid, playerName);
        int i = PLAYER_DATA_LIST.indexOf(data);
        if (i != -1) {
            data = PLAYER_DATA_LIST.get(i);
        } else {
            PLAYER_DATA_LIST.add(data);
        }
        return data;
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
        if (!(o instanceof SEPlayerData)) return false;
        SEPlayerData that = (SEPlayerData) o;
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
