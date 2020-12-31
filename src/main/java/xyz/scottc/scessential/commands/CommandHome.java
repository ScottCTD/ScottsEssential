package xyz.scottc.scessential.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
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

    private static final String SET_HOME = "sethome";
    private static final String HOME = "home";
    private static final String DEL_HOME = "delhome";
    private static final String LIST_HOMES = "listhome";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(SET_HOME)
                .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> setHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "name"))))
                .executes(context -> setHome(context.getSource().asPlayer(), "home"))
        );

        dispatcher.register(
                Commands.literal(HOME)
                .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> home(context.getSource().asPlayer(), StringArgumentType.getString(context, "name"))))
                .executes(context -> home(context.getSource().asPlayer(), "home"))
        );

        dispatcher.register(
                Commands.literal(DEL_HOME)
                .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> delHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "name"))))
                .executes(context -> delHome(context.getSource().asPlayer(), "home"))
        );

        dispatcher.register(
                Commands.literal(LIST_HOMES)
                .executes(context -> listHome(context.getSource().asPlayer()))
        );
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
            return 0;
        }
        MinecraftServer server = player.getServer();
        if (server != null) {
            TeleportUtils.teleport(player, server.getWorld(homePos.getDimension()), homePos.getPos());
            data.setLastHomeTime(System.currentTimeMillis());
            player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                    TextUtils.getTranslationKey("message", "homesuccess"), name), true);
        }
        return 0;
    }

    private static int delHome(ServerPlayerEntity player, String name) {
        SEPlayerData data = SEPlayerData.getInstance(player.getUniqueID());
        if (data.getHomePos(name) == null) {
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
        player.sendStatusMessage(TextUtils.getYellowTextFromString(false, false, false,
                TextUtils.getSeparator("=", 20)), false);
        Set<String> names = homes.keySet();
        int index = 0;
        for (String name : names) {
            TeleportPos teleportPos = homes.get(name);
            IFormattableTextComponent text = (IFormattableTextComponent) TextUtils.getGreenTextFromString(false, false, false, (index + 1) + ": " + name);
            text.setStyle(text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + name))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(teleportPos.toString()))));
            player.sendStatusMessage(text, false);
            index++;
        }
        player.sendStatusMessage(TextUtils.getYellowTextFromString(false, false, false,
                TextUtils.getSeparator("=", 20)), false);
        return 0;
    }

}
