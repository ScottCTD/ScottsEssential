package xyz.scottc.scessential.core;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class TeleportPos {

    public static final Map<String, TeleportPos> WARPS = new HashMap<>();

    private RegistryKey<World> dimension;
    private BlockPos pos;

    public TeleportPos(ServerPlayerEntity player) {
        this.dimension = player.getServerWorld().getDimensionKey();
        this.pos = player.getPosition();
    }

    public TeleportPos(RegistryKey<World> dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public TeleportPos(BlockPos pos) {
        this.dimension = World.OVERWORLD;
        this.pos = pos;
    }

    public TeleportPos() {}

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public void setDimension(RegistryKey<World> dimension) {
        this.dimension = dimension;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public JsonObject toJSON() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("dimension", this.dimension.getLocation().toString());
        jsonObject.addProperty("x", this.pos.getX());
        jsonObject.addProperty("y", this.pos.getY());
        jsonObject.addProperty("z", this.pos.getZ());
        return jsonObject;
    }

    public void fromJSON(JsonObject jsonObject) {
        this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(jsonObject.get("dimension").getAsString()));
        this.pos = new BlockPos(jsonObject.get("x").getAsInt(), jsonObject.get("y").getAsInt(), jsonObject.get("z").getAsInt());
    }

    @Override
    public String toString() {
        String dimension = this.dimension.getLocation().getPath();
        String blockpos = "x: " + this.pos.getX() + " y: " + this.pos.getY() + " z: " + this.pos.getZ();
        return "World: " + dimension + "\nPosition: " + blockpos;
    }
}
