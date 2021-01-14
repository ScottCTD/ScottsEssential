package xyz.scottc.scessential.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 01/11/2021 23:28
 */
public class PlayerStatistics implements INBTSerializable<CompoundNBT> {

    public static final List<PlayerStatistics> ALL_STATISTICS = new ArrayList<>();

    private ServerPlayerEntity player;
    private String name;
    private UUID uuid;

    private int deathAmount = 0;
    private int totalPlayedTicks;
    private int mobsKilled;
    private int distanceWalked;
    private int blocksBroke;

    private PlayerStatistics(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    private PlayerStatistics() {}

    public static @NotNull PlayerStatistics getInstance(@NotNull ServerPlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        PlayerStatistics instance = new PlayerStatistics(gameProfile.getId(), gameProfile.getName());
        int index = ALL_STATISTICS.indexOf(instance);
        if (index != -1) {
            instance = ALL_STATISTICS.get(index);
        } else {
            ALL_STATISTICS.add(instance);
        }
        instance.player = player;
        instance.update();
        return instance;
    }

    public void update() {
        ServerStatisticsManager stats = this.player.getStats();
        this.setDeathAmount(stats.getValue(Stats.CUSTOM.get(Stats.DEATHS)));
        this.setTotalPlayedTicks(stats.getValue(Stats.CUSTOM.get(Stats.PLAY_ONE_MINUTE)));
        this.setMobsKilled(stats.getValue(Stats.CUSTOM.get(Stats.MOB_KILLS)));
        this.setDistanceWalked(stats.getValue(Stats.CUSTOM.get(Stats.WALK_ONE_CM)));
        AtomicInteger temp = new AtomicInteger();
        ForgeRegistries.BLOCKS.getValues().forEach(block -> temp.getAndAdd(stats.getValue(Stats.BLOCK_MINED.get(block))));
        this.setBlocksBroke(temp.get());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("uuid", this.uuid);
        nbt.putString("name", this.name);
        nbt.putInt("deathAmount", this.deathAmount);
        nbt.putInt("totalPlayedTicks", this.totalPlayedTicks);
        nbt.putInt("mobsKilled", this.mobsKilled);
        nbt.putInt("distanceWalked", this.distanceWalked);
        nbt.putInt("blocksBroke", this.blocksBroke);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.uuid = nbt.getUniqueId("uuid");
        this.name = nbt.getString("name");
        this.deathAmount = nbt.getInt("deathAmount");
        this.totalPlayedTicks = nbt.getInt("totalPlayedTicks");
        this.mobsKilled = nbt.getInt("mobsKilled");
        this.distanceWalked = nbt.getInt("distanceWalked");
        this.blocksBroke = nbt.getInt("blocksBroke");
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public int getDeathAmount() {
        return this.deathAmount;
    }

    public void setDeathAmount(int deathAmount) {
        this.deathAmount = deathAmount;
    }

    public int getTotalPlayedTicks() {
        return this.totalPlayedTicks;
    }

    public void setTotalPlayedTicks(int totalPlayedTicks) {
        this.totalPlayedTicks = totalPlayedTicks;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled = mobsKilled;
    }

    public int getDistanceWalked() {
        return distanceWalked;
    }

    public void setDistanceWalked(int distanceWalked) {
        this.distanceWalked = distanceWalked;
    }

    public int getBlocksBroke() {
        return blocksBroke;
    }

    public void setBlocksBroke(int blocksBroke) {
        this.blocksBroke = blocksBroke;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerStatistics)) return false;
        return this.uuid.equals(((PlayerStatistics) o).uuid);
    }

    public enum StatisticsType {
        DEATH_AMOUNT,
        TIME_PLAYED,
        MOBS_KILLED,
        DISTANCE_WALKED,
        BLOCKS_BROKE
    }

    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class EventHandler {

        private static int counter = 0;

        // Deserialize statistics data
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
            // Deserialize statistics
            if (Main.STATISTICS_FILE.exists()) {
                try {
                    CompoundNBT nbt = CompressedStreamTools.readCompressed(Main.STATISTICS_FILE);
                    Optional.ofNullable((ListNBT) nbt.get("statistics")).ifPresent(listnbt -> listnbt.forEach(statistic -> {
                        PlayerStatistics playerStatistics = new PlayerStatistics();
                        playerStatistics.deserializeNBT((CompoundNBT) statistic);
                        PlayerStatistics.ALL_STATISTICS.add(playerStatistics);
                    }));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Main.LOGGER.info("Successfully deserialize player statistics data!");
            }
        }

        // Serialize statistics data
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onWorldSaved(WorldEvent.Save event) {
            // Serialize statistics
            try {
                CompoundNBT temp = new CompoundNBT();
                ListNBT listNBT = new ListNBT();
                PlayerStatistics.ALL_STATISTICS.forEach(statistic -> {
                    CompoundNBT nbt = statistic.serializeNBT();
                    listNBT.add(nbt);
                });
                temp.put("statistics", listNBT);
                CompressedStreamTools.writeCompressed(temp, Main.STATISTICS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                if (counter >= 30 * 20) {
                    counter = 0;
                    // Only update online players
                    SCEPlayerData.PLAYER_DATA_LIST.forEach(data -> data.getStatistics().update());
                }
                counter++;
            }
        }
    }
}
