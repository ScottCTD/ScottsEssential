package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityCleaner {

    // Clean item
    @ConfigField
    public static boolean isItemEntityCleanupEnable = true;
    @ConfigField
    public static int cleanupItemEntitiesIntervalSeconds = 60;
    @ConfigField
    public static String cleanedupItemEntitiesMessage = "null";
    @ConfigField
    public static int cleanupItemEntitiesCountdownSeconds = 30;
    @ConfigField
    public static String cleanupItemEntitiesCountdownMessage = "null";
    @ConfigField
    public static List<? extends String> itemEntitiesWhitelist = Collections.emptyList();

    // clean mob
    @ConfigField
    public static boolean isMobEntityCleanupEnable = true;
    @ConfigField
    public static int cleanupMobEntitiesIntervalSeconds = 60;
    @ConfigField
    public static String cleanedupMobEntitiesMessage = "null";
    @ConfigField
    public static int cleanupMobEntitiesCountdownSeconds = 30;
    @ConfigField
    public static String cleanupMobEntitiesCountdownMessage = "null";
    @ConfigField
    public static List<? extends String> mobEntitiesWhitelist = Collections.emptyList();

    // clean other
    @ConfigField
    public static int cleanupOtherEntitiesIntervalSeconds = 60;
    @ConfigField
    public static boolean
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
    private static boolean isCleanupItemCountdownMessageSent = false;
    private static boolean isCleanupMobMessageSent = false;
    private static long clearMobTimer = 0;
    private static long otherTimer = 0;
    private static int counter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (counter >= 2 * 20) {
            if (event.phase == TickEvent.Phase.END) {
                counter = 0;
                Optional.ofNullable(Main.SERVER).ifPresent(server -> {
                    Iterable<ServerWorld> worlds = server.getWorlds();

                    // item entities cleaner
                    if (isItemEntityCleanupEnable) {
                        long nextCleanupTime = clearItemTimer + cleanupItemEntitiesIntervalSeconds * 1000L;
                        // countdown
                        if (nextCleanupTime - System.currentTimeMillis() <= cleanupItemEntitiesCountdownSeconds * 1000L && !isCleanupItemCountdownMessageSent) {
                            sendMessage(cleanupItemEntitiesCountdownMessage, TextUtils.getYellowTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "cleanupitemcountdown"), cleanupItemEntitiesCountdownSeconds), cleanupItemEntitiesCountdownSeconds);
                            isCleanupItemCountdownMessageSent = true;
                        }
                        // real clean
                        if (nextCleanupTime <= System.currentTimeMillis()) {
                            int amount = cleanupEntity(worlds, ItemEntity.class, entity -> !new SCEItemEntity((ItemEntity) entity).isInWhitelist());
                            clearItemTimer = System.currentTimeMillis();
                            isCleanupItemCountdownMessageSent = false;
                            sendMessage(cleanedupItemEntitiesMessage, TextUtils.getGreenTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("message", "itemcleanupcomplete"), amount), amount);
                        }
                    }

                    // mob entities cleaner
                    if (isMobEntityCleanupEnable) {
                        long nextCleanupTime = clearMobTimer + cleanupMobEntitiesIntervalSeconds * 1000L;
                        if (nextCleanupTime - System.currentTimeMillis() <= cleanupMobEntitiesCountdownSeconds * 1000L && !isCleanupMobMessageSent) {
                            sendMessage(cleanupMobEntitiesCountdownMessage, TextUtils.getYellowTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "cleanupmobcountdown"), cleanupMobEntitiesCountdownSeconds), cleanupMobEntitiesCountdownSeconds);
                            isCleanupMobMessageSent = true;
                        }
                        if (nextCleanupTime <= System.currentTimeMillis()) {
                            int amount = cleanupEntity(worlds, MobEntity.class, entity -> !new SCEMobEntity((MobEntity) entity).isInWhitelist());
                            clearMobTimer = System.currentTimeMillis();
                            isCleanupMobMessageSent = false;
                            sendMessage(cleanedupMobEntitiesMessage, TextUtils.getGreenTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("message", "mobcleanupcomplete"), amount), amount);
                        }
                    }

                    // Other entities cleaner
                    if (otherTimer + cleanupOtherEntitiesIntervalSeconds * 1000L <= System.currentTimeMillis()) {
                        int amount = cleanOtherEntities(worlds);
                        otherTimer = System.currentTimeMillis();
                    }

                });
            }
        }
        counter++;
    }

    public static int cleanOtherEntities(Iterable<ServerWorld> worlds) {
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
        return amount;
    }

    public static int cleanupEntity(Iterable<ServerWorld> worlds, Class<? extends Entity> Type, Predicate<Entity> additionalPredicate) {
        AtomicInteger amount = new AtomicInteger();
        worlds.forEach(world -> world.getEntities()
                .filter(Type::isInstance)
                .filter(entity -> entity.getCustomName() == null)
                .filter(additionalPredicate)
                .forEach(entity -> {
                    entity.remove();
                    amount.getAndIncrement();
                }));
        return amount.get();
    }

    private static void sendMessage(String customizedMessage, ITextComponent defaultMessage, Object... formatters) {
        if ("null".equals(customizedMessage)) {
            Main.sendMessageToAllPlayers(defaultMessage, false);
        } else if (!customizedMessage.isEmpty()) {
            Main.sendMessageToAllPlayers(new StringTextComponent(String.format(customizedMessage, formatters)), false);
        }
    }

}
