package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.utils.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandFly {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("fly")
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> fly(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "Target"), FlyType.PERMANENT))
                                .then(Commands.argument("Minutes", IntegerArgumentType.integer())
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(context -> fly(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "Target"), FlyType.TEMPORARY, IntegerArgumentType.getInteger(context, "Minutes")))
                                )
                        )
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().asPlayer();
                            return fly(player, player, FlyType.PERMANENT);
                        })
        );
    }

    private static int fly(ServerPlayerEntity source, ServerPlayerEntity target, FlyType type, int... minutes) {
        SCEPlayerData data = SCEPlayerData.getInstance(target);
        if (target.isCreative()) {
            source.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "cantsetfly"), data.getPlayerName()), false);
            return 0;
        } else if (data.isFlyable()) {
            data.setFlyable(false);
            source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "ok")), false);
            target.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "cantflynow")), false);
            return 0;
        } else {
            data.setFlyable(true);
        }
        switch (type) {
            case PERMANENT:
                data.setCanFlyUntil(-1L);
                source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "flypermanentlySource"), data.getPlayerName()), false);
                target.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "flypermanentlyTarget")), false);
                break;
            case TEMPORARY:
                long canFlyUntil = System.currentTimeMillis() + minutes[0] * 60 * 1000L;
                data.setCanFlyUntil(canFlyUntil);
                Date date = new Date(canFlyUntil);
                // TODO Configurable format
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss MM/dd/yyyy");
                String formattedDate = simpleDateFormat.format(date);
                target.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "flytempTarget"), formattedDate), false);
                source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("message", "flytempSource"), data.getPlayerName(), formattedDate), false);
                break;
        }
        return 0;
    }

    private enum FlyType {
        TEMPORARY, PERMANENT
    }

}
