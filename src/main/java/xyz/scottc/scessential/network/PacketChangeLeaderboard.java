package xyz.scottc.scessential.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.scottc.scessential.core.PlayerStatistics;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.*;
import java.util.function.Function;
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
                    openRank(context, (stat01, stat02) -> stat02.getDeathAmount() - stat01.getDeathAmount(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("text", "deaths"), statistics.getName(), statistics.getDeathAmount()),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "deathTitle"))
                    );
                    break;
                case TIME_PLAYED:
                    openRank(context, (stat01, stat02) -> stat02.getTotalPlayedTicks() - stat01.getTotalPlayedTicks(),
                            statistics -> {
                                int seconds = statistics.getTotalPlayedTicks() / 20, minutes = seconds / 60, hours = minutes / 60;
                                minutes -= hours * 60;
                                return TextUtils.getWhiteTextFromI18n(false, false, false,
                                        TextUtils.getTranslationKey("text", "timePlayed"), statistics.getName(), hours, minutes);},
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "timePlayedTitle"))
                    );
                    break;
                case MOBS_KILLED:
                    openRank(context, (stat01, stat02) -> stat02.getMobsKilled() - stat01.getMobsKilled(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                            TextUtils.getTranslationKey("text", "mobsKilled"), statistics.getName(), statistics.getMobsKilled()),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "mobsKilledTitle"))
                    );
                    break;
                case DISTANCE_WALKED:
                    openRank(context, (stat01, stat02) -> stat02.getDistanceWalked() - stat01.getDistanceWalked(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("text", "distanceWalked"), statistics.getName(), statistics.getDistanceWalked() / 100),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "distanceWalkedTitle"))
                    );
                    break;
                case BLOCKS_BROKE:
                    openRank(context, (stat01, stat02) -> stat02.getBlocksBroke() - stat01.getBlocksBroke(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("text", "blocksBroke"), statistics.getName(), statistics.getBlocksBroke()),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "blocksBrokeTitle"))
                    );
                    break;
                case FISH_CAUGHT:
                    openRank(context, (stat01, stat02) -> stat02.getFishCaught() - stat01.getFishCaught(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("text", "fishCaught"), statistics.getName(), statistics.getFishCaught()),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "fishCaughtTitle"))
                            );
                    break;
                case DISTANCE_BOATED:
                    openRank(context, (stat01, stat02) -> stat02.getDistanceBoated() - stat01.getDistanceBoated(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("text", "distanceBoated"), statistics.getName(), statistics.getDistanceBoated()),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "distanceBoatedTitle"))
                    );
                    break;
                case DAMAGE_DEALT:
                    openRank(context, (stat01, stat02) -> stat02.getDamageDealt() - stat01.getDamageDealt(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("text", "damageDealt"), statistics.getName(), statistics.getDamageDealt() / 10),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "damageDealtTitle"))
                    );
                    break;
                case DAMAGE_TAKEN:
                    openRank(context, (stat01, stat02) -> stat02.getDamageTaken() - stat01.getDamageTaken(),
                            statistics -> TextUtils.getWhiteTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("text", "damageTaken"), statistics.getName(), statistics.getDamageTaken() / 10),
                            new TranslationTextComponent(TextUtils.getTranslationKey("text", "damageTakenTitle"))
                    );
                    break;
            }
            context.get().setPacketHandled(true);
        });
    }

    private static void openRank(Supplier<NetworkEvent.Context> context, Comparator<PlayerStatistics> comparator,
                                 Function<PlayerStatistics, IFormattableTextComponent> formatter, TranslationTextComponent title) {
        PlayerStatistics.ALL_STATISTICS.sort(comparator);
        List<ITextComponent> result = new ArrayList<>();
        int index = 1;
        for (PlayerStatistics statistics : PlayerStatistics.ALL_STATISTICS) {
            result.add(color(index, new StringTextComponent(index + " : ").append(formatter.apply(statistics))));
            index++;
        }
        Network.sendToPlayerClient(context.get().getSender(), new PacketOpenLeaderboard(title, result, result.size()));
    }

    private static IFormattableTextComponent color(int index, IFormattableTextComponent text) {
        switch (index) {
            case 1:
                text = text.mergeStyle(Style.EMPTY.setColor(Color.fromInt(0xFFD700)));
                break;
            case 2:
                text = text.mergeStyle(Style.EMPTY.setColor(Color.fromInt(0xC0C0C0)));
                break;
            case 3:
                text = text.mergeStyle(Style.EMPTY.setColor(Color.fromInt(0xCD7F32)));
                break;
        }
        return text;
    }


}
