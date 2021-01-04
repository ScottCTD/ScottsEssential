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
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
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

    @ConfigField
    public static boolean isRTPEnable = true;
    @ConfigField
    public static int rtpCooldownSeconds = 10;
    @ConfigField
    public static int maxRTPAttempts = 10;
    @ConfigField
    public static int minRTPHeightDefault = 40, maxRTPHeightDefault = 120, minRTPRadiusDefault = 1000, maxRTPRadiusDefault = 10000;
    @ConfigField
    public static int minRTPHeightOverworld = 1, maxRTPHeightOverworld = 150, minRTPRadiusOverworld = 1000, maxRTPRadiusOverworld = 10000;
    @ConfigField
    public static int minRTPHeightNether = 30, maxRTPHeightNether = 100, minRTPRadiusNether = 1000, maxRTPRadiusNether = 10000;
    @ConfigField
    public static int minRTPHeightEnd = 40, maxRTPHeightEnd = 140, minRTPRadiusEnd = 1000, maxRTPRadiusEnd = 10000;

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
            SCEPlayerData data = SCEPlayerData.getInstance(player);
            if (TeleportUtils.isInCooldown(player, data.getLastRTPTime(), rtpCooldownSeconds)) {
                return;
            }
            ServerWorld world = player.getServerWorld();
            Random random = new Random();
            int x, y, z;
            String worldKey = world.getDimensionKey().getLocation().toString();
            player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "startrtp")), false);
            boolean nether = false;
            for (int i = 0; i < maxRTPAttempts; i++) {
                switch (worldKey) {
                    case OVERWORLD:
                        y = random.nextInt(maxRTPHeightOverworld - minRTPHeightOverworld) + minRTPHeightOverworld;
                        x = random.nextInt(maxRTPRadiusOverworld - minRTPRadiusOverworld) + minRTPRadiusOverworld;
                        z = random.nextInt(maxRTPRadiusOverworld - minRTPRadiusOverworld) + minRTPRadiusOverworld;
                        break;
                    case NETHER:
                        y = random.nextInt(maxRTPHeightNether - minRTPHeightNether) + minRTPHeightNether;
                        x = random.nextInt(maxRTPRadiusNether - minRTPRadiusNether) + minRTPRadiusNether;
                        z = random.nextInt(maxRTPRadiusNether - minRTPRadiusNether) + minRTPRadiusNether;
                        nether = true;
                        break;
                    case END:
                        y = random.nextInt(maxRTPHeightEnd - minRTPHeightEnd) + minRTPHeightEnd;
                        x = random.nextInt(maxRTPRadiusEnd - minRTPRadiusEnd) + minRTPRadiusEnd;
                        z = random.nextInt(maxRTPRadiusEnd - minRTPRadiusEnd) + minRTPRadiusEnd;
                        break;
                    default:
                        y = random.nextInt(maxRTPHeightDefault - minRTPHeightDefault) + minRTPHeightDefault;
                        x = random.nextInt(maxRTPRadiusDefault - minRTPRadiusDefault) + minRTPRadiusDefault;
                        z = random.nextInt(maxRTPRadiusDefault - minRTPRadiusDefault) + minRTPRadiusDefault;
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
                // Destroy player nearby block
                BlockPos.getAllInBox(targetPos.getX() - 1, targetPos.getY(), targetPos.getZ() - 1, targetPos.getX() + 1, targetPos.getY() + 1, targetPos.getZ() + 1)
                        .forEach(blockpos -> world.destroyBlock(blockpos, true));
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
        return 1;
    }

}
