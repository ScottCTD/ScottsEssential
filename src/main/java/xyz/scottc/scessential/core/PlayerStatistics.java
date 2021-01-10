package xyz.scottc.scessential.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStatistics implements INBTSerializable<CompoundNBT> {

    public static final List<PlayerStatistics> ALL_STATISTICS = new ArrayList<>();

    private String name;
    private UUID uuid;

    private int deathAmount;

    private int totalPlayedSeconds;
    public long lastUpdateTime = 0;

    private PlayerStatistics(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public PlayerStatistics() {}

    public static PlayerStatistics getInstance(PlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        return getInstance(gameProfile.getId(), gameProfile.getName());
    }

    public static PlayerStatistics getInstance(UUID uuid, String name) {
        PlayerStatistics instance = new PlayerStatistics(uuid, name);
        int index = ALL_STATISTICS.indexOf(instance);
        if (index != -1) {
            instance = ALL_STATISTICS.get(index);
        } else {
            ALL_STATISTICS.add(instance);
        }
        return instance;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("uuid", this.uuid);
        nbt.putString("name", this.name);
        nbt.putInt("deathAmount", this.deathAmount);
        nbt.putInt("totalPlayedSeconds", this.totalPlayedSeconds);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.uuid = nbt.getUniqueId("uuid");
        this.name = nbt.getString("name");
        this.deathAmount = nbt.getInt("deathAmount");
        this.totalPlayedSeconds = nbt.getInt("totalPlayedSeconds");
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

    public void addToPlayedSeconds(int seconds) {
        this.totalPlayedSeconds += seconds;
    }

    public int getTotalPlayedSeconds() {
        return this.totalPlayedSeconds;
    }

    public void setTotalPlayedSeconds(int totalPlayedSeconds) {
        this.totalPlayedSeconds = totalPlayedSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerStatistics)) return false;
        return this.uuid.equals(((PlayerStatistics) o).uuid);
    }

    public enum StatisticsType {
        DEATH_AMOUNT,
        TIME_PLAYED
    }

    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class EventHandler {

        private static int counter = 0;

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            PlayerStatistics instance = PlayerStatistics.getInstance(event.getPlayer());
            instance.lastUpdateTime = System.currentTimeMillis();
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                if (counter >= 10 * 20) {
                    counter = 0;
                    long now = System.currentTimeMillis();
                    SCEPlayerData.PLAYER_DATA_LIST.forEach(player -> {
                        PlayerStatistics statistics = player.getStatistics();
                        int diff = (int) ((now - statistics.lastUpdateTime) / 1000);
                        statistics.addToPlayedSeconds(diff);
                        statistics.lastUpdateTime = now;
                    });
                }
                counter++;
            }
        }
    }
}
