package xyz.scottc.scessential.events.inforecorder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.DateUtils;

import java.text.ParseException;
import java.util.UUID;

public class CommonInfoStorage implements IInfoStorage {

    // TODO
    @ConfigField
    public static String datePattern = "hh:mm:ss MM/dd/yyyy";

    private String playerName;
    private UUID playerUUID;
    private long time;
    private String info;

    public CommonInfoStorage() {
        this.time = System.currentTimeMillis();
    }

    public CommonInfoStorage(String playerName, UUID playerUUID, String info) {
        this();
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.info = info;
    }

    public CommonInfoStorage(PlayerEntity player, String info) {
        this(player.getGameProfile().getName(), player.getGameProfile().getId(), info);
    }

    @Override
    public String getPlayerName() {
        return this.playerName;
    }

    @Override
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    @Override
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Override
    public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public JsonElement serializeJson() {
        JsonObject json = new JsonObject();
        json.addProperty("playerName", this.playerName);
        json.addProperty("uuid", this.playerUUID.toString());
        json.addProperty("time", DateUtils.toString(this.time, datePattern));
        json.addProperty("info", this.info);
        return json;
    }

    @Override
    public void deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            JsonObject json = (JsonObject) jsonElement;
            this.playerName = json.get("playerName").getAsString();
            this.playerUUID = UUID.fromString(json.get("uuid").getAsString());
            try {
                this.time = DateUtils.getTime(json.get("time").getAsString(), datePattern);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.info = json.get("info").getAsString();
        }
    }
}
