package xyz.scottc.scessential.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xyz.scottc.scessential.events.entitycleaner.EntityCleaner;

import java.util.Arrays;
import java.util.List;

public class ConfigEntityCleaner extends AbstractModConfig {

    private ForgeConfigSpec.BooleanValue
            isItemEntityCleanupEnable,
            isMobEntityCleanupEnable,
            isExperienceOrbEntityCleanupEnable,
            isFallingBlocksEntityCleanupEnable,
            isArrowEntityCleanupEnable,
            isTridentEntityCleanupEnable,
            isDamagingProjectileEntityCleanupEnable,
            isShulkerBulletEntityCleanupEnable,
            isFireworkRocketEntityCleanupEnable,
            isItemFrameEntityCleanupEnable,
            isPaintingEntityCleanupEnable,
            isBoatEntityCleanupEnable,
            isTNTEntityCleanupEnable;
    private ForgeConfigSpec.IntValue
            cleanupItemEntitiesIntervalSeconds,
            cleanupItemEntitiesCountdownSeconds,
            cleanupMobEntitiesIntervalSeconds,
            cleanupMobEntitiesCountdownSeconds,
            cleanupOtherEntitiesIntervalSeconds;
    private ForgeConfigSpec.ConfigValue<List<? extends String>>
            itemEntitiesWhitelist,
            mobEntitiesWhitelist;

    public ConfigEntityCleaner(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        builder.push("EntityCleanup");

        this.builder.push("ItemEntities");
        isItemEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up item entities in your server.",
                        "Default value: true")
                .define("isItemEntityCleanupEnable", true);

        cleanupItemEntitiesIntervalSeconds = builder
                .comment("Time interval in seconds between two actions of cleaning item entities.",
                        "Default value: 300 seconds (5 minutes)")
                .defineInRange("cleanItemEntitiesInterval", 300, 1, Integer.MAX_VALUE);

        cleanupItemEntitiesCountdownSeconds = builder
                .comment("Seconds of warning message sent before next action of cleaning item entities.",
                        "Default value: 30 seconds")
                .defineInRange("cleanupItemEntitiesCountdown", 30, 1, Integer.MAX_VALUE);

        itemEntitiesWhitelist = builder
                .comment("List of item registry names (E.g: minecraft:stone) not being cleaned.",
                        "You could use /scessential getItemRegistryName item command with a item hold in your main hand to get it's registry name.",
                        "You could also use minecraft:* or rats:* to add all items of certain mod to the whitelist.")
                .define("ItemEntitiesWhitelist", Arrays.asList("minecraft:diamond", "minecraft:emerald"), ConfigEntityCleaner::isResourceName);
        this.builder.pop();

