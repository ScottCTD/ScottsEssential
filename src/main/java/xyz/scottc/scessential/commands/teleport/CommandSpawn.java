package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.level.Level;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

/**
 * 01/01/2021 18:39
 * /spawn
 */
public class CommandSpawn implements Command<CommandSourceStack> {

    @ConfigField
    public static boolean isSpawnEnable = true;
    @ConfigField
    public static String spawnAlias = "spawn";
    @ConfigField
    public static int spawnCooldownSeconds = 3;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(spawnAlias)
                .executes(new CommandSpawn())
        );
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        SCEPlayerData data = SCEPlayerData.getInstance(player);

        if (TeleportUtils.isInCooldown(player, data.getLastSpawnTime(), spawnCooldownSeconds)) {
            return 1;
        }
        MinecraftServer server = player.getServer();
        if (server != null) {
            ServerLevel world = server.getLevel(Level.OVERWORLD);
            if (world != null) {
                BlockPos spawnPoint = world.getSharedSpawnPos();
                data.addTeleportHistory(new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
                TeleportUtils.teleport(player, world, spawnPoint);
                data.setLastSpawnTime(System.currentTimeMillis());
                player.sendMessage(TextUtils.getGreenTextFromI18n(false ,false, false,
                        TextUtils.getTranslationKey("message", "spawnSuccess")), Util.NIL_UUID);
            }
        }
        return 1;
    }

}
