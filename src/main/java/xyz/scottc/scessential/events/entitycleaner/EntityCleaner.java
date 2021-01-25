package xyz.scottc.scessential.events.entitycleaner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.MonsterEntity;
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

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityCleaner {

    // Clean item
    @ConfigField
    public static boolean isItemEntityCleanupEnable = true;
    @ConfigField
    public static int cleanupItemEntitiesIntervalSeconds = 60;
    @ConfigField
    public static String cleanedUpItemEntitiesMessage = "null";
    @ConfigField
    public static int cleanupItemEntitiesCountdownSeconds = 30;
    @ConfigField
    public static String cleanupItemEntitiesCountdownMessage = "null";
    // true -> whitelist false -> blacklist
    @ConfigField
    public static boolean itemEntitiesMatchMode = true;
    @ConfigField
    public static List<? extends String> itemEntitiesWhitelist = Collections.emptyList();
    @ConfigField
    public static List<? extends String> itemEntitiesBlacklist = Collections.emptyList();

    // clean mob
    @ConfigField
    public static boolean
            isMobEntityCleanupEnable = true,
            isAnimalEntitiesCleanupEnable = true,
            isMonsterEntitiesCleanupEnable = true;
    @ConfigField
    public static int cleanupMobEntitiesIntervalSeconds = 60;
    @ConfigField
    public static String cleanedUpMobEntitiesMessage = "null";
    @ConfigField
    public static int cleanupMobEntitiesCountdownSeconds = 30;
    @ConfigField
    public static String cleanupMobEntitiesCountdownMessage = "null";
    @ConfigField
    public static boolean mobEntitiesMatchMode = true;
    @ConfigField
    public static List<? extends String> mobEntitiesWhitelist = Collections.emptyList();
    @ConfigField
    public static List<? extends String> mobEntitiesBlacklist = Collections.emptyList();

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
                                    TextUtils.getTranslationKey("message", "cleanupItemCountdown"), cleanupItemEntitiesCountdownSeconds), cleanupItemEntitiesCountdownSeconds);
                            isCleanupItemCountdownMessageSent = true;
                        }
                        // real clean
                        if (nextCleanupTime <= System.currentTimeMillis()) {
                            int amount = cleanupEntity(worlds, entity -> entity instanceof ItemEntity, entity -> new SCEItemEntity((ItemEntity) entity).filtrate());
                            clearItemTimer = System.currentTimeMillis();
                            isCleanupItemCountdownMessageSent = false;
                            sendMessage(cleanedUpItemEntitiesMessage, TextUtils.getGreenTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("message", "itemCleanupComplete"), amount), amount);
                        }
                    }

                    // mob entities cleaner
                    if (isMobEntityCleanupEnable) {
                        long nextCleanupTime = clearMobTimer + cleanupMobEntitiesIntervalSeconds * 1000L;
                        if (nextCleanupTime - System.currentTimeMillis() <= cleanupMobEntitiesCountdownSeconds * 1000L && !isCleanupMobMessageSent) {
                            sendMessage(cleanupMobEntitiesCountdownMessage, TextUtils.getYellowTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "cleanupMobCountdown"), cleanupMobEntitiesCountdownSeconds), cleanupMobEntitiesCountdownSeconds);
                            isCleanupMobMessageSent = true;
                        }
                        if (nextCleanupTime <= System.currentTimeMillis()) {
                            int amount = 0;
                            if (isAnimalEntitiesCleanupEnable)
                                amount += cleanupEntity(worlds, entity -> (entity instanceof MobEntity) && !(entity instanceof MonsterEntity),
                                        entity -> new SCEMobEntity((MobEntity) entity).filtrate());
                            if (isMonsterEntitiesCleanupEnable)
                                amount += cleanupEntity(worlds, entity -> entity instanceof MonsterEntity, entity -> new SCEMobEntity((MobEntity) entity).filtrate());
                            clearMobTimer = System.currentTimeMillis();
                            isCleanupMobMessageSent = false;
                            sendMessage(cleanedUpMobEntitiesMessage, TextUtils.getGreenTextFromI18n(false, false, false,
                                    TextUtils.getTranslationKey("message", "mobCleanupComplete"), amount), amount);
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
            amount += cleanupEntity(worlds, entity -> entity instanceof ExperienceOrbEntity, entity -> true);
        if (isFallingBlocksEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof FallingBlockEntity, entity -> true);
        if (isArrowEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof AbstractArrowEntity, entity -> !(entity instanceof TridentEntity));
        if (isTridentEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof TridentEntity, entity -> true);
        if (isDamagingProjectileEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof DamagingProjectileEntity, entity -> true);
        if (isShulkerBulletEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof ShulkerBulletEntity, entity -> true);
        if (isFireworkRocketEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof FireworkRocketEntity, entity -> true);
        if (isItemFrameEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof ItemFrameEntity, entity -> true);
        if (isPaintingEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof PaintingEntity, entity -> true);
        if (isBoatEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof BoatEntity, entity -> true);
        if (isTNTEntityCleanupEnable)
            amount += cleanupEntity(worlds, entity -> entity instanceof TNTEntity, entity -> true);
        return amount;
    }

    public static int cleanupEntity(Iterable<ServerWorld> worlds, Predicate<Entity> type, Predicate<Entity> additionalPredicate) {
        AtomicInteger amount = new AtomicInteger();
        worlds.forEach(world -> world.getEntities()
                .filter(entity -> entity.getCustomName() == null)
                .filter(type)
                .filter(additionalPredicate)
                .forEach(entity -> {
                    entity.remove();
                    if (entity instanceof ItemEntity) {
                        amount.getAndAdd(((ItemEntity) entity).getItem().getCount());
                    } else {
                        amount.getAndIncrement();
                    }
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
