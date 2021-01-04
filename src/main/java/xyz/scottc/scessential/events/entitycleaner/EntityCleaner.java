package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityCleaner {

    @ConfigField
    public static int cleanupItemEntitiesIntervalSeconds = 60;
    @ConfigField
    public static int cleanupItemEntitiesCountdownSeconds = 30;
    @ConfigField
    public static int cleanupMobEntitiesIntervalSeconds = 60;
    @ConfigField
    public static int cleanupOtherEntitiesIntervalSeconds = 60;
    @ConfigField
    public static List<? extends String> itemEntitiesWhitelist = Collections.emptyList();
    @ConfigField
    public static List<? extends String> mobEntitiesWhitelist = Collections.emptyList();

    @ConfigField
    public static boolean
            isItemEntityCleanupEnable = true,
            isMobEntityCleanupEnable = true,
            isExperienceOrbEntityCleanupEnable = true,
            isFallingBlocksEntityCleanupEnable = true,
            isArrowEntityCleanupEnable = true,
            isTridentEntityCleanupEnable = false,
            isDamagingProjectileEntityCleanupEnable = false,
            isShulkerBulletEntityCleanupEnable = true,
            isFireworkRocketEntityCleanupEnable = false,
            isItemFrameEntityCleanupEnable = false,
            isPaintingEntityCleanupEnable = false,
            isBoatEntityCleanupEnable = false,
            isTNTEntityCleanupEnable = true;

    private static long clearItemTimer = 0;
    private static boolean isCleanupItemMessageSent = false;
    private static long clearMobTimer = 0;
    private static long otherTimer = 0;
    private static int counter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (counter >= 20) {
            if (event.phase == TickEvent.Phase.END) {
                Iterable<ServerWorld> worlds = Main.SERVER.getWorlds();
                // item entities cleaner
                if (isItemEntityCleanupEnable) {
                    long nextCleanupTime = clearItemTimer + cleanupItemEntitiesIntervalSeconds * 1000L;
                    // if next action of clean within 30s, then send message
                    if (nextCleanupTime - System.currentTimeMillis() <= cleanupItemEntitiesCountdownSeconds * 1000L && !isCleanupItemMessageSent) {
                        Main.sendMessageToAllPlayers(TextUtils.getYellowTextFromI18n(true, false, false,
                                TextUtils.getTranslationKey("message", "cleanupitemcountdown"), cleanupItemEntitiesCountdownSeconds), false);
                        isCleanupItemMessageSent = true;
                    }
                    if (nextCleanupTime <= System.currentTimeMillis()) {
                        int amount = cleanupEntity(worlds, ItemEntity.class, entity -> !new SCEItemEntity((ItemEntity) entity).isInWhitelist());
                        clearItemTimer = System.currentTimeMillis();
                        isCleanupItemMessageSent = false;
                        Main.sendMessageToAllPlayers(TextUtils.getGreenTextFromI18n(false, false, false,
                                TextUtils.getTranslationKey("message", "itemcleanupcomplete"), amount), false);
                    }
                }
                // mob entities cleaner
                if (isMobEntityCleanupEnable) {
                    if (clearMobTimer + cleanupMobEntitiesIntervalSeconds * 1000L <= System.currentTimeMillis()) {
                        int amount = cleanupEntity(worlds, MobEntity.class, entity -> !new SCEMobEntity((MobEntity) entity).isInWhitelist());
                        clearMobTimer = System.currentTimeMillis();
                        Main.sendMessageToAllPlayers(TextUtils.getGreenTextFromI18n(false, false, false,
                                TextUtils.getTranslationKey("message", "mobcleanupcomplete"), amount), false);
                    }
                }
                // Other entities cleaner
                if (otherTimer + cleanupOtherEntitiesIntervalSeconds * 1000L <= System.currentTimeMillis()) {
                    int amount = 0;
                    if (isExperienceOrbEntityCleanupEnable)
                        amount += cleanupEntity(worlds, ExperienceOrbEntity.class, entity -> true);
                    if (isFallingBlocksEntityCleanupEnable)
                        amount += cleanupEntity(worlds, FallingBlockEntity.class, entity -> true);
                    if (isArrowEntityCleanupEnable)
                        amount += cleanupEntity(worlds, AbstractArrowEntity.class, entity -> !(entity instanceof TridentEntity));
                    if (isTridentEntityCleanupEnable)
                        amount += cleanupEntity(worlds, TridentEntity.class, entity -> true);
                    if (isDamagingProjectileEntityCleanupEnable)
                        amount += cleanupEntity(worlds, DamagingProjectileEntity.class, entity -> true);
                    if (isShulkerBulletEntityCleanupEnable)
                        amount += cleanupEntity(worlds, ShulkerBulletEntity.class, entity -> true);
                    if (isFireworkRocketEntityCleanupEnable)
                        amount += cleanupEntity(worlds, FireworkRocketEntity.class, entity -> true);
                    if (isItemFrameEntityCleanupEnable)
                        amount += cleanupEntity(worlds, ItemFrameEntity.class, entity -> true);
                    if (isPaintingEntityCleanupEnable)
                        amount += cleanupEntity(worlds, PaintingEntity.class, entity -> true);
                    if (isBoatEntityCleanupEnable)
                        amount += cleanupEntity(worlds, BoatEntity.class, entity -> true);
                    if (isTNTEntityCleanupEnable)
                        amount += cleanupEntity(worlds, TNTEntity.class, entity -> true);
                    otherTimer = System.currentTimeMillis();
                    Main.sendMessageToAllPlayers(TextUtils.getGreenTextFromI18n(false, false, false,
                            TextUtils.getTranslationKey("message", "misccleanupcomplete"), amount), false);
                }
            }
            counter = 0;
        } else {
            counter++;
        }
    }

    private static int cleanupEntity(Iterable<ServerWorld> worlds, Class<? extends Entity> Type, Predicate<Entity> additionalPredicate) {
        AtomicInteger amount = new AtomicInteger();
        worlds.forEach(world -> world.getEntities()
                .filter(Type::isInstance)
                .filter(additionalPredicate)
                .forEach(entity -> {
                    entity.remove();
                    amount.getAndIncrement();
                }));
        return amount.get();
    }

}