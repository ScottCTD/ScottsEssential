package xyz.scottc.scessential.commands.info;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.network.Network;
import xyz.scottc.scessential.network.PacketOpenLeaderboard;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandRank {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("rank")
                        .executes(context -> openGUI(context.getSource().asPlayer()))
        );
    }

    private static int openGUI(ServerPlayerEntity source) {
        Map<Integer, String> rank = new TreeMap<>((a, b) -> (b - a));
        SCEPlayerData.PLAYER_DATA_LIST.forEach(data -> rank.put(data.getStatistics().getDeathAmount(), data.getName()));
        List<ITextComponent> texts = getRank(rank);
        PacketOpenLeaderboard test = new PacketOpenLeaderboard(new StringTextComponent("Leaderboard - Deaths"), texts, texts.size());
        Network.sendToPlayerClient(source, test);
        return 1;
    }

    private static List<ITextComponent> getRank(Map<Integer, String> rank) {
        List<ITextComponent> result = new ArrayList<>();
        int index = 1;
        for (Map.Entry<Integer, String> e : rank.entrySet()) {
            StringTextComponent head = new StringTextComponent(index + " : ");
            IFormattableTextComponent text = TextUtils.getWhiteTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "deathAmount"), e.getValue(), e.getKey());
            IFormattableTextComponent finalText = head.append(text);
            switch (index) {
                case 1:
                    finalText = finalText.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFFD700)));
                    break;
                case 2:
                    finalText = finalText.setStyle(Style.EMPTY.setColor(Color.fromInt(0xC0C0C0)));
                    break;
                case 3:
                    finalText = finalText.setStyle(Style.EMPTY.setColor(Color.fromInt(0xCD7F32)));
                    break;
            }
            result.add(finalText);
            index++;
        }
        return result;
    }

}
