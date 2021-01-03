package xyz.scottc.scessential.utils;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import xyz.scottc.scessential.core.TeleportPos;

public class TeleportUtils {

    public static void teleport(ServerPlayerEntity player, ServerWorld world, BlockPos targetPos) {
        player.teleport(world, targetPos.getX(), targetPos.getY(), targetPos.getZ(), player.rotationYaw, player.rotationPitch);
    }

    public static void teleport(ServerPlayerEntity player, TeleportPos pos) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            teleport(player, server.getWorld(pos.getDimension()), pos.getPos());
        }
    }

    /**
     *
     * @param lastTeleportTime Last teleport time
     * @param cooldownSeconds Cooldown of this function in config
     * @return -1 if no cooldown, else return the remain cooldown in seconds.
     */
    public static double getCooldown(long lastTeleportTime, int cooldownSeconds) {
        long now = System.currentTimeMillis();
        long target = lastTeleportTime + cooldownSeconds * 1000L;
        if (now < target) {
            return (target - now) / 1000D;
        } else {
            return -1;
        }
    }

    public static boolean isInCooldown(ServerPlayerEntity player, long lastTeleportTime, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return false;
        double cooldown = TeleportUtils.getCooldown(lastTeleportTime, cooldownSeconds);
        if (cooldown != -1) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "incooldown"), cooldown), false);
            return true;
        } else {
            return false;
        }
    }
}
