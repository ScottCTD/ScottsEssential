package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.LavaFluid;
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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(backAlias)
                .executes(context -> back(context.getSource().getPlayerOrException()))
        );
    }

    private static int back(ServerPlayer player) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        if (TeleportUtils.isInCooldown(player, data.getLastBackTime(), backCooldownSeconds)) {
            return 1;
        }
        TeleportPos teleportPos = data.getTeleportHistory();
        if (teleportPos == null) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "noBack")), Util.NIL_UUID);
            return 1;
        }
        // Safe teleport But if you fall into the void, I will not save you.
        BlockPos.MutableBlockPos pos = teleportPos.getPos().mutable();
        ServerLevel world = Main.SERVER.getLevel(teleportPos.getDimension());
        if (world != null) {
            // Teleport to a cobblestone above the lava
            while (world.getFluidState(pos).getType() instanceof LavaFluid) {
                pos = pos.move(0, 1, 0);
            }
            world.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
            teleportPos.setPos(pos.above());
        }
        TeleportUtils.teleport(player, teleportPos);
        data.setLastBackTime(System.currentTimeMillis());
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "backSuccess")), Util.NIL_UUID);
        data.moveCurrentBackIndex();
        return 1;
    }

}
