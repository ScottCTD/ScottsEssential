package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import xyz.scottc.scessential.Main;
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
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        if (TeleportUtils.isInCooldown(player, data.getLastBackTime(), backCooldownSeconds)) {
            return 1;
        }
        TeleportPos teleportPos = data.getTeleportHistory();
        if (teleportPos == null) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "noBack")), false);
            return 1;
        }
        // Safe teleport, But if you fall into the void, I will not save you.
        BlockPos.Mutable pos = teleportPos.getPos().toMutable();
        ServerWorld world = Main.SERVER.getWorld(teleportPos.getDimension());
        if (world != null && world.getFluidState(pos).getFluid() instanceof LavaFluid) {
            // Teleport to a cobblestone above the lava
            while (world.getFluidState(pos).getFluid() instanceof LavaFluid) {
                pos = pos.move(0, 1, 0);
            }
            world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
            teleportPos.setPos(pos.up());
        }
        TeleportUtils.teleport(player, teleportPos);
        data.setLastBackTime(System.currentTimeMillis());
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "backSuccess")), true);
        data.moveCurrentBackIndex();
        return 1;
    }

}
