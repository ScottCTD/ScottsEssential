package xyz.scottc.scessential.commands.info;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.List;
import java.util.Optional;

/**
 *  01/04/2021 19:16
 * /sce getRegistryName
 */
public class CommandGetRegistryName {

    @ConfigField
    public static int entitiesWithinRadius = 3;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(Main.MOD_ID)
                        .then(Commands.literal("getRegistryName")
                                .then(Commands.literal("item")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> getHeldItemRegistryName(context.getSource().getPlayerOrException()))
                                )
                                .then(Commands.literal("mob")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> getNearbyEntitiesRegistryName(context.getSource().getPlayerOrException()))
                                )
                                .requires(source -> source.hasPermission(2))
                        )
        );
    }

    private static int getHeldItemRegistryName(ServerPlayer player) {
        Optional.ofNullable(player.getMainHandItem().getItem().getRegistryName())
                .ifPresent(resourceLocation -> player.sendMessage(TextUtils.getWhiteTextFromString(true, true, false, resourceLocation.toString())
                        .copy().withStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, resourceLocation.toString()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponent(resourceLocation.toString() + "\n").append(
                                                TextUtils.getGreenTextFromI18n(true, false, false,
                                                        TextUtils.getTranslationKey("message", "clickToCopy"))
                                        )))), Util.NIL_UUID));
        return 1;
    }

    private static int getNearbyEntitiesRegistryName(ServerPlayer player) {

        ServerLevel world = player.getLevel();
        BlockPos pos = player.getOnPos();
        List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(
                pos.getX() - entitiesWithinRadius, pos.getY() - entitiesWithinRadius, pos.getZ() - entitiesWithinRadius,
                pos.getX() + entitiesWithinRadius, pos.getY() + entitiesWithinRadius, pos.getZ() + entitiesWithinRadius));
        if (entities.size() == 0) {
            player.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "nearbyNoLiving")), Util.NIL_UUID);
            return 1;
        }
        entities.forEach(target -> Optional.ofNullable(target.getEncodeId())
                .ifPresent(entityString -> player.sendMessage(TextUtils.getWhiteTextFromString(true, true, false, entityString)
                        .copy().withStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entityString))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponent(entityString + "\n").append(
                                                TextUtils.getGreenTextFromI18n(true, false, false,
                                                        TextUtils.getTranslationKey("message", "clickToCopy"))
                                        )))), Util.NIL_UUID)));
        return 1;
    }

}
