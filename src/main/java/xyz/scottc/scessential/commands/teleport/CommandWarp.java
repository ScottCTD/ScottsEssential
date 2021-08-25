package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Map;

/**
 * 01/02/2021 22:25
 * /setwarp
 * /warp
 * /listwarps
 * /delwarp
 */
public class CommandWarp {

    @ConfigField
    public static boolean isWarpEnable = true;
    @ConfigField
    public static String
            setWarpAlias    = "setwarp",
            warpAlias       = "warp",
            listWarpsAlias  = "listwarps",
            delWarpAlias    = "delwarp";
    @ConfigField
    public static int warpCooldownSeconds = 3;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(setWarpAlias)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .executes(context -> setWarp(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name")))
                        )
                        .requires(source -> source.hasPermission(2))
        );

        dispatcher.register(
                Commands.literal(warpAlias)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(TeleportPos.WARPS.keySet(), builder))
                                .executes(context -> warp(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name")))
                        )
        );

        dispatcher.register(
                Commands.literal(listWarpsAlias)
                        .executes(context -> listWarps(context.getSource().getPlayerOrException()))
        );

        LiteralCommandNode<CommandSourceStack> delWarp = dispatcher.register(
                Commands.literal(delWarpAlias)
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(TeleportPos.WARPS.keySet(), builder))
                                .executes(context -> delWarp(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name")))
                        )
                        .requires(source -> source.hasPermission(2))
        );
        dispatcher.register(Commands.literal("removewarp").requires(commandSource -> commandSource.hasPermission(2)).redirect(delWarp));
    }

    private static int setWarp(ServerPlayer player, String name) {
        TeleportPos.WARPS.put(name, new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "setWarpSuccess"), name), Util.NIL_UUID);
        return 1;
    }

    private static int warp(ServerPlayer player, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        if (TeleportUtils.isInCooldown(player, data.getLastWarpTime(), warpCooldownSeconds)) {
            return 1;
        }
        if (!TeleportPos.WARPS.containsKey(name)) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "warpNotFound"), name), Util.NIL_UUID);
            return 1;
        }
        data.addTeleportHistory(new TeleportPos(player));
        TeleportUtils.teleport(player, TeleportPos.WARPS.get(name));
        data.setLastWarpTime(System.currentTimeMillis());
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "warpSuccess"), name), Util.NIL_UUID);
        return 1;
    }

    private static int listWarps(ServerPlayer player) {
        Thread thread = new Thread(() -> {
            if (TeleportPos.WARPS.isEmpty()) {
                player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                        TextUtils.getTranslationKey("message", "noWarp")), Util.NIL_UUID);
                return;
            }
            player.sendMessage(new TextComponent(TextUtils.getSeparator("=", 20)), ChatType.GAME_INFO,Util.NIL_UUID);
            int index = 1;
            for (Map.Entry<String, TeleportPos> warp : TeleportPos.WARPS.entrySet()) {
                TextComponent text = (TextComponent) TextUtils.getGreenTextFromString(false, true, false, index + ": " + warp.getKey());
                MutableComponent hoverText = new TextComponent(warp.getValue().toString() + "\n")
                        .append(TextUtils.getGreenTextFromI18n(false, false, false,
                                TextUtils.getTranslationKey("message", "clickToTeleport")));
                player.sendMessage(text.setStyle(text.getStyle()
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getKey()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))), Util.NIL_UUID
                );
            }
            player.sendMessage(new TextComponent(TextUtils.getSeparator("=", 20)), Util.NIL_UUID);
        });
        thread.start();
        return 1;
    }

    private static int delWarp(ServerPlayer player, String name) {
        if (!TeleportPos.WARPS.containsKey(name)) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "warpNotFound"), name), Util.NIL_UUID);
            return 1;
        }
        TeleportPos.WARPS.remove(name);
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "delWarpSuccess"), name), Util.NIL_UUID);
        return 1;
    }

}
