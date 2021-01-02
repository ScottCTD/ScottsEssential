package xyz.scottc.scessential.core;

import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TPARequest {

    private static final Map<Long, TPARequest> TPA_REQUEST = new HashMap<>();

    private final long id;
    private final ServerPlayerEntity source;
    private final ServerPlayerEntity target;
    private final long createTime;

    private TPARequest(long id, ServerPlayerEntity source, ServerPlayerEntity target, boolean reverse) {
        this.id = id;
        if (reverse) {
            this.target = source;
            this.source = target;
        } else {
            this.source = source;
            this.target = target;
        }
        this.createTime = System.currentTimeMillis();
    }

    public static @Nonnull TPARequest getInstance(long id, ServerPlayerEntity source, ServerPlayerEntity target, boolean reverse) {
        TPARequest instance = getInstance(id);
        if (instance == null) {
            instance = new TPARequest(id, source, target, reverse);
            TPA_REQUEST.put(id, instance);
        }
        return instance;
    }

    public static @Nullable TPARequest getInstance(long id) {
        return TPA_REQUEST.get(id);
    }

    public long getId() {
        return id;
    }

    public ServerPlayerEntity getSource() {
        return source;
    }

    public ServerPlayerEntity getTarget() {
        return target;
    }

    public long getCreateTime() {
        return createTime;
    }

    public static Map<Long, TPARequest> getTpaRequest() {
        return TPA_REQUEST;
    }
}
