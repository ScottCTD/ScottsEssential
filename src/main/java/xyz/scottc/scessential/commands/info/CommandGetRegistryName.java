package xyz.scottc.scessential.commands.info;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.server.ServerWorld;
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

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(Main.MODID)
                        .then(Commands.literal("getRegistryName")
                                .then(Commands.literal("item")
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(context -> getHeldItemRegistryName(context.getSource().asPlayer()))
                                )
                                .then(Commands.literal("mob")
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(context -> getNearbyEntitiesRegistryName(context.getSource().asPlayer()))
                                )
                                .requires(source -> source.hasPermissionLevel(2))
                        )
        );
    }

    private static int getHeldItemRegistryName(ServerPlayerEntity player) {
        Optional.ofNullable(player.getHeldItemMainhand().getItem().getRegistryName())
                .ifPresent(resourceLocation -> player.sendStatusMessage(TextUtils.getWhiteTextFromString(true, true, false, resourceLocation.toString())
                        .mergeStyle(Style.EMPTY
                                .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, resourceLocation.toString()))
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new StringTextComponent(resourceLocation.toString() + "\n").append(
                                                TextUtils.getGreenTextFromI18n(true, false, false,
                                                        TextUtils.getTranslationKey("message", "clicktocopy"))
                        )))), false));
        return 1;
    }

    private static int getNearbyEntitiesRegistryName(ServerPlayerEntity player) {

        ServerWorld world = player.getServerWorld();
        BlockPos pos = player.getPosition();
        List<Entity> entities = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(
                pos.getX() - entitiesWithinRadius, pos.getY() - entitiesWithinRadius, pos.getZ() - entitiesWithinRadius,
                pos.getX() + entitiesWithinRadius, pos.getY() + entitiesWithinRadius, pos.getZ() + entitiesWithinRadius));
        if (entities.size() == 0) {
            player.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "nearbynoliving")), false);
            return 1;
        }
        entities.forEach(target -> Optional.ofNullable(target.getEntityString())
                .ifPresent(entityString -> player.sendStatusMessage(TextUtils.getWhiteTextFromString(true, true, false, entityString)
                        .mergeStyle(Style.EMPTY
                                .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entityString))
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new StringTextComponent(entityString + "\n").append(
                                                TextUtils.getGreenTextFromI18n(true, false, false,
                                                        TextUtils.getTranslationKey("message", "clicktocopy"))
                                        )))), false)));
        return 1;
    }

}
