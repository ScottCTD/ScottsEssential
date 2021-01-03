package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import xyz.scottc.scessential.Config;
import xyz.scottc.scessential.core.SEPlayerData;
import xyz.scottc.scessential.core.TPARequest;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

/**
 * 01/02/2021 21:26
 * /tpa
 * /tpaaccept
 * /tpadeny
 * /tpahere
 * /tphere
 * /tpallhere
 */
public class CommandTPA {

    private static long id = 0;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("tpa")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> tpa(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "target")))
                        )
        );

        dispatcher.register(
                Commands.literal("tpahere")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> tpaHere(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "target")))
                        )
        );

        dispatcher.register(
                Commands.literal("tphere")
                        .then(Commands.argument("target", EntityArgument.player())
                                .requires(commandSource -> commandSource.hasPermissionLevel(2))
                                .executes(context -> tpHere(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "target")))
                        )
        );

        dispatcher.register(
                Commands.literal("tpallhere")
                        .requires(commandSource -> commandSource.hasPermissionLevel(2))
                        .executes(context -> tpAllHere(context.getSource().asPlayer()))
        );

        dispatcher.register(
                Commands.literal("tpaaccept")
                        .then(Commands.argument("id", LongArgumentType.longArg())
                                .executes(context -> tpaaccept(context.getSource().asPlayer(), LongArgumentType.getLong(context, "id")))
                        )
        );

        dispatcher.register(
                Commands.literal("tpadeny")
                        .then(Commands.argument("id", LongArgumentType.longArg())
                                .executes(context -> tpadeny(context.getSource().asPlayer(), LongArgumentType.getLong(context, "id")))
                        )
        );

    }

    // Many duplicate code with tpahere because it is unnecessary to extract them out of only two methods.
    private static int tpa(ServerPlayerEntity source, ServerPlayerEntity target) {
        SEPlayerData sourceData = SEPlayerData.getInstance(source);
        if (TeleportUtils.isInCooldown(source, sourceData.getLastTPATime(), Config.tpaCooldownSeconds)) {
            return 0;
        }

        TPARequest request = TPARequest.getInstance(nextId(), source, target, false);

        String sourceName = sourceData.getPlayerName();
        String targetName = target.getGameProfile().getName();

        source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "requestsent"), targetName), false);

        IFormattableTextComponent line01 = TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpaline01"), sourceName);

        IFormattableTextComponent line0201 = TextUtils.getYellowTextFromString(true, false, false, sourceName);
        IFormattableTextComponent line0202 = TextUtils.getWhiteTextFromString(false, false, false, " -> ");
        IFormattableTextComponent line0203 = TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "you"));
        IFormattableTextComponent line02 = line0201.append(line0202).append(line0203);

        IFormattableTextComponent line0301 = TextUtils.getGreenTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "accept"));
        IFormattableTextComponent line0301Hover = TextUtils.getGreenTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "acceptHover"));
        line0301 = line0301.setStyle(line0301.getStyle()
                                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + request.getId()))
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.deepCopy().appendString("\n").append(line0301Hover)))
        );
        IFormattableTextComponent line0302 = TextUtils.getRedTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "deny"));
        IFormattableTextComponent line0302Hover = TextUtils.getRedTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "denyHover"));
        line0302 = line0302.setStyle(line0302.getStyle()
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + request.getId()))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.deepCopy().appendString("\n").append(line0302Hover)))
        );
        IFormattableTextComponent line03 = line0301.append(TextUtils.getWhiteTextFromString(false, false, false, " | ")).append(line0302);

        target.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 40)), false);
        target.sendStatusMessage(line01, false);
        target.sendStatusMessage(line02, false);
        target.sendStatusMessage(line03, false);
        target.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 40)), false);

        return 0;
    }

    private static int tpaHere(ServerPlayerEntity source, ServerPlayerEntity target) {
        SEPlayerData sourceData = SEPlayerData.getInstance(source);
        if (TeleportUtils.isInCooldown(source, sourceData.getLastTPATime(), Config.tpaCooldownSeconds)) {
            return 0;
        }

        TPARequest request = TPARequest.getInstance(nextId(), source, target, true);

        String sourceName = sourceData.getPlayerName();
        String targetName = target.getGameProfile().getName();

        source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "requestsent"), targetName), false);

        IFormattableTextComponent line01 = TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpahereline01"), sourceName);

        IFormattableTextComponent line0201 = TextUtils.getGreenTextFromString(false, false, false, "You");
        IFormattableTextComponent line0202 = TextUtils.getWhiteTextFromString(false, false, false, " -> ");
        IFormattableTextComponent line0203 = TextUtils.getYellowTextFromString(true, false, false, sourceName);
        IFormattableTextComponent line02 = line0201.append(line0202).append(line0203);

        IFormattableTextComponent line0301 = TextUtils.getGreenTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "accept"));
        IFormattableTextComponent line0301Hover = TextUtils.getGreenTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "acceptHover"));
        line0301 = line0301.setStyle(line0301.getStyle()
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + request.getId()))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.deepCopy().appendString("\n").append(line0301Hover)))
        );
        IFormattableTextComponent line0302 = TextUtils.getRedTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "deny"));
        IFormattableTextComponent line0302Hover = TextUtils.getRedTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "denyHover"));
        line0302 = line0302.setStyle(line0302.getStyle()
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + request.getId()))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.deepCopy().appendString("\n").append(line0302Hover)))
        );
        IFormattableTextComponent line03 = line0301.append(TextUtils.getWhiteTextFromString(false, false, false, " | ")).append(line0302);

        target.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 40)), false);
        target.sendStatusMessage(line01, false);
        target.sendStatusMessage(line02, false);
        target.sendStatusMessage(line03, false);
        target.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 40)), false);

        return 0;
    }

    private static int tpaaccept(ServerPlayerEntity player, long id) {
        TPARequest request = TPARequest.getInstance(id);
        if (request == null) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "tpanotfound")), false);
            return 0;
        }
        ServerPlayerEntity source = request.getSource();
        SEPlayerData sourceData = SEPlayerData.getInstance(source);
        sourceData.addTeleportHistory(new TeleportPos(source));
        TeleportUtils.teleport(source, new TeleportPos(request.getTarget()));
        sourceData.setLastTPATime(System.currentTimeMillis());
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpasuccessTarget"), sourceData.getPlayerName()), true);
        source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpasuccessSource"), player.getGameProfile().getName()), true);
        TPARequest.getTpaRequest().remove(id);
        return 0;
    }

    private static int tpadeny(ServerPlayerEntity player, long id) {
        TPARequest request = TPARequest.getInstance(id);
        if (request == null) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "tpanotfound")), false);
            return 0;
        }
        TPARequest.getTpaRequest().remove(request.getId());
        ServerPlayerEntity source = request.getSource();
        source.sendStatusMessage(TextUtils.getRedTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "tpadenySource"), player.getGameProfile().getName()), false);
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "ok")), false);
        return 0;
    }

    private static int tpHere(ServerPlayerEntity source, ServerPlayerEntity target) {
        TeleportUtils.teleport(target, new TeleportPos(source));
        return 0;
    }

    private static int tpAllHere(ServerPlayerEntity source) {
        new Thread(() -> source.getServerWorld().getPlayers().stream()
                    .filter(player -> !player.equals(source))
                    .forEach(player -> TeleportUtils.teleport(player, new TeleportPos(source))))
                .start();
        return 0;
    }

    private static long nextId() {
        return ++id;
    }

}
