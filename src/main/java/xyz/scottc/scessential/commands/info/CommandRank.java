package xyz.scottc.scessential.commands.info;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.network.Network;
import xyz.scottc.scessential.network.PacketOpenLeaderboard;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Collections;

public class CommandRank {

    @ConfigField
    public static boolean isRankEnable = true;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("rank")
                        .executes(context -> openGUI(context.getSource().asPlayer()))
        );
    }

    private static int openGUI(ServerPlayerEntity source) {
        Network.sendToPlayerClient(source, new PacketOpenLeaderboard(new TranslationTextComponent(
                TextUtils.getTranslationKey("text", "leaderboard")
        ), Collections.emptyList(), 0));
        return 1;
    }


}
