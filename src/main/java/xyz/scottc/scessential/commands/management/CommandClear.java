package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.events.entitycleaner.EntityCleaner;
import xyz.scottc.scessential.events.entitycleaner.SCEItemEntity;
import xyz.scottc.scessential.events.entitycleaner.SCEMobEntity;

public class CommandClear {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(Main.MOD_ID)
                        .then(
                                Commands.literal("clean")
                                        .then(
                                                Commands.literal("item")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getAllLevels(), entity -> entity instanceof ItemEntity,
                                                        entity -> new SCEItemEntity((ItemEntity) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("monster")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getAllLevels(), entity -> entity instanceof Monster,
                                                        entity -> new SCEMobEntity((Mob) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("animal")
                                                .executes(context -> EntityCleaner.cleanupEntity(Main.SERVER.getAllLevels(), entity -> (entity instanceof Mob) && !(entity instanceof Monster),
                                                        entity -> new SCEMobEntity((Mob) entity).filtrate()))
                                        )
                                        .then(
                                                Commands.literal("other")
                                                .executes(context -> EntityCleaner.cleanOtherEntities(Main.SERVER.getAllLevels()))
                                        )
                                        .requires(source -> source.hasPermission(2))
                        )
        );
    }

}
