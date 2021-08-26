package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.utils.TextUtils;

public class CommandSCE {

    public static LiteralCommandNode<CommandSourceStack> scessential;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        scessential = dispatcher.register(
                Commands.literal(Main.MOD_ID)
                        .executes(context -> scessential(context.getSource().getPlayerOrException()))
        );
    }

    private static int scessential(ServerPlayer player) {
        player.sendMessage(TextUtils.getWhiteTextFromString(true, false, false,
                Main.MOD_ID + "-" + Main.MOD_VERSION), Util.NIL_UUID);
        return 1;
    }

}
