package xyz.scottc.scessential.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.scottc.scessential.Config;

import javax.annotation.Nullable;
import java.util.*;

public class SEPlayerData {

    public static final List<SEPlayerData> PLAYER_DATA_LIST = new ArrayList<>();

    private UUID uuid;

    private final Map<String, TeleportPos> homes = new HashMap<>();

    private final TeleportPos[] teleportHistory = new TeleportPos[Config.maxBacks];
    // 0 -> The most recent teleport
    public int currentBackIndex = 0;

    private long lastSpawnTime = 0;
    private long lastHomeTime = 0;
    private long lastBackTime = 0;

    private SEPlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public static SEPlayerData getInstance(UUID uuid) {
        SEPlayerData data = new SEPlayerData(uuid);
        int i = PLAYER_DATA_LIST.indexOf(data);
        if (i != -1) {
            data = PLAYER_DATA_LIST.get(i);
        } else {
            PLAYER_DATA_LIST.add(data);
        }
        return data;
    }

    public static SEPlayerData getInstance(String uuid) {
        return getInstance(UUID.fromString(uuid));
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

    public long getLastBackTime() {
        return lastBackTime;
    }

    public void setLastBackTime(long lastBackTime) {
        this.lastBackTime = lastBackTime;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", this.uuid.toString());

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
            jsonBacks.add(backPos.toJSON());
        }
        jsonObject.add("backHistory", jsonBacks);

        return jsonObject;
    }

    public void fromJson(JsonObject jsonObject) {
        if (this.uuid == null) {
            this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SEPlayerData)) return false;
        SEPlayerData that = (SEPlayerData) o;
        return this.uuid.equals(that.uuid);
    }

    @Override
    public String toString() {
        return this.uuid.toString();
    }
}
