package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

/**
 * 01/01/2021 18:38
 * /back
 */
public class CommandBack {

    @ConfigField
    public static boolean isBackEnable = true;
    @ConfigField
    public static String backAlias = "back";
    @ConfigField
    public static int backCooldownSeconds;
    @ConfigField
    public static int maxBacks = 10;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal(backAlias)
                .executes(context -> back(context.getSource().asPlayer()))
        );
    }

    private static int back(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            SCEPlayerData data = SCEPlayerData.getInstance(player);
            if (TeleportUtils.isInCooldown(player, data.getLastBackTime(), backCooldownSeconds)) {
                return 1;
            }
            TeleportPos teleportPos = data.getTeleportHistory();
            if (teleportPos == null) {
                player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                        TextUtils.getTranslationKey("message", "noback")), false);
                return 1;
            }
            TeleportUtils.teleport(player, server.getWorld(teleportPos.getDimension()), teleportPos.getPos());
            data.setLastBackTime(System.currentTimeMillis());
            player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "backsuccess")), true);
            data.moveCurrentBackIndex();
        }
        return 1;
    }

}
