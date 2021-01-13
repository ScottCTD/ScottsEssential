package xyz.scottc.scessential.events.inforecorder;

import com.google.gson.JsonElement;
import xyz.scottc.scessential.core.TeleportPos;

import java.util.UUID;

public interface IInfoStorage {

    String getPlayerName();

    void setPlayerName(String name);

    UUID getPlayerUUID();

    void setPlayerUUID(UUID uuid);

    long getTime();

    void setTime(long time);

    TeleportPos getPos();

    void setPos(TeleportPos pos);

    JsonElement serializeJson();

    void deserializeJson(JsonElement json);

}
