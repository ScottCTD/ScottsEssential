package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.LavaFluid;
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
    public static String rtpAlias = "rtp";
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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(rtpAlias)
                        .executes(context -> rtp(context.getSource().getPlayerOrException()))
        );
    }

    private static int rtp(ServerPlayer player) {
        Thread thread = new Thread(() -> {
            SCEPlayerData data = SCEPlayerData.getInstance(player);
            if (TeleportUtils.isInCooldown(player, data.getLastRTPTime(), rtpCooldownSeconds)) {
                return;
            }
            ServerLevel world = player.getLevel();
            Random random = new Random();
            int x, y, z;
            String worldKey = world.dimension().getRegistryName().toString();
            player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "startRTP")), Util.NIL_UUID);
            boolean nether = false;
            for (int i = 0; i < maxRTPAttempts; i++) {
                switch (worldKey) {
                    case OVERWORLD -> {
                        y = random.nextInt(maxRTPHeightOverworld - minRTPHeightOverworld) + minRTPHeightOverworld;
                        x = random.nextInt(maxRTPRadiusOverworld - minRTPRadiusOverworld) + minRTPRadiusOverworld;
                        z = random.nextInt(maxRTPRadiusOverworld - minRTPRadiusOverworld) + minRTPRadiusOverworld;
                    }
                    case NETHER -> {
                        y = random.nextInt(maxRTPHeightNether - minRTPHeightNether) + minRTPHeightNether;
                        x = random.nextInt(maxRTPRadiusNether - minRTPRadiusNether) + minRTPRadiusNether;
                        z = random.nextInt(maxRTPRadiusNether - minRTPRadiusNether) + minRTPRadiusNether;
                        nether = true;
                    }
                    case END -> {
                        y = random.nextInt(maxRTPHeightEnd - minRTPHeightEnd) + minRTPHeightEnd;
                        x = random.nextInt(maxRTPRadiusEnd - minRTPRadiusEnd) + minRTPRadiusEnd;
                        z = random.nextInt(maxRTPRadiusEnd - minRTPRadiusEnd) + minRTPRadiusEnd;
                    }
                    default -> {
                        y = random.nextInt(maxRTPHeightDefault - minRTPHeightDefault) + minRTPHeightDefault;
                        x = random.nextInt(maxRTPRadiusDefault - minRTPRadiusDefault) + minRTPRadiusDefault;
                        z = random.nextInt(maxRTPRadiusDefault - minRTPRadiusDefault) + minRTPRadiusDefault;
                    }
                }
                BlockPos playerPos = player.getOnPos();
                BlockPos targetPos = new BlockPos(x + playerPos.getX(), y, z + playerPos.getZ());
                if (!nether) {
                    world.getChunk(targetPos.getX() >> 4, targetPos.getZ() >> 4, ChunkStatus.HEIGHTMAPS);
                    targetPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetPos);
                }
                Optional<ResourceKey<Biome>> biomeRegistryKey = world.getBiomeName(targetPos);
                if (biomeRegistryKey.isPresent() && biomeRegistryKey.get().getRegistryName().getPath().contains("ocean")) {
                    continue;
                }
                if (world.getBlockState(targetPos.above()).getBlock() instanceof AirBlock) {
                    continue;
                }
                Fluid fluid = world.getFluidState(targetPos).getType();
                if (fluid instanceof LavaFluid) {
                    continue;
                }
                // Destroy player nearby block
                BlockPos.betweenClosed(targetPos.getX() - 1, targetPos.getY(), targetPos.getZ() - 1, targetPos.getX() + 1, targetPos.getY() + 1, targetPos.getZ() + 1)
                        .forEach(blockPos -> world.destroyBlock(blockPos, true));
                data.addTeleportHistory(new TeleportPos(world.dimension(), player.getOnPos()));
                player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "rtpAttempts"), i + 1), Util.NIL_UUID);
                TeleportUtils.teleport(player, world, targetPos.above());
                data.setLastRTPTime(System.currentTimeMillis());
                player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "rtpSuccess"), x, y, z), Util.NIL_UUID);
                return;
            }
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "rtpFail")), Util.NIL_UUID);
        });
        thread.start();
        return 1;
    }

}
