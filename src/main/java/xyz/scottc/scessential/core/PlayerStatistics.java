package xyz.scottc.scessential.core;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerStatistics implements INBTSerializable<CompoundNBT> {

    private int deathAmount;
    private int totalPlayedSeconds;

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("deathAmount", this.deathAmount);
        nbt.putInt("totalPlayedSeconds", this.totalPlayedSeconds);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.deathAmount = nbt.getInt("deathAmount");
        this.totalPlayedSeconds = nbt.getInt("totalPlayedSeconds");
    }

    public int getDeathAmount() {
        return this.deathAmount;
    }

    public void setDeathAmount(int deathAmount) {
        this.deathAmount = deathAmount;
    }

    public int getTotalPlayedSeconds() {
        return this.totalPlayedSeconds;
    }

    public void setTotalPlayedSeconds(int totalPlayedSeconds) {
        this.totalPlayedSeconds = totalPlayedSeconds;
    }
}
