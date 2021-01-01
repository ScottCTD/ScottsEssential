package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import xyz.scottc.scessential.Config;
import xyz.scottc.scessential.core.SEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Map;
import java.util.Set;

/**
 * 01/01/2021 18:34
 * /home
 * /sethome
 * /homeother
 * /delhome /removehome
 * /listhomes
 * /listotherhomes
 */
public class CommandHome {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> setHome = dispatcher.register(
                Commands.literal("sethome")
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .executes(context -> setHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> setHome(context.getSource().asPlayer(), "home"))
        );
        dispatcher.register(Commands.literal("homeset").redirect(setHome));

        dispatcher.register(
                Commands.literal("home")
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .executes(context -> home(context.getSource().asPlayer(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> home(context.getSource().asPlayer(), "home"))
        );
        dispatcher.register(Commands.literal("homeother")
                .then(Commands.argument("Other", EntityArgument.player())
                        .then(Commands.argument("HomeName", StringArgumentType.string())
                                .executes(context -> homeOther(context.getSource().asPlayer(),
                                        EntityArgument.getPlayer(context, "Other"),
                                        StringArgumentType.getString(context, "HomeName"))
                                )
                        )
                )
        );

        LiteralCommandNode<CommandSource> delHome = dispatcher.register(
                Commands.literal("delhome")
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .executes(context -> delHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> delHome(context.getSource().asPlayer(), "home"))
        );
        dispatcher.register(Commands.literal("removehome").redirect(delHome));

        dispatcher.register(
                Commands.literal("listhomes")
                        .executes(context -> listHome(context.getSource().asPlayer()))
        );
        dispatcher.register(
                Commands.literal("listotherhomes")
                        .then(Commands.argument("other", EntityArgument.player())
                                .executes(context -> listOthersHome(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "other"))))
        );
    }

    private static int setHome(ServerPlayerEntity player, String name) {
        SEPlayerData data = SEPlayerData.getInstance(player.getGameProfile());
        data.setHome(name, new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "sethomesuccess"), name), false);
        return 0;
    }

    private static int home(ServerPlayerEntity player, String name) {
        SEPlayerData data = SEPlayerData.getInstance(player.getGameProfile());
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
        data.addTeleportHistory(new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
        TeleportUtils.teleport(player, homePos);
        data.setLastHomeTime(System.currentTimeMillis());
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "homesuccess"), name), true);
        return 0;
    }

    private static int homeOther(ServerPlayerEntity source, ServerPlayerEntity other, String homeName) {
        SEPlayerData sourceData = SEPlayerData.getInstance(source.getGameProfile());
        if (TeleportUtils.isInCooldown(source, sourceData.getLastHomeOtherTime(), Config.homeOtherCooldownSeconds)) {
            return 0;
        }
        SEPlayerData otherData = SEPlayerData.getInstance(other.getGameProfile());
        TeleportPos otherHomePos = otherData.getHomePos(homeName);
        if (otherHomePos == null) {
            source.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeothernotfound"), otherData.getPlayerName(), homeName), false);
            return 0;
        }
        sourceData.addTeleportHistory(new TeleportPos(source.getServerWorld().getDimensionKey(), source.getPosition()));
        TeleportUtils.teleport(source, otherHomePos);
        sourceData.setLastHomeOtherTime(System.currentTimeMillis());
        source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "otherhomesuccess"), otherData.getPlayerName(), homeName), true);
        return 0;
    }

    private static int delHome(ServerPlayerEntity player, String name) {
        SEPlayerData data = SEPlayerData.getInstance(player.getGameProfile());
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
        SEPlayerData data = SEPlayerData.getInstance(player.getGameProfile());
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

    private static int listOthersHome(ServerPlayerEntity source, ServerPlayerEntity other) {
        SEPlayerData otherData = SEPlayerData.getInstance(other.getGameProfile());
        Map<String, TeleportPos> otherHomes = otherData.getHomes();
        if (otherHomes.isEmpty()) {
            source.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "othernohome"), otherData.getPlayerName()), false);
            return 0;
        }
        source.sendStatusMessage(TextUtils.getYellowTextFromString(false, false, false,
                TextUtils.getSeparator("=", 20)), false);
        int index = 0;
        for (Map.Entry<String, TeleportPos> e : otherHomes.entrySet()) {
            TeleportPos teleportPos = e.getValue();
            IFormattableTextComponent text = TextUtils.getGreenTextFromString(false, true, false, (index + 1) + ": " + e.getKey());
            text.setStyle(text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + e.getKey()))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(teleportPos.toString()).appendString("\n")
                            .append(TextUtils.getGreenTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "clicktoteleport"))
                            ))));
            source.sendStatusMessage(text, false);
            index++;
        }
        source.sendStatusMessage(TextUtils.getYellowTextFromString(false, false, false,
                TextUtils.getSeparator("=", 20)), false);
        return 0;
    }

}
