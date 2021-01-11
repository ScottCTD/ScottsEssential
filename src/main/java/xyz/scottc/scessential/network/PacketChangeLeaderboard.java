package xyz.scottc.scessential.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.scottc.scessential.core.PlayerStatistics;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

public class PacketChangeLeaderboard extends AbstractPacket {

    private final PlayerStatistics.StatisticsType type;

    public PacketChangeLeaderboard(PacketBuffer buffer) {
        super(buffer);
        this.type = buffer.readEnumValue(PlayerStatistics.StatisticsType.class);
    }

    public PacketChangeLeaderboard(PlayerStatistics.StatisticsType type) {
        this.type = type;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEnumValue(this.type);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            switch (this.type) {
                case DEATH_AMOUNT:
                    openDeathRank(context);
                    break;
                case TIME_PLAYED:
                    openTimePlayedRank(context);
                    break;
            }
            context.get().setPacketHandled(true);
        });
    }

    private static void openDeathRank(Supplier<NetworkEvent.Context> context) {
        Map<Integer, String> deathRank = new TreeMap<>((a, b) -> (b - a));
        PlayerStatistics.ALL_STATISTICS.forEach(statistics -> deathRank.put(statistics.getDeathAmount(), statistics.getName()));
        List<ITextComponent> result = new ArrayList<>();
        int index = 1;
        for (Map.Entry<Integer, String> e : deathRank.entrySet()) {
            StringTextComponent head = new StringTextComponent(index + " : ");
            IFormattableTextComponent text = TextUtils.getWhiteTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("text", "deathAmount"), e.getValue(), e.getKey());
            IFormattableTextComponent finalText = head.append(text);
            finalText = color(index, finalText);
            result.add(finalText);
            index++;
        }
        Network.sendToPlayerClient(context.get().getSender(), new PacketOpenLeaderboard(
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "deathstitle")), result, result.size()));

    }

    private static void openTimePlayedRank(Supplier<NetworkEvent.Context> context) {
        Map<Integer, String> rank = new TreeMap<>((a, b) -> (b - a));
        PlayerStatistics.ALL_STATISTICS.forEach(statistics -> rank.put(statistics.getTotalPlayedSeconds(), statistics.getName()));
        List<ITextComponent> result = new ArrayList<>();
        int index = 1;
        for (Map.Entry<Integer, String> e : rank.entrySet()) {
            StringTextComponent head = new StringTextComponent(index + " : ");
            int hour = e.getKey() / 60 / 60;
            int mins = (e.getKey() - hour * 60 * 60) / 60;
            IFormattableTextComponent text = TextUtils.getWhiteTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("text", "timeplayed"), e.getValue(), hour, mins);
            IFormattableTextComponent finalText = head.append(text);
            finalText = color(index, finalText);
            result.add(finalText);
            index++;
        }
        Network.sendToPlayerClient(context.get().getSender(), new PacketOpenLeaderboard(
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "timeplayedtitle")), result, result.size()));
    }

    private static IFormattableTextComponent color(int index, IFormattableTextComponent text) {
        switch (index) {
            case 1:
                text = text.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFFD700)));
                break;
            case 2:
                text = text.setStyle(Style.EMPTY.setColor(Color.fromInt(0xC0C0C0)));
                break;
            case 3:
                text = text.setStyle(Style.EMPTY.setColor(Color.fromInt(0xCD7F32)));
                break;
        }
        return text;
    }
}
