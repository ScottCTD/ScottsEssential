package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
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

    @ConfigField
    public static boolean isTPAEnable = true;
    @ConfigField
    public static String
            tpaAlias        = "tpa",
            tpaHereAlias    = "tpahere",
            tpHereAlias     = "tphere",
            tpAllHereAlias  = "tpallhere";
    @ConfigField
    public static int tpaCooldownSeconds = 3;
    @ConfigField
    public static int maxTPARequestTimeoutSeconds = 30;

    private static long id = 0;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(tpaAlias)
                        .then(Commands.argument("Target", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(SCEPlayerData.getAllPlayerNamesFormatted(), builder))
                                .executes(context -> tpa(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Target")))
                        )
        );

        dispatcher.register(
                Commands.literal(tpaHereAlias)
                        .then(Commands.argument("Target", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(SCEPlayerData.getAllPlayerNamesFormatted(), builder))
                                .executes(context -> tpaHere(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Target")))
                        )
        );

        dispatcher.register(
                Commands.literal(tpHereAlias)
                        .then(Commands.argument("Target", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(SCEPlayerData.getAllPlayerNamesFormatted(), builder))
                                .executes(context -> tpHere(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Target")))
                        )
                        .requires(source -> source.hasPermission(2))
        );

        dispatcher.register(
                Commands.literal(tpAllHereAlias)
                        .requires(commandSource -> commandSource.hasPermission(2))
                        .executes(context -> tpAllHere(context.getSource().getPlayerOrException()))
        );

        dispatcher.register(
                Commands.literal("tpaaccept")
                        .then(Commands.argument("id", LongArgumentType.longArg())
                                .executes(context -> tpaaccept(context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "id")))
                        )
        );

        dispatcher.register(
                Commands.literal("tpadeny")
                        .then(Commands.argument("id", LongArgumentType.longArg())
                                .executes(context -> tpadeny(context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "id")))
                        )
        );

    }

    // Many duplicate code with tpahere because it is unnecessary to extract them out of only two methods.
    private static int tpa(ServerPlayer source, String targetName) {
        ServerPlayer target = (ServerPlayer) SCEPlayerData.getPlayer(targetName);
        if (target == null) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false , false,
                    TextUtils.getTranslationKey("message", "playerNotFound"), targetName), Util.NIL_UUID);
            return 1;
        }
        SCEPlayerData sourceData = SCEPlayerData.getInstance(source);
        if (TeleportUtils.isInCooldown(source, sourceData.getLastTPATime(), tpaCooldownSeconds)) {
            return 1;
        }
        if (source.equals(target)) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "canttpaself")), Util.NIL_UUID);
            return 1;
        }

        TPARequest request = TPARequest.getInstance(nextId(), source, target, false);
        String sourceName = sourceData.getName();

        source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "requestSent"), targetName), Util.NIL_UUID);

        TextComponent line01 = (TextComponent) TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpaRequestMessage"), sourceName);

        TextComponent line0201 = (TextComponent) TextUtils.getYellowTextFromString(true, false, false, sourceName);
        TextComponent line0202 = (TextComponent) TextUtils.getWhiteTextFromString(false, false, false, " -> ");
        TextComponent line0203 = (TextComponent) TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "you"));
        MutableComponent line02 = line0201.append(line0202).append(line0203);

        Component line0301 = TextUtils.getGreenTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "accept"));
        TextComponent line0301Hover = (TextComponent) TextUtils.getGreenTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "acceptHover"));
        line0301 = line0301.copy().withStyle(line0301.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0301Hover)))
        );
        Component line0302 = TextUtils.getRedTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "deny"));
        Component line0302Hover = TextUtils.getRedTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "denyHover"));
        line0302 = line0302.copy().withStyle(line0302.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0302Hover)))
        );
        Component line03 = line0301.copy().append(TextUtils.getWhiteTextFromString(false, false, false, " | ")).append(line0302);

        target.sendMessage(new TextComponent(TextUtils.getSeparator("=", 40)), Util.NIL_UUID);
        target.sendMessage(line01, Util.NIL_UUID);
        target.sendMessage(line02, Util.NIL_UUID);
        target.sendMessage(line03, Util.NIL_UUID);
        target.sendMessage(new TextComponent(TextUtils.getSeparator("=", 40)), Util.NIL_UUID);

        return 1;
    }

    private static int tpaHere(ServerPlayer source, String targetName) {
        ServerPlayer target = (ServerPlayer) SCEPlayerData.getPlayer(targetName);
        if (target == null) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false , false,
                    TextUtils.getTranslationKey("message", "playerNotFound"), targetName), Util.NIL_UUID);
            return 1;
        }
        if (source.equals(target)) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "cantTPASelf")), Util.NIL_UUID);
            return 1;
        }
        SCEPlayerData sourceData = SCEPlayerData.getInstance(source);
        if (TeleportUtils.isInCooldown(source, sourceData.getLastTPATime(), tpaCooldownSeconds)) {
            return 1;
        }

        TPARequest request = TPARequest.getInstance(nextId(), source, target, true);

        String sourceName = sourceData.getName();

        source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "requestSent"), targetName), Util.NIL_UUID);

        Component line01 = TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpaHereRequestMessage"), sourceName);

        Component line0201 = TextUtils.getGreenTextFromString(false, false, false, "You");
        Component line0202 = TextUtils.getWhiteTextFromString(false, false, false, " -> ");
        Component line0203 = TextUtils.getYellowTextFromString(true, false, false, sourceName);
        Component line02 = line0201.copy().append(line0202).append(line0203);

        Component line0301 = TextUtils.getGreenTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "accept"));
        Component line0301Hover = TextUtils.getGreenTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "acceptHover"));
        line0301 = line0301.plainCopy().withStyle(line0301.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0301Hover)))
        );
        Component line0302 = TextUtils.getRedTextFromI18n(true, true, false,
                TextUtils.getTranslationKey("message", "deny"));
        Component line0302Hover = TextUtils.getRedTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "denyHover"));
        line0302 = line0302.plainCopy().withStyle(line0302.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + request.getId()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, line02.copy().append("\n").append(line0302Hover)))
        );
        Component line03 = line0301.copy().append(TextUtils.getWhiteTextFromString(false, false, false, " | ")).append(line0302);

        target.sendMessage(new TextComponent(TextUtils.getSeparator("=", 40)), Util.NIL_UUID);
        target.sendMessage(line01, Util.NIL_UUID);
        target.sendMessage(line02, Util.NIL_UUID);
        target.sendMessage(line03, Util.NIL_UUID);
        target.sendMessage(new TextComponent(TextUtils.getSeparator("=", 40)), Util.NIL_UUID);

        return 1;
    }

    private static int tpaaccept(ServerPlayer player, long id) {
        TPARequest request = TPARequest.getInstance(id);
        if (request == null) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "requestNotFound")), Util.NIL_UUID);
            return 1;
        }
        ServerPlayer source = request.getSource();
        SCEPlayerData sourceData = SCEPlayerData.getInstance(source);
        sourceData.addTeleportHistory(new TeleportPos(source));
        TeleportUtils.teleport(source, new TeleportPos(request.getTarget()));
        sourceData.setLastTPATime(System.currentTimeMillis());
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpaSuccessTarget"), sourceData.getName()), ChatType.GAME_INFO,Util.NIL_UUID);
        source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "tpaSuccessSource"), player.getGameProfile().getName()), ChatType.GAME_INFO,Util.NIL_UUID);
        TPARequest.getTpaRequest().remove(id);
        return 1;
    }

    private static int tpadeny(ServerPlayer player, long id) {
        TPARequest request = TPARequest.getInstance(id);
        if (request == null) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "requestNotFound")), Util.NIL_UUID);
            return 1;
        }
        TPARequest.getTpaRequest().remove(request.getId());
        ServerPlayer source = request.getSource();
        source.sendMessage(TextUtils.getRedTextFromI18n(true, false, false,
                TextUtils.getTranslationKey("message", "tpaDenySource"), player.getGameProfile().getName()), Util.NIL_UUID);
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "ok")), Util.NIL_UUID);
        return 1;
    }

    private static int tpHere(ServerPlayer source, String targetName) {
        ServerPlayer target = (ServerPlayer) SCEPlayerData.getPlayer(targetName);
        if (target == null) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false , false,
                    TextUtils.getTranslationKey("message", "playerNotFound"), targetName), Util.NIL_UUID);
            return 1;
        }
        TeleportUtils.teleport(target, new TeleportPos(source));
        return 1;
    }

    private static int tpAllHere(ServerPlayer source) {
        new Thread(() -> Main.SERVER.getPlayerList().getPlayers().stream()
                .filter(player -> !player.equals(source))
                .forEach(player -> TeleportUtils.teleport(player, new TeleportPos(source))))
                .start();
        return 1;
    }

    private static long nextId() {
        return ++id;
    }

}
