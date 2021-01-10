package xyz.scottc.scessential.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.core.PlayerStatistics;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The capability interface for storing mod player data
 */
public interface ISCEPlayerData extends INBTSerializable<CompoundNBT> {

    @NotNull List<SCEPlayerData> getAllPlayerData();

    /**
     * Get the PlayerEntity of this player.
     * @return The PlayerEntity of this player.
     *         Null if triggered before player loaded (PlayerEvent.LoadFromFile).
     */
    @Nullable PlayerEntity getPlayer();

    void setPlayer(PlayerEntity player);

    /**
     * Get the UUID of this player.
     * @return The UUID of this player.
     *         Null if triggered before player loaded (PlayerEvent.LoadFromFile).
     */
    @Nullable UUID getUuid();

    void setUuid(UUID uuid);

    PlayerStatistics getStatistics();

    /**
     * If the trashcan of this player had not been initialized, this method will return null.
     * The initialization of trashcan happened when this player firstly use command /trashcan
     * @return The Trashcan of this player.
     */
    @Nullable CommandTrashcan.Trashcan getTrashcan();

    /**
     * Set the trashcan of this player.
     * @param trashcan CommandTrashcan.Trashcan
     */
    void setTrashcan(CommandTrashcan.Trashcan trashcan);

    /**
     * @return If the player is flyable.
     */
    boolean isFlyable();

    /**
     * Set the if this player could fly.
     * @param flyable boolean Flyable
     */
    void setFlyable(boolean flyable);

    /**
     * @return The dead line in long this player can fly.
     */
    long getCanFlyUntil();

    /**
     * Set the dead line in long this player can fly.
     * @param time time in long
     */
    void setCanFlyUntil(long time);

    /**
     * Get all homes of this player.
     * @return A Map<String, TeleportPos> where String refers to the name of that home, and TeleportPos refers to the
     *         position.
     */
    Map<String, TeleportPos> getHomes();

    /**
     * Add a TeleportPos which servers as the teleport history.
     * @param teleportPos A TeleportPos.
     */
    void addTeleportHistory(TeleportPos teleportPos);

    TeleportPos[] getAllTeleportHistory();

    /**
     * Get the next TeleportPos for /back usage.
     * @return The next TeleportPos for /back usage.
     */
    @Nullable TeleportPos getTeleportHistory();

    int getCurrentBackIndex();

    void setCurrentBackIndex(int index);

    /**
     * ++current back index.
     * CurrentBackIndex: 0 refers to the position before the most recent teleport.
     *                   And etc...
     */
    void moveCurrentBackIndex();
}
