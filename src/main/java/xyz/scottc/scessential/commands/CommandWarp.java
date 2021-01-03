package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import xyz.scottc.scessential.Config;
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

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("setwarp")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermissionLevel(2))
                                .executes(context -> setWarp(context.getSource().asPlayer(), StringArgumentType.getString(context, "name")))
                        )
        );

        dispatcher.register(
                Commands.literal("warp")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests((context, builder) -> ISuggestionProvider.suggest(TeleportPos.WARPS.keySet(), builder))
                                .executes(context -> warp(context.getSource().asPlayer(), StringArgumentType.getString(context, "name")))
                        )
        );

        dispatcher.register(
                Commands.literal("listwarps")
                        .executes(context -> listWarps(context.getSource().asPlayer()))
        );

        LiteralCommandNode<CommandSource> delWarp = dispatcher.register(
                Commands.literal("delwarp")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermissionLevel(2))
                                .suggests((context, builder) -> ISuggestionProvider.suggest(TeleportPos.WARPS.keySet(), builder))
                                .executes(context -> delWarp(context.getSource().asPlayer(), StringArgumentType.getString(context, "name")))
                        )
        );
        dispatcher.register(Commands.literal("removewarp").requires(commandSource -> commandSource.hasPermissionLevel(2)).redirect(delWarp));
    }

    private static int setWarp(ServerPlayerEntity player, String name) {
        TeleportPos.WARPS.put(name, new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "setwarpsuccess"), name), false);
        return 0;
    }

    private static int warp(ServerPlayerEntity player, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        if (TeleportUtils.isInCooldown(player, data.getLastWarpTime(), Config.warpCooldownSeconds)) {
            return 0;
        }
        if (!TeleportPos.WARPS.containsKey(name)) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "warpnotfound"), name), false);
            return 0;
        }
        data.addTeleportHistory(new TeleportPos(player));
        TeleportUtils.teleport(player, TeleportPos.WARPS.get(name));
        data.setLastWarpTime(System.currentTimeMillis());
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "warpsuccess"), name), true);
        return 0;
    }

    private static int listWarps(ServerPlayerEntity player) {
        Thread thread = new Thread(() -> {
            if (TeleportPos.WARPS.isEmpty()) {
                player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                        TextUtils.getTranslationKey("message", "nowarp")), false);
            }
            player.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 20)), false);
            int index = 1;
            for (Map.Entry<String, TeleportPos> warp : TeleportPos.WARPS.entrySet()) {
                IFormattableTextComponent text = TextUtils.getGreenTextFromString(false, true, false, index + ": " + warp.getKey());
                IFormattableTextComponent hoverText = new StringTextComponent(warp.getValue().toString() + "\n")
                        .append(TextUtils.getGreenTextFromI18n(false, false, false,
                                TextUtils.getTranslationKey("message", "clicktoteleport")));
                player.sendStatusMessage(text.setStyle(text.getStyle()
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getKey()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))), false
                );
            }
            player.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 20)), false);
        });
        thread.start();
        return 0;
    }

    private static int delWarp(ServerPlayerEntity player, String name) {
        if (!TeleportPos.WARPS.containsKey(name)) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "warpnotfound"), name), false);
            return 0;
        }
        TeleportPos.WARPS.remove(name);
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "delwarpsuccess"), name), false);
        return 0;
    }

}
