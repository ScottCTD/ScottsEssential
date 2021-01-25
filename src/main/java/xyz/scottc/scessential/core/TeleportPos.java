package xyz.scottc.scessential.core;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TeleportPos implements INBTSerializable<CompoundNBT> {

    public static final Map<String, TeleportPos> WARPS = new HashMap<>();

    private RegistryKey<World> dimension;
    private BlockPos pos;

    public TeleportPos(PlayerEntity player) {
        this.dimension = player.getEntityWorld().getDimensionKey();
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("dimension", this.dimension.getLocation().toString());
        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("dimension")));
        this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    @Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {

        // Deserialize warps
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
            if (Main.WARPS_FILE.exists()) {
                try {
                    CompoundNBT temp = CompressedStreamTools.readCompressed(Main.WARPS_FILE);
                    Optional.ofNullable((ListNBT) temp.get("warps")).ifPresent(warps -> {
                        for (INBT e : warps) {
                            CompoundNBT warp = (CompoundNBT) e;
                            TeleportPos pos = new TeleportPos();
                            pos.deserializeNBT((CompoundNBT) Objects.requireNonNull(warp.get("pos")));
                            TeleportPos.WARPS.put(warp.getString("name"), pos);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Serialize warps
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onWorldSave(WorldEvent.Save event) {
            try {
                CompoundNBT temp = new CompoundNBT();
                ListNBT warps = new ListNBT();
                for (Map.Entry<String, TeleportPos> warp : TeleportPos.WARPS.entrySet()) {
                    CompoundNBT warpNbt = new CompoundNBT();
                    warpNbt.putString("name", warp.getKey());
                    warpNbt.put("pos", warp.getValue().serializeNBT());
                    warps.add(warpNbt);
                }
                temp.put("warps", warps);
                CompressedStreamTools.writeCompressed(temp, Main.WARPS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
