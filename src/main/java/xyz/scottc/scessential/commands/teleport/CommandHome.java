package xyz.scottc.scessential.commands.teleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
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

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(setHomeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .executes(context -> setHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> setHome(context.getSource().asPlayer(), "home"))
        );

        dispatcher.register(
                Commands.literal(homeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .suggests((context, builder) -> ISuggestionProvider.suggest(SCEPlayerData.getInstance(context.getSource().asPlayer()).getHomes().keySet(), builder))
                                .executes(context -> home(context.getSource().asPlayer(), StringArgumentType.getString(context, "Name")))
                        )
                        .executes(context -> home(context.getSource().asPlayer(), "home"))
        );
        dispatcher.register(Commands.literal(homeOtherAlias)
                .then(Commands.argument("Other", EntityArgument.player())
                        .then(Commands.argument("HomeName", StringArgumentType.string())
                                .requires(commandSource -> commandSource.hasPermissionLevel(2))
                                .suggests((context, builder) -> ISuggestionProvider.suggest(SCEPlayerData.getInstance(EntityArgument.getPlayer(context, "Other")).getHomes().keySet(), builder))
                                .executes(context -> homeOther(context.getSource().asPlayer(),
                                        EntityArgument.getPlayer(context, "Other"),
                                        StringArgumentType.getString(context, "HomeName"))
                                )
                        )
                )
                .requires(source -> source.hasPermissionLevel(2))
        );

        dispatcher.register(
                Commands.literal(delHomeAlias)
                        .then(Commands.argument("Name", StringArgumentType.string())
                                .suggests((context, builder) -> ISuggestionProvider.suggest(SCEPlayerData.getInstance(context.getSource().asPlayer()).getHomes().keySet(), builder))
                                .executes(context -> delHome(context.getSource().asPlayer(), StringArgumentType.getString(context, "Name"))))
                        .executes(context -> delHome(context.getSource().asPlayer(), "home"))
        );

        dispatcher.register(
                Commands.literal(delOtherHomeAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .then(Commands.argument("Name", StringArgumentType.string())
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .suggests((context, builder) -> ISuggestionProvider.suggest(SCEPlayerData.getInstance(EntityArgument.getPlayer(context, "Target")).getHomes().keySet(), builder))
                                        .executes(context -> delOthersHome(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "Target"), StringArgumentType.getString(context, "Name")))
                                )
                        )
                        .requires(source -> source.hasPermissionLevel(2))
        );

        dispatcher.register(
                Commands.literal(listHomesAlias)
                        .executes(context -> listHome(context.getSource().asPlayer()))
        );
        dispatcher.register(
                Commands.literal(listOtherHomesAlias)
                        .then(Commands.argument("other", EntityArgument.player())
                                .requires(commandSource -> commandSource.hasPermissionLevel(2))
                                .executes(context -> listOthersHome(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "other"))))
                        .requires(source -> source.hasPermissionLevel(2))
        );
    }

    private static int setHome(ServerPlayerEntity player, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        if (data.getHomes().size() >= maxHomes) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "reachMaxHome"), maxHomes), false);
            return 1;
        }
        if (data.getHomePos(name) != null) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeExist"), name), false);
            return 1;
        }
        data.setHome(name, new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "setHomeSuccess"), name), false);
        return 1;
    }

    private static int home(ServerPlayerEntity player, String name) {
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
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeNotFound"), name), false);
            return 1;
        }
        data.addTeleportHistory(new TeleportPos(player.getServerWorld().getDimensionKey(), player.getPosition()));
        TeleportUtils.teleport(player, homePos);
        data.setLastHomeTime(System.currentTimeMillis());
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "homeSuccess"), name), true);
        return 1;
    }

    private static int homeOther(ServerPlayerEntity source, ServerPlayerEntity other, String homeName) {
        SCEPlayerData sourceData = SCEPlayerData.getInstance(source);
        if (TeleportUtils.isInCooldown(source, sourceData.getLastHomeOtherTime(), homeOtherCooldownSeconds)) {
            return 1;
        }
        SCEPlayerData otherData = SCEPlayerData.getInstance(other);
        TeleportPos otherHomePos = otherData.getHomePos(homeName);
        if (otherHomePos == null) {
            source.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeOtherNotFound"), otherData.getName(), homeName), false);
            return 1;
        }
        sourceData.addTeleportHistory(new TeleportPos(source.getServerWorld().getDimensionKey(), source.getPosition()));
        TeleportUtils.teleport(source, otherHomePos);
        sourceData.setLastHomeOtherTime(System.currentTimeMillis());
        source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "otherHomeSuccess"), otherData.getName(), homeName), true);
        return 1;
    }

    private static int delHome(ServerPlayerEntity player, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        TeleportPos homePos = data.getHomePos(name);
        if (homePos == null) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeNotFound"), name), false);
            return 1;
        }
        data.delHome(name);
        player.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "delHomeSuccess"), name), false);
        return 1;
    }

    private static int delOthersHome(ServerPlayerEntity source, ServerPlayerEntity target, String name) {
        SCEPlayerData data = SCEPlayerData.getInstance(target);
        if (!data.getHomes().containsKey(name)) {
            source.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "homeOtherNotFound"), data.getName(), name), false);
            return 1;
        }
        data.delHome(name);
        source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "delOthersHomeSuccess"), data.getName(), name), false);
        return 1;
    }

    private static int listHome(ServerPlayerEntity player) {
        SCEPlayerData data = SCEPlayerData.getInstance(player);
        Map<String, TeleportPos> homes = data.getHomes();
        if (homes.isEmpty()) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "noHome")), false);
            return Command.SINGLE_SUCCESS;
        }
        player.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 20)), false);
        Set<String> names = homes.keySet();
        int index = 1;
        for (String name : names) {
            TeleportPos teleportPos = homes.get(name);
            IFormattableTextComponent text = TextUtils.getGreenTextFromString(false, true, false, index + ": " + name);
            text.setStyle(text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + name))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(teleportPos.toString()).appendString("\n")
                            .append(TextUtils.getGreenTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "clickToTeleport"))
                            ))));
            player.sendStatusMessage(text, false);
            index++;
        }
        player.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 20)), false);

        return 1;
    }

    private static int listOthersHome(ServerPlayerEntity source, ServerPlayerEntity other) {
        SCEPlayerData otherData = SCEPlayerData.getInstance(other);
        Map<String, TeleportPos> otherHomes = otherData.getHomes();
        if (otherHomes.isEmpty()) {
            source.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "otherNoHome"), otherData.getName()), false);
            return Command.SINGLE_SUCCESS;
        }
        source.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 20)), false);
        int index = 0;
        for (Map.Entry<String, TeleportPos> e : otherHomes.entrySet()) {
            TeleportPos teleportPos = e.getValue();
            IFormattableTextComponent text = TextUtils.getGreenTextFromString(false, true, false, (index + 1) + ": " + e.getKey());
            text.setStyle(text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + e.getKey()))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(teleportPos.toString()).appendString("\n")
                            .append(TextUtils.getGreenTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "clickToTeleport"))
                            ))));
            source.sendStatusMessage(text, false);
            index++;
        }
        source.sendStatusMessage(new StringTextComponent(TextUtils.getSeparator("=", 20)), false);
        return 1;
    }

}
