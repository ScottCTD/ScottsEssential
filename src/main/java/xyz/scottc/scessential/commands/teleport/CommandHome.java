package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TeleportUtils;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Map;
import java.util.Set;

/**
 * 01/02/2021 22:25
 * /home
 * /sethome
 * /homeother
 * /delhome /removehome
 * /listhomes
 * /listotherhomes
 * /delotherhome
 */
public class CommandHome {

    @ConfigField
    public static boolean isHomeEnable = true;
    @ConfigField
    public static String
            setHomeAlias        = "sethome",
            homeAlias           = "home",
            homeOtherAlias      = "homeother",
            delHomeAlias        = "delhome",
            listHomesAlias      = "listhomes",
            listOtherHomesAlias = "listotherhomes",
            delOtherHomeAlias   = "delotherhome";

    @ConfigField
    public static int homeCooldownSeconds = 3;
    @ConfigField
    public static int homeOtherCooldownSeconds = 3;
    @ConfigField
    public static int maxHomes = 5;



    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(setHomeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .executes(context -> setHome(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> setHome(context.getSource().getPlayerOrException(), "home"))
        );

        dispatcher.register(
                Commands.literal(homeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(SCEPlayerData.getInstance(context.getSource().getPlayerOrException()).getHomes().keySet(), builder))
                                .executes(context -> home(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Name")))
                        )
                        .executes(context -> home(context.getSource().getPlayerOrException(), "home"))
        );
        dispatcher.register(Commands.literal(homeOtherAlias)
                .then(Commands.argument("Other", EntityArgument.player())
                        .then(Commands.argument("HomeName", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(SCEPlayerData.getInstance(EntityArgument.getPlayer(context, "Other")).getHomes().keySet(), builder))
                                .executes(context -> homeOther(context.getSource().getPlayerOrException(),
                                        EntityArgument.getPlayer(context, "Other"),
                                        StringArgumentType.getString(context, "HomeName"))
                                )
                        )
                )
                .requires(source -> source.hasPermission(2))
        );

        dispatcher.register(
                Commands.literal(delHomeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(SCEPlayerData.getInstance(context.getSource().getPlayerOrException()).getHomes().keySet(), builder))
                                .executes(context -> delHome(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> delHome(context.getSource().getPlayerOrException(), "home"))
        );

        dispatcher.register(
                Commands.literal(delOtherHomeAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .then(Commands.argument("Name", StringArgumentType.string())
                                        .requires(source -> source.hasPermission(2))
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(SCEPlayerData.getInstance(EntityArgument.getPlayer(context, "Target")).getHomes().keySet(), builder))
                                        .executes(context -> delOthersHome(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target"), StringArgumentType.getString(context, "Name")))
                                )
                        )
                        .requires(source -> source.hasPermission(2))
        );

        dispatcher.register(
                Commands.literal(listHomesAlias)
                        .executes(context -> listHome(context.getSource().getPlayerOrException()))
        );
        dispatcher.register(
                Commands.literal(listOtherHomesAlias)
                        .then(Commands.argument("other", EntityArgument.player())
                                .requires(commandSource -> commandSource.hasPermission(2))
                                .executes(context -> listOthersHome(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "other"))))
                        .requires(source -> source.hasPermission(2))
        );
    }

    private static int setHome(ServerPlayer player, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        if (data.getHomes().size() >= maxHomes) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "reachMaxHome"), maxHomes), Util.NIL_UUID);
            return 1;
        }
        if (data.getHomePos(name) != null) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeExist"), name), Util.NIL_UUID);
            return 1;
        }
        data.setHome(name, new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "setHomeSuccess"), name), Util.NIL_UUID);
        return 1;
    }

    private static int home(ServerPlayer player, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        if (TeleportUtils.isInCooldown(player, data.getLastHomeTime(), homeCooldownSeconds)) {
            return 1;
        }
        TeleportPos homePos = data.getHomePos(name);
        if (homePos == null) {
            if (data.getHomes().size() == 1) {
                home(player, data.getHomes().keySet().toArray()[0].toString());
                return 1;
            }
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeNotFound"), name), Util.NIL_UUID);
            return 1;
        }
        data.addTeleportHistory(new TeleportPos(player.getLevel().dimension(), player.getOnPos()));
        TeleportUtils.teleport(player, homePos);
        data.setLastHomeTime(System.currentTimeMillis());
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "homeSuccess"), name), Util.NIL_UUID);
        return 1;
    }

    private static int homeOther(ServerPlayer source, ServerPlayer other, String homeName) {
        SCEPlayerData sourceData = SCEPlayerData.getInstance(source);
        if (TeleportUtils.isInCooldown(source, sourceData.getLastHomeOtherTime(), homeOtherCooldownSeconds)) {
            return 1;
        }
        SCEPlayerData otherData = SCEPlayerData.getInstance(other);
        TeleportPos otherHomePos = otherData.getHomePos(homeName);
        if (otherHomePos == null) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeOtherNotFound"), otherData.getName(), homeName), Util.NIL_UUID);
            return 1;
        }
        sourceData.addTeleportHistory(new TeleportPos(source.getLevel().dimension(), source.getOnPos()));
        TeleportUtils.teleport(source, otherHomePos);
        sourceData.setLastHomeOtherTime(System.currentTimeMillis());
        source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "otherHomeSuccess"), otherData.getName(), homeName), Util.NIL_UUID);
        return 1;
    }

    private static int delHome(ServerPlayer player, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        TeleportPos homePos = data.getHomePos(name);
        if (homePos == null) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeNotFound"), name), Util.NIL_UUID);
            return 1;
        }
        data.delHome(name);
        player.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "delHomeSuccess"), name), Util.NIL_UUID);
        return 1;
    }

    private static int delOthersHome(ServerPlayer source, ServerPlayer target, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(target);
        if (!data.getHomes().containsKey(name)) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeOtherNotFound"), data.getName(), name), Util.NIL_UUID);
            return 1;
        }
        data.delHome(name);
        source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "delOthersHomeSuccess"), data.getName(), name), Util.NIL_UUID);
        return 1;
    }

    private static int listHome(ServerPlayer player) {
        Thread thread = new Thread(() -> {
            SCEPlayerData data = SCEPlayerData.getInstance(player);
            Map<String, TeleportPos> homes = data.getHomes();
            if (homes.isEmpty()) {
                player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                        TextUtils.getTranslationKey("message", "noHome")), Util.NIL_UUID);
                return;
            }
            player.sendMessage(new TextComponent(TextUtils.getSeparator("=", 20)), Util.NIL_UUID);
            Set<String> names = homes.keySet();
            int index = 1;
            for (String name : names) {
                TeleportPos teleportPos = homes.get(name);
                TextComponent text = (TextComponent) TextUtils.getGreenTextFromString(false, true, false, index + ": " + name);
                text.setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + name))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(teleportPos.toString()).append("\n")
                                .append(TextUtils.getGreenTextFromI18n(true, false, false,
                                        TextUtils.getTranslationKey("message", "clickToTeleport"))
                                ))));
                player.sendMessage(text, Util.NIL_UUID);
                index++;
            }
            player.sendMessage(new TextComponent(TextUtils.getSeparator("=", 20)), Util.NIL_UUID);
        });
        thread.start();
        return 1;
    }

    private static int listOthersHome(ServerPlayer source, ServerPlayer other) {
        Thread thread = new Thread(() -> {
            SCEPlayerData otherData = SCEPlayerData.getInstance(other);
            Map<String, TeleportPos> otherHomes = otherData.getHomes();
            if (otherHomes.isEmpty()) {
                source.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                        TextUtils.getTranslationKey("message", "otherNoHome"), otherData.getName()), Util.NIL_UUID);
                return;
            }
            source.sendMessage(new TextComponent(TextUtils.getSeparator("=", 20)), Util.NIL_UUID);
            int index = 0;
            for (Map.Entry<String, TeleportPos> e : otherHomes.entrySet()) {
                TeleportPos teleportPos = e.getValue();
                TextComponent text = (TextComponent) TextUtils.getGreenTextFromString(false, true, false, (index + 1) + ": " + e.getKey());
                text.setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + e.getKey()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(teleportPos.toString()).append("\n")
                                .append(TextUtils.getGreenTextFromI18n(true, false, false,
                                        TextUtils.getTranslationKey("message", "clickToTeleport"))
                                ))));
                source.sendMessage(text, Util.NIL_UUID);
                index++;
            }
            source.sendMessage(new TextComponent(TextUtils.getSeparator("=", 20)), Util.NIL_UUID);
        });
        thread.start();
        return 1;
    }

}
