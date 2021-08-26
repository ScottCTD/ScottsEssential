package xyz.scottc.scessential.core;

import com.google.gson.JsonObject;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TeleportPos implements INBTSerializable<CompoundTag> {

    public static final Map<String, TeleportPos> WARPS = new HashMap<>();

    private ResourceKey<Level> dimension;
    private BlockPos pos;

    public TeleportPos(Player player) {
        this.dimension = player.getCommandSenderWorld().dimension();
        this.pos = player.getOnPos();
    }

    public TeleportPos(ResourceKey<Level> dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public TeleportPos(BlockPos pos) {
        this.dimension = Level.OVERWORLD;
        this.pos = pos;
    }

    public TeleportPos() {}

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setDimension(ResourceKey<Level> dimension) {
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
        jsonObject.addProperty("dimension", this.dimension.getRegistryName().toString());
        jsonObject.addProperty("x", this.pos.getX());
        jsonObject.addProperty("y", this.pos.getY());
        jsonObject.addProperty("z", this.pos.getZ());
        return jsonObject;
    }

    public void fromJSON(JsonObject jsonObject) {
        this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(jsonObject.get("dimension").getAsString()));
        this.pos = new BlockPos(jsonObject.get("x").getAsInt(), jsonObject.get("y").getAsInt(), jsonObject.get("z").getAsInt());
    }

    @Override
    public String toString() {
        String dimension = this.dimension.getRegistryName().getPath();
        String blockpos = "x: " + this.pos.getX() + " y: " + this.pos.getY() + " z: " + this.pos.getZ();
        return "World: " + dimension + "\nPosition: " + blockpos;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("dimension", this.dimension.getRegistryName().toString());
        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("dimension")));
        this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    @Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {

        // Deserialize warps
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
            if (Main.WARPS_FILE.exists()) {
                try {
                    CompoundTag temp = NbtIo.readCompressed(Main.WARPS_FILE);
                    Optional.ofNullable((ListTag) temp.get("warps")).ifPresent(warps -> {
                        for (Tag e : warps) {
                            CompoundTag warp = (CompoundTag) e;
                            TeleportPos pos = new TeleportPos();
                            pos.deserializeNBT((CompoundTag) Objects.requireNonNull(warp.get("pos")));
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
                CompoundTag temp = new CompoundTag();
                ListTag warps = new ListTag();
                for (Map.Entry<String, TeleportPos> warp : TeleportPos.WARPS.entrySet()) {
                    CompoundTag warpNbt = new CompoundTag();
                    warpNbt.putString("name", warp.getKey());
                    warpNbt.put("pos", warp.getValue().serializeNBT());
                    warps.add(warpNbt);
                }
                temp.put("warps", warps);
                NbtIo.writeCompressed(temp, Main.WARPS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
