package xyz.scottc.scessential.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.api.ISCEPlayerData;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CapabilitySCEPlayerData {

    @CapabilityInject(ISCEPlayerData.class)
    public static Capability<ISCEPlayerData> SCE_PLAYER_DATA_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
                ISCEPlayerData.class,
                new Capability.IStorage<ISCEPlayerData>() {
                    @Override
                    public @NotNull INBT writeNBT(Capability<ISCEPlayerData> capability, ISCEPlayerData instance, Direction side) {
                        CompoundNBT nbt = new CompoundNBT();

                        // Info
                        Optional.ofNullable(instance.getUuid()).ifPresent(id -> nbt.putString("uuid", id.toString()));

                        // Fly
                        nbt.putBoolean("flyable", instance.isFlyable());
                        nbt.putLong("canFlyUntil", instance.getCanFlyUntil());

                        // Homes
                        ListNBT nbtHomes = new ListNBT();
                        for (Map.Entry<String, TeleportPos> home : instance.getHomes().entrySet()) {
                            CompoundNBT nbtHome = new CompoundNBT();
                            nbtHome.putString("name", home.getKey());
                            nbtHome.put("pos", home.getValue().serializeNBT());
                            nbtHomes.add(nbtHome);
                        }
                        nbt.put("homes", nbtHomes);

                        // Backs
                        nbt.putInt("currentBackIndex", instance.getCurrentBackIndex());
                        ListNBT nbtBacks = new ListNBT();
                        for (TeleportPos backPos : instance.getAllTeleportHistory()) {
                            if (backPos == null) break;
                            nbtBacks.add(backPos.serializeNBT());
                        }
                        nbt.put("backHistory", nbtBacks);

                        return nbt;
                    }

                    @Override
                    public void readNBT(Capability<ISCEPlayerData> capability, ISCEPlayerData instance, Direction side, INBT iNbt) {
                        CompoundNBT nbt = (CompoundNBT) iNbt;
                        try {
                            instance.setUuid(UUID.fromString(nbt.getString("uuid")));
                        } catch (IllegalArgumentException ignore) {}

                        instance.setFlyable(nbt.getBoolean("flyable"));
                        instance.setCanFlyUntil(nbt.getLong("canFlyUntil"));

                        Optional.ofNullable((ListNBT) nbt.get("homes")).ifPresent((nbtHomes) -> {
                            for (INBT home : nbtHomes) {
                                CompoundNBT temp = (CompoundNBT) home;
                                TeleportPos pos = new TeleportPos();
                                pos.deserializeNBT(temp.getCompound("pos"));
                                instance.getHomes().put(temp.getString("name"), pos);
                            }
                        });

                        instance.setCurrentBackIndex(nbt.getInt("currentBackIndex"));
                        Optional.ofNullable((ListNBT) nbt.get("backHistory")).ifPresent(backs -> {
                            int i = 0;
                            for (INBT back : backs) {
                                CompoundNBT temp = (CompoundNBT) back;
                                TeleportPos pos = new TeleportPos();
                                pos.deserializeNBT(temp);
                                try {
                                    instance.getAllTeleportHistory()[i] = pos;
                                } catch (IndexOutOfBoundsException e) {
                                    break;
                                }
                                i++;
                            }
                        });
                    }
                },
                SCEPlayerData::getInstance);
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT> {

        private final SCEPlayerData playerData = SCEPlayerData.getInstance();

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap.equals(CapabilitySCEPlayerData.SCE_PLAYER_DATA_CAPABILITY) ?
                    LazyOptional.of(() -> this.playerData).cast() :
                    LazyOptional.empty();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return this.playerData.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.playerData.deserializeNBT(nbt);
        }
    }

}
