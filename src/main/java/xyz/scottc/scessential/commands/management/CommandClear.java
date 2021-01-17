package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
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
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), entity -> entity instanceof ItemEntity,
                                                        entity -> !new SCEItemEntity((ItemEntity) entity).isInWhitelist()))
                                        )
                                        .then(
                                                Commands.literal("monster")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), entity -> entity instanceof MonsterEntity,
                                                        entity -> !new SCEMobEntity((MobEntity) entity).isInWhitelist()))
                                        )
                                        .then(
                                                Commands.literal("animal")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), entity -> (entity instanceof MobEntity) && !(entity instanceof MonsterEntity),
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