        this.builder.push("MobEntities");
        isMobEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up mob entities in your server.",
                        "Default value: true")
                .define("isMobEntityCleanupEnable", true);

        cleanupMobEntitiesIntervalSeconds = builder
                .comment("Time interval in seconds between two actions of cleaning mob entities.",
                        "Default value: 360 seconds (6 minutes)")
                .defineInRange("cleanMobEntitiesInterval", 360, 1, Integer.MAX_VALUE);

        this.cleanupMobEntitiesCountdownSeconds = this.builder
                .comment("Seconds of warning message sent before next action of cleaning mob entities.",
                        "Default value: 30 seconds")
                .defineInRange("cleanupMobEntitiesCountdown", 30, 1, Integer.MAX_VALUE);

        mobEntitiesWhitelist = builder
                .comment("List of mob resourcelocation names (E.g: minecraft:cow) not being cleaned.",
                        "You could use /scessential getItemRegistryName mob to get the registry names of nearby mobs. (radius specified in Commands section)",
                        "You could also use minecraft:* or minecolonies:* to add all mobs of certain mod to the whitelist.")
                .define("MobEntitiesWhitelist", Arrays.asList("minecraft:cat", "minecraft:mule", "minecraft:wolf", "minecraft:horse",
                        "minecraft:donkey", "minecraft:wither", "minecraft:guardian", "minecraft:villager", "minecraft:iron_golem", "minecraft:snow_golem",
                        "minecraft:vindicator", "minecraft:ender_dragon", "minecraft:elder_guardian"), ConfigEntityCleaner::isResourceName);
        this.builder.pop();

        this.builder.push("OtherEntities");
        cleanupOtherEntitiesIntervalSeconds = builder
                .comment("Time interval in seconds between two actions of cleaning other entities.",
                        "Default value: 300 seconds (5 minutes)")
                .defineInRange("cleanOtherEntitiesInterval", 300, 1, Integer.MAX_VALUE);

        isExperienceOrbEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up experience orb entities in your server.",
                        "Default value: true")
                .define("IsExperienceOrbEntityCleanupEnable", true);

        isFallingBlocksEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up falling block entities in your server.",
                        "Default value: true")
                .define("IsFallingBlocksEntityCleanupEnable", true);

        isArrowEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up arrow entities in your server.",
                        "Default value: true")
                .define("IsArrowEntityCleanupEnable", true);

        isTridentEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up trident entities in your server.",
                        "Default value: false")
                .define("IsTridentEntityCleanupEnable", false);

        isDamagingProjectileEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up damaging projectile entities (E.g Fireballs and wither skulls) in your server.",
                        "Default value: false")
                .define("IsDamagingProjectileEntityCleanupEnable", false);

        isShulkerBulletEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up shulker bullet entities in your server.",
                        "Default value: true")
                .define("IsShulkerBulletEntityCleanupEnable", true);

        isFireworkRocketEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up firework rocket entities in your server.",
                        "Default value: false")
                .define("IsFireworkRocketEntityCleanupEnable", false);

        isItemFrameEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up item frame entities in your server.",
                        "Default value: false")
                .define("IsItemFrameEntityCleanupEnable", false);

        isPaintingEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up painting entities in your server.",
                        "Default value: false")
                .define("IsPaintingEntityCleanupEnable", false);

        isBoatEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up boat entities in your server.",
                        "Default value: false")
                .define("IsBoatEntityCleanupEnable", false);

        isTNTEntityCleanupEnable = builder
                .comment("Set it to false to disable cleaning up TNT entities (should be the fired TNT) in your server.",
                        "Default value: true")
                .define("IsTNTEntityCleanupEnable", true);
        this.builder.pop();

        builder.pop();
    }

    @Override
    public void get() {
        EntityCleaner.cleanupItemEntitiesIntervalSeconds = cleanupItemEntitiesIntervalSeconds.get();
        EntityCleaner.cleanupItemEntitiesCountdownSeconds = cleanupItemEntitiesCountdownSeconds.get();
        EntityCleaner.cleanupMobEntitiesIntervalSeconds = cleanupMobEntitiesIntervalSeconds.get();
        EntityCleaner.cleanupMobEntitiesCountdownSeconds = this.cleanupMobEntitiesCountdownSeconds.get();
        EntityCleaner.itemEntitiesWhitelist = itemEntitiesWhitelist.get();
        EntityCleaner.mobEntitiesWhitelist = mobEntitiesWhitelist.get();

        EntityCleaner.cleanupOtherEntitiesIntervalSeconds = cleanupOtherEntitiesIntervalSeconds.get();
        EntityCleaner.isItemEntityCleanupEnable = isItemEntityCleanupEnable.get();
        EntityCleaner.isMobEntityCleanupEnable = isMobEntityCleanupEnable.get();
        EntityCleaner.isExperienceOrbEntityCleanupEnable = isExperienceOrbEntityCleanupEnable.get();
        EntityCleaner.isFallingBlocksEntityCleanupEnable = isFallingBlocksEntityCleanupEnable.get();
        EntityCleaner.isArrowEntityCleanupEnable = isArrowEntityCleanupEnable.get();
        EntityCleaner.isTridentEntityCleanupEnable = isTridentEntityCleanupEnable.get();
        EntityCleaner.isDamagingProjectileEntityCleanupEnable = isDamagingProjectileEntityCleanupEnable.get();
        EntityCleaner.isShulkerBulletEntityCleanupEnable = isShulkerBulletEntityCleanupEnable.get();
        EntityCleaner.isFireworkRocketEntityCleanupEnable = isFireworkRocketEntityCleanupEnable.get();
        EntityCleaner.isItemFrameEntityCleanupEnable = isItemFrameEntityCleanupEnable.get();
        EntityCleaner.isPaintingEntityCleanupEnable = isPaintingEntityCleanupEnable.get();
        EntityCleaner.isBoatEntityCleanupEnable = isBoatEntityCleanupEnable.get();
        EntityCleaner.isTNTEntityCleanupEnable = isTNTEntityCleanupEnable.get();
    }

    private static boolean isResourceName(Object o) {
        if (o instanceof String) {
            return ((String) o).contains(":");
        }
        return false;
    }

}
