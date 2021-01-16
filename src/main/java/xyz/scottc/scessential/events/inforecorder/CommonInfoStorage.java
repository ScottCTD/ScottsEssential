package xyz.scottc.scessential.events.inforecorder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.DateUtils;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

public class CommonInfoStorage implements IInfoStorage {

    @ConfigField
    public static String datePattern = "HH:mm:ss MM/dd/yyyy";
    @ConfigField
    public static boolean isRecordPlayerUUID = true;

    private String playerName;
    private UUID playerUUID;
    private long time;
    private TeleportPos pos;
    private String info;

    public CommonInfoStorage() {
        this.time = System.currentTimeMillis();
    }

    public CommonInfoStorage(String playerName, UUID playerUUID, TeleportPos pos, String info) {
        this();
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.pos = pos;
        this.info = info;
    }

    public CommonInfoStorage(PlayerEntity player, String info) {
        this(player.getGameProfile().getName(), player.getGameProfile().getId(), new TeleportPos(player), info);
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

    @Override
    public TeleportPos getPos() {
        return this.pos;
    }

    @Override
    public void setPos(TeleportPos pos) {
        this.pos = pos;
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
        if (isRecordPlayerUUID && this.playerUUID != null) json.addProperty("uuid", this.playerUUID.toString());
        json.addProperty("time", DateUtils.toString(this.time, datePattern));
        json.add("pos", this.pos.toJSON());
        json.addProperty("info", this.info);
        return json;
    }

    @Override
    public void deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            JsonObject json = (JsonObject) jsonElement;
            this.playerName = json.get("playerName").getAsString();
            Optional.ofNullable(json.get("uuid").getAsString()).ifPresent(uuid -> this.playerUUID = UUID.fromString(uuid));
            try {
                this.time = DateUtils.getTime(json.get("time").getAsString(), datePattern);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.pos = new TeleportPos();
            this.pos.fromJSON(json.get("pos").getAsJsonObject());
            this.info = json.get("info").getAsString();
        }
    }
}
