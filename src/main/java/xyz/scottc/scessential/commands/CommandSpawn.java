package xyz.scottc.scessential.commands;

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
import xyz.scottc.scessential.Config;
import xyz.scottc.scessential.core.SEPlayerData;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

public class CommandSpawn implements Command<CommandSource> {

    private static final CommandSpawn INSTANCE = new CommandSpawn();
    public static final String SPAWN = "spawn";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(SPAWN)
                        .executes(INSTANCE)
        );
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        SEPlayerData data = SEPlayerData.getInstance(player.getUniqueID());

        if (TeleportUtils.isInCooldown(player, data.getLastSpawnTime(), Config.spawnCooldownSeconds)) {
            return 0;
        }
        MinecraftServer server = player.getServer();
        if (server != null) {
            ServerWorld world = server.getWorld(World.OVERWORLD);
            if (world != null) {
                BlockPos spawnPoint = world.getSpawnPoint();
                TeleportUtils.teleport(player, world, spawnPoint);
                data.setLastSpawnTime(System.currentTimeMillis());
                player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false ,false, false,
                        TextUtils.getTranslationKey("message", "spawnsuccess")), true);
            }
        }

        return 0;
    }

}
