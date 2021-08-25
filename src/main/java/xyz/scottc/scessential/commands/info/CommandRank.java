package xyz.scottc.scessential.commands.info;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.network.Network;
import xyz.scottc.scessential.network.PacketOpenLeaderboard;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Collections;

public class CommandRank {

    @ConfigField
    public static boolean isRankEnable = true;
    @ConfigField
    public static String rankAlias = "rank";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(rankAlias)
                        .executes(context -> openGUI(context.getSource().getPlayerOrException()))
        );
    }

    private static int openGUI(ServerPlayer source) {
        Network.sendToPlayerClient(source, new PacketOpenLeaderboard(new TranslatableComponent(
                TextUtils.getTranslationKey("text", "leaderboard")
        ), Collections.emptyList(), 0));
        return 1;
    }


}
