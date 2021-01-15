package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

/**
 * 01/01/2021 18:39
 * /spawn
 */
public class CommandSpawn implements Command<CommandSource> {

    @ConfigField
    public static boolean isSpawnEnable = true;
    @ConfigField
    public static String spawnAlias = "spawn";
    @ConfigField
    public static int spawnCooldownSeconds = 3;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal(spawnAlias)
                .executes(new CommandSpawn())
        );
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        SCEPlayerData data = SCEPlayerData.getInstance(player);

        if (TeleportUtils.isInCooldown(player, data.getLastSpawnTime(), spawnCooldownSeconds)) {
            return 1;
        }
        MinecraftServer server = player.getServer();
        if (server != null) {
            ServerWorld world = server.getWorld(World.OVERWORLD);
            if (world != null) {
                BlockPos spawnPoint = world.getSpawnPoint();
                data.addTeleportHistory(new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
                TeleportUtils.teleport(player, world, spawnPoint);
                data.setLastSpawnTime(System.currentTimeMillis());
                player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false ,false, false,
                        TextUtils.getTranslationKey("message", "spawnSuccess")), true);
            }
        }
        return 1;
    }

}
