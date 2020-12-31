package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import xyz.scottc.scessential.Config;
import xyz.scottc.scessential.core.SEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Map;
import java.util.Set;

public class CommandHome {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> setHome = dispatcher.register(
                Commands.literal("sethome")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> setHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "name"))))
                        .executes(context -> setHome(context.getSource().asPlayer(), "home"))
        );
        dispatcher.register(Commands.literal("homeset").redirect(setHome));

        dispatcher.register(
                Commands.literal("home")
                .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> home(context.getSource().asPlayer(), StringArgumentType.getString(context, "name"))))
                .executes(context -> home(context.getSource().asPlayer(), "home"))
        );

        LiteralCommandNode<CommandSource> delHome = dispatcher.register(
                Commands.literal("delhome")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> delHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "name"))))
                        .executes(context -> delHome(context.getSource().asPlayer(), "home"))
        );
        dispatcher.register(Commands.literal("removehome").redirect(delHome));

        LiteralCommandNode<CommandSource> listhome = dispatcher.register(
                Commands.literal("listhome")
                        .executes(context -> listHome(context.getSource().asPlayer()))
        );
        dispatcher.register(Commands.literal("listhomes").redirect(listhome));
    }

    private static int setHome(ServerPlayerEntity player, String name) {
        SEPlayerData data = SEPlayerData.getInstance(player.getUniqueID());
        data.setHome(name, new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "sethomesuccess"), name), false);
        return 0;
    }

    private static int home(ServerPlayerEntity player, String name) {
        SEPlayerData data = SEPlayerData.getInstance(player.getUniqueID());
        if (TeleportUtils.isInCooldown(player, data.getLastHomeTime(), Config.homeCooldownSeconds)) {
            return 0;
        }
        TeleportPos homePos = data.getHomePos(name);
        if (homePos == null) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homenotfound"), name), false);
            player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "setnewhome"), name), false);

            IFormattableTextComponent setNewText = TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "options"));
            IFormattableTextComponent accept = TextUtils.getGreenTextFromI18n(true, true, false,
                    TextUtils.getTranslationKey("message", "accept"));
            accept.setStyle(accept.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sethome " + name)));
            IFormattableTextComponent deny = TextUtils.getRedTextFromI18n(true, true, false,
                    TextUtils.getTranslationKey("message", "deny"));
            player.sendStatusMessage(setNewText.appendString("\n").append(accept).append(new StringTextComponent(" | ").setStyle(Style.EMPTY)).append(deny), false);
            return 0;
        }
        MinecraftServer server = player.getServer();
        if (server != null) {
            data.addTeleportHistory(new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
            TeleportUtils.teleport(player, server.getWorld(homePos.getDimension()), homePos.getPos());
            data.setLastHomeTime(System.currentTimeMillis());
            player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "homesuccess"), name), true);
        }
        return 0;
    }

    private static int delHome(ServerPlayerEntity player, String name) {
        SEPlayerData data = SEPlayerData.getInstance(player.getUniqueID());
        TeleportPos homePos = data.getHomePos(name);
        if (homePos == null) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homenotfound"), name), false);
            return 0;
        }
        data.delHome(name);
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "delhomesuccess"), name), false);
        return 0;
    }

    private static int listHome(ServerPlayerEntity player) {
        SEPlayerData data = SEPlayerData.getInstance(player.getUniqueID());
        Map<String, TeleportPos> homes = data.getHomes();
        if (homes.isEmpty()) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "nohome")), false);
            return 0;
        }
        player.sendStatusMessage(TextUtils.getYellowTextFromString(false, false, false,
                TextUtils.getSeparator("=", 20)), false);
        Set<String> names = homes.keySet();
        int index = 0;
        for (String name : names) {
            TeleportPos teleportPos = homes.get(name);
            IFormattableTextComponent text = TextUtils.getGreenTextFromString(false, true, false, (index + 1) + ": " + name);
            text.setStyle(text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + name))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(teleportPos.toString()).appendString("\n")
                            .append(TextUtils.getGreenTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "clicktoteleport"))
                    ))));
            player.sendStatusMessage(text, false);
            index++;
        }
        player.sendStatusMessage(TextUtils.getYellowTextFromString(false, false, false,
                TextUtils.getSeparator("=", 20)), false);
        return 0;
    }

}
