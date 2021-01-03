package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.AirBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import xyz.scottc.scessential.Config;
import xyz.scottc.scessential.core.SEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Optional;
import java.util.Random;

/**
 * 01/01/2021 23:15
 * /rtp
 */
public class CommandRTP {

    private static final String OVERWORLD = "minecraft:overworld";
    private static final String NETHER = "minecraft:the_nether";
    private static final String END = "minecraft:the_end";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("rtp")
                        .executes(context -> rtp(context.getSource().asPlayer()))
        );
    }

    private static int rtp(ServerPlayerEntity player) {
        Thread thread = new Thread(() -> {
            SEPlayerData data = SEPlayerData.getInstance(player);
            if (TeleportUtils.isInCooldown(player, data.getLastRTPTime(), Config.rtpCooldownSeconds)) {
                return;
            }
            ServerWorld world = player.getServerWorld();
            Random random = new Random();
            int x, y, z;
            String worldKey = world.getDimensionKey().getLocation().toString();
            player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "startrtp")), false);
            boolean nether = false;
            for (int i = 0; i < Config.maxRTPAttempts; i++) {
                switch (worldKey) {
                    case OVERWORLD:
                        y = random.nextInt(Config.maxRTPHeightOverworld - Config.minRTPHeightOverworld) + Config.minRTPHeightOverworld;
                        x = random.nextInt(Config.maxRTPRadiusOverworld - Config.minRTPRadiusOverworld) + Config.minRTPRadiusOverworld;
                        z = random.nextInt(Config.maxRTPRadiusOverworld - Config.minRTPRadiusOverworld) + Config.minRTPRadiusOverworld;
                        break;
                    case NETHER:
                        y = random.nextInt(Config.maxRTPHeightNether - Config.minRTPHeightNether) + Config.minRTPHeightNether;
                        x = random.nextInt(Config.maxRTPRadiusNether - Config.minRTPRadiusNether) + Config.minRTPRadiusNether;
                        z = random.nextInt(Config.maxRTPRadiusNether - Config.minRTPRadiusNether) + Config.minRTPRadiusNether;
                        nether = true;
                        break;
                    case END:
                        y = random.nextInt(Config.maxRTPHeightEnd - Config.minRTPHeightEnd) + Config.minRTPHeightEnd;
                        x = random.nextInt(Config.maxRTPRadiusEnd - Config.minRTPRadiusEnd) + Config.minRTPRadiusEnd;
                        z = random.nextInt(Config.maxRTPRadiusEnd - Config.minRTPRadiusEnd) + Config.minRTPRadiusEnd;
                        break;
                    default:
                        y = random.nextInt(Config.maxRTPHeightDefault - Config.minRTPHeightDefault) + Config.minRTPHeightDefault;
                        x = random.nextInt(Config.maxRTPRadiusDefault - Config.minRTPRadiusDefault) + Config.minRTPRadiusDefault;
                        z = random.nextInt(Config.maxRTPRadiusDefault - Config.minRTPRadiusDefault) + Config.minRTPRadiusDefault;
                        break;
                }
                BlockPos playerPos = player.getPosition();
                BlockPos targetPos = new BlockPos(x + playerPos.getX(), y, z + playerPos.getZ());
                if (!nether) {
                    world.getChunk(targetPos.getX() >> 4, targetPos.getZ() >> 4, ChunkStatus.HEIGHTMAPS);
                    targetPos = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, targetPos);
                }
                Optional<RegistryKey<Biome>> biomeRegistryKey = world.func_242406_i(targetPos);
                if (biomeRegistryKey.isPresent() && biomeRegistryKey.get().getLocation().getPath().contains("ocean")) {
                    continue;
                }
                if (world.getBlockState(targetPos.down()).getBlock() instanceof AirBlock) {
                    continue;
                }
                Fluid fluid = world.getFluidState(targetPos).getFluid();
                if (fluid instanceof LavaFluid) {
                    continue;
                }
                world.destroyBlock(targetPos.up(), true);
                world.destroyBlock(targetPos.up().up(), true);
                data.addTeleportHistory(new TeleportPos(world.getDimensionKey(), player.getPosition()));
                player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "rtpattempts"), i + 1), false);
                TeleportUtils.teleport(player, world, targetPos.up());
                data.setLastRTPTime(System.currentTimeMillis());
                player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "rtpsuccess"), x, y, z), true);
                return;
            }
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "rtpfail")), false);
        });
        thread.start();
        return 0;
    }

}
