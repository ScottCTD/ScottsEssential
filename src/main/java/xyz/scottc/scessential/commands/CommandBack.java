package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import xyz.scottc.scessential.Config;
import xyz.scottc.scessential.core.SEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

/**
 * 01/01/2021 18:38
 * /back
 */
public class CommandBack {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("back")
                .executes(context -> back(context.getSource().asPlayer()))
        );
    }

    private static int back(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            SEPlayerData data = SEPlayerData.getInstance(player);
            if (TeleportUtils.isInCooldown(player, data.getLastBackTime(), Config.backCooldownSeconds)) {
                return 0;
            }
            TeleportPos teleportPos = data.getTeleportHistory();
            if (teleportPos == null) {
                player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, true, true,
                        TextUtils.getTranslationKey("message", "noback")), true);
                return 0;
            }
            TeleportUtils.teleport(player, server.getWorld(teleportPos.getDimension()), teleportPos.getPos());
            data.setLastBackTime(System.currentTimeMillis());
            player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "backsuccess")), true);
            data.currentBackIndex++;
        }
        return 0;
    }

}
