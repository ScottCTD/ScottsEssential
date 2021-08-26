package xyz.scottc.scessential.utils;


import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xyz.scottc.scessential.core.TeleportPos;

public class TeleportUtils {

    public static void teleport(ServerPlayer player, ServerLevel world, BlockPos targetPos) {
        // +0.5 teleport to the center of a block -> avoid suffocating
        player.teleportTo(world, targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5, player.xRotO, player.yRotO);
    }

    public static void teleport(ServerPlayer player, TeleportPos pos) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            teleport(player, server.getLevel(pos.getDimension()), pos.getPos());
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

    public static boolean isInCooldown(ServerPlayer player, long lastTeleportTime, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return false;
        double cooldown = TeleportUtils.getCooldown(lastTeleportTime, cooldownSeconds);
        if (cooldown != -1) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "inCoolDown"), cooldown), Util.NIL_UUID);
            return true;
        } else {
            return false;
        }
    }
}
