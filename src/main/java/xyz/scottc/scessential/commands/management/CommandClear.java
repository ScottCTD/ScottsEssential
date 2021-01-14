package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.events.entitycleaner.EntityCleaner;
import xyz.scottc.scessential.events.entitycleaner.SCEItemEntity;
import xyz.scottc.scessential.events.entitycleaner.SCEMobEntity;

public class CommandClear {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(Main.MODID)
                        .then(
                                Commands.literal("clean")
                                        .then(
                                                Commands.literal("item")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), ItemEntity.class,
                                                        entity -> !new SCEItemEntity((ItemEntity) entity).isInWhitelist()))
                                        )
                                        .then(
                                                Commands.literal("mob")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), MobEntity.class,
                                                        entity -> !new SCEMobEntity((MobEntity) entity).isInWhitelist()))
                                        )
                                        .then(
                                                Commands.literal("other")
                                                .executes(context -> EntityCleaner.cleanOtherEntities(Main.SERVER.getWorlds()))
                                        )
                                        .requires(source -> source.hasPermissionLevel(2))
                        )
        );
    }

}
