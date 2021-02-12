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

public class CommandClean {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(Main.MOD_ID)
                        .then(
                                Commands.literal("clean")
                                        .then(
                                                Commands.literal("items")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), entity -> entity instanceof ItemEntity,
                                                        entity -> new SCEItemEntity((ItemEntity) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("monsters")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), entity -> entity instanceof MonsterEntity,
                                                        entity -> new SCEMobEntity((MobEntity) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("animals")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getWorlds(), entity -> (entity instanceof MobEntity) && !(entity instanceof MonsterEntity),
                                                        entity -> new SCEMobEntity((MobEntity) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("others")
                                                .executes(context -> EntityCleaner.cleanOtherEntities(Main.SERVER.getWorlds()))
                                        )
                                        .requires(source -> source.hasPermissionLevel(2))
                        )
        );
    }

}
