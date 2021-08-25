package xyz.scottc.scessential.core;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 01/02/2021 22:34
 */
public class TPARequest {

    // All the tpa requests are stored here.
    public static final Map<Long, TPARequest> TPA_REQUEST = new HashMap<>();

    // The id of this tpa request.
    // It is equals to the key in TPA_REQUEST when stored in it.
    private final long id;

    // The player who sent the request
    // If reversed (tpahere), it is the player who receive tpahere request
    private final ServerPlayer source;

    // The player who receive the request
    // If reversed (tpahere), it is the player who sent tpahere request
    private final ServerPlayer target;

    // The time this reuqest created, in long
    private final long createTime;

    private TPARequest(long id, ServerPlayer source, ServerPlayer target, boolean reverse) {
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

    /**
     * Get an instance of TPARequest
     * @param id The id of that request
     * @param source The player who sent the request
     * @param target The player who receive the request
     * @param reverse If tpahere, the sender and receiver should be reversed.
     * @return If TPA_REQUEST has this request (Identified by id), then return this request.
     *         Otherwise, create and return a new request.
     */
    public static @Nonnull TPARequest getInstance(long id, ServerPlayer source, ServerPlayer target, boolean reverse) {
        TPARequest instance = getInstance(id);
        if (instance == null) {
            instance = new TPARequest(id, source, target, reverse);
            TPA_REQUEST.put(id, instance);
        }
        return instance;
    }

    /**
     * Get an instance of TPARequest
     * @param id The id of that request.
     * @return If TPA_REQUEST has this request (Identified by id), then return this request.
     *         Otherwise, return null.
     */
    public static @Nullable TPARequest getInstance(long id) {
        return TPA_REQUEST.get(id);
    }

    public long getId() {
        return id;
    }

    public ServerPlayer getSource() {
        return source;
    }

    public ServerPlayer getTarget() {
        return target;
    }

    public long getCreateTime() {
        return createTime;
    }

    public static Map<Long, TPARequest> getTpaRequest() {
        return TPA_REQUEST;
    }
}
