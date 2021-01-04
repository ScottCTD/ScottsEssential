package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.utils.TextUtils;

public class CommandSCE {

    public static LiteralCommandNode<CommandSource> scessential;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        scessential = dispatcher.register(
                Commands.literal(Main.MODID)
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> scessential(context.getSource().asPlayer()))
        );
    }

    private static int scessential(ServerPlayerEntity player) {
        player.sendStatusMessage(TextUtils.getWhiteTextFromString(true, false, false,
                Main.MODID + "-" + Main.MOD_VERSION), false);
        return 1;
    }

}
