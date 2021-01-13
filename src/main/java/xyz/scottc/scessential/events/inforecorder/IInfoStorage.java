package xyz.scottc.scessential.events.inforecorder;

import com.google.gson.JsonElement;

import java.util.UUID;

public interface IInfoStorage {

    String getPlayerName();

    void setPlayerName(String name);

    UUID getPlayerUUID();

    void setPlayerUUID(UUID uuid);

    long getTime();

    void setTime(long time);

    JsonElement serializeJson();

    void deserializeJson(JsonElement json);

}
