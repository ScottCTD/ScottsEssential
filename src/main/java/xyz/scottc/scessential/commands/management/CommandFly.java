package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.utils.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandFly {

    @ConfigField
    public static boolean isFlyEnable = true;
    @ConfigField
    public static String flyAlias = "fly";
    @ConfigField
    public static String datePattern = "hh:mm:ss MM/dd/yyyy";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(flyAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> fly(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target"), FlyType.PERMANENT))
                                .then(Commands.argument("Minutes", IntegerArgumentType.integer())
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> fly(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target"), FlyType.TEMPORARY, IntegerArgumentType.getInteger(context, "Minutes")))
                                )
                        )
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            return fly(player, player, FlyType.PERMANENT);
                        })
        );
    }

    private static int fly(ServerPlayer source, ServerPlayer target, FlyType type, int... minutes) {
        SCEPlayerData data = SCEPlayerData.getInstance(target);
        if (target.isCreative()) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "cantSetFly"), data.getName()), Util.NIL_UUID);
            return 1;
        } else if (data.isFlyable()) {
            data.setFlyable(false);
            source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "ok")), Util.NIL_UUID);
            target.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "cantFlyNow")), Util.NIL_UUID);
            return 1;
        } else {
            data.setFlyable(true);
        }
        switch (type) {
            case PERMANENT:
                data.setCanFlyUntil(-1L);
                if (!source.equals(target)) {
                    source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                            TextUtils.getTranslationKey("message", "flyPermanentlySource"), data.getName()), Util.NIL_UUID);
                }
                target.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "flyPermanentlyTarget")), Util.NIL_UUID);
                break;
            case TEMPORARY:
                long canFlyUntil = System.currentTimeMillis() + minutes[0] * 60 * 1000L;
                data.setCanFlyUntil(canFlyUntil);
                Date date = new Date(canFlyUntil);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                String formattedDate = simpleDateFormat.format(date);
                if (!source.equals(target)) {
                    target.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                            TextUtils.getTranslationKey("message", "flyTempTarget"), formattedDate), Util.NIL_UUID);
                }
                source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "flyTempSource"), data.getName(), formattedDate), Util.NIL_UUID);
                break;
        }
        return 1;
    }

    private enum FlyType {
        TEMPORARY, PERMANENT
    }

}
