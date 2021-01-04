package xyz.scottc.scessential.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xyz.scottc.scessential.commands.info.CommandGetRegistryName;
import xyz.scottc.scessential.commands.management.CommandFly;
import xyz.scottc.scessential.commands.teleport.*;

public class ConfigCommands extends AbstractModConfig {

    // Spawn
    private static ForgeConfigSpec.BooleanValue isSpawnEnableConfig;
    private static ForgeConfigSpec.IntValue spawnCooldownSecondsConfig;

    // Home
    private static ForgeConfigSpec.BooleanValue isHomeEnableConfig;
    private static ForgeConfigSpec.IntValue homeCooldownSecondsConfig, homeOtherCooldownSecondsConfig;
    private static ForgeConfigSpec.IntValue maxHomesConfig;

    // Back
    private static ForgeConfigSpec.BooleanValue isBackEnableConfig;
    private static ForgeConfigSpec.IntValue backCooldownSecondsConfig;
    private static ForgeConfigSpec.IntValue maxBacksConfig;

    // Warp
    private static ForgeConfigSpec.BooleanValue isWarpEnableConfig;
    private static ForgeConfigSpec.IntValue warpCooldownSecondsConfig;

    // TPA
    private static ForgeConfigSpec.BooleanValue isTPAEnableConfig;
    private static ForgeConfigSpec.IntValue tpaCooldownSecondsConfig;
    private static ForgeConfigSpec.IntValue maxTPARequestTimeoutSecondsConfig;

    // RTP
    private static ForgeConfigSpec.BooleanValue isRTPEnableConfig;
    private static ForgeConfigSpec.IntValue rtpCooldownSecondsConfig;
    private static ForgeConfigSpec.IntValue maxRTPAttemptsConfig;
    private static ForgeConfigSpec.IntValue
            minRTPHeightDefaultConfig,
            maxRTPHeightDefaultConfig,
            minRTPRadiusDefaultConfig,
            maxRTPRadiusDefaultConfig,
            minRTPHeightOverworldConfig,
            maxRTPHeightOverworldConfig,
            minRTPRadiusOverworldConfig,
            maxRTPRadiusOverworldConfig,
            minRTPHeightNetherConfig,
            maxRTPHeightNetherConfig,
            minRTPRadiusNetherConfig,
            maxRTPRadiusNetherConfig,
            minRTPHeightEndConfig,
            maxRTPHeightEndConfig,
            minRTPRadiusEndConfig,
            maxRTPRadiusEndConfig;

    // fly
    private static ForgeConfigSpec.BooleanValue isFlyEnable;
    private static ForgeConfigSpec.ConfigValue<? extends String> datePattern;

    // scessential getRegistryName mob
    private static ForgeConfigSpec.IntValue entitiesWithinRadius;

    public ConfigCommands(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    void init() {
        this.builder.push("Commands");

        this.builder.push("Spawn");
        isSpawnEnableConfig = this.builder
                .comment("Set it to false to disable /spawn command.\nDefault value: true")
                .define("IsSpawnEnable", true);
        spawnCooldownSecondsConfig = this.builder
                .comment("The time interval between two /spawn commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("SpawnCooldown", 3, 0, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Back");
        isBackEnableConfig = this.builder
                .comment("Set it to false to disable /back command.\nDefault value: true")
                .define("IsBackEnable", true);
        backCooldownSecondsConfig = this.builder
                .comment("The time interval between two /back commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("BackCooldown", 3, 0, Integer.MAX_VALUE);
        maxBacksConfig = this.builder
                .comment("Max amount of times of /back can use to go back to certain locations. \nDefault value: 10 Times")
                .defineInRange("MaxBacks", 10, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Home");
        isHomeEnableConfig = this.builder
                .comment("Set it to false to disable /home, /sethome, /delhome (/removehome), /listhomes, /homeother, and /listotherhomes command.\nDefault value: true")
                .define("IsSpawnEnable", true);
        homeCooldownSecondsConfig = this.builder
                .comment("The time interval between two /home commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("HomeCooldown", 3, 0, Integer.MAX_VALUE);
        homeOtherCooldownSecondsConfig = this.builder
                .comment("The time interval between two /homeother commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("HomeOtherCooldown", 3, 0, Integer.MAX_VALUE);
        maxHomesConfig = this.builder
                .comment("The max amount of homes that a player can set.\nDefault value: 5 homes")
                .defineInRange("MaxHomes", 5, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Warp");
        isWarpEnableConfig = this.builder
                .comment("Set it to false to disable /warp, /setwarp, /delwarp, and /listwarps command.\nDefault value: true")
                .define("IsWarpEnable", true);
        warpCooldownSecondsConfig = this.builder
                .comment("The time interval between two /warp commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("WarpCooldown", 3, 0, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("TPA");
        isTPAEnableConfig = this.builder
                .comment("Set it to false to disable /tpa, /tpahere, /tpaaccept, /tpadeny, /tphere, and /tpallhere command.\nDefault value: true")
                .define("IsTPAEnable", true);
        tpaCooldownSecondsConfig = this.builder
                .comment("The time interval between two /tpa and /tpahere commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("TPACooldown", 3, 0, Integer.MAX_VALUE);
        maxTPARequestTimeoutSecondsConfig = this.builder
                .comment("If a tpa request last more than this seconds, that tpa request will be considered expired. \nDefault value: 60 seconds")
                .defineInRange("MaxTPARequestExpireTime", 60, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("RTP");
        isRTPEnableConfig = this.builder
                .comment("Set it to false to disable /rtp command.\nDefault value: true")
                .define("IsRTPEnable", true);
        rtpCooldownSecondsConfig = this.builder
                .comment("The time interval between two /rtp commands, or teleport cooldown, in seconds.\nDefault value: 10 seconds")
                .defineInRange("RTPCooldown", 10, 0, Integer.MAX_VALUE);
        maxRTPAttemptsConfig = this.builder
                .comment("Max attempts for /rtp to find a safe landing site.\nDefault value: 10 Attempts")
                .defineInRange("MaxRTPAttempts", 10, 1, Integer.MAX_VALUE);
        this.builder.push("OverworldSettings");
        minRTPHeightOverworldConfig = this.builder
                .comment("The min height in overworld that the /rtp commands could reach.\nDefault value: 1 Blocks")
                .defineInRange("OverworldMinHeight", 1, 0, 256);
        maxRTPHeightOverworldConfig = this.builder
                .comment("The max height in overworld that the /rtp commands could reach.\nDefault value: 150 Blocks")
                .defineInRange("OverworldMaxHeight", 150, 0, 256);
        minRTPRadiusOverworldConfig = this.builder
                .comment("The min radius (centered on player) in overworld that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("OverworldMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusOverworldConfig = this.builder
                .comment("The max radius (centered on player) in overworld that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("OverworldMaxRadius", 10000, 0, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.push("TheNetherSettings");
        minRTPHeightNetherConfig = this.builder
                .comment("The min height in the nether that the /rtp commands could reach.\nDefault value: 30 Blocks")
                .defineInRange("TheNetherMinHeight", 30, 0, 128);
        maxRTPHeightNetherConfig = this.builder
                .comment("The max height in the nether that the /rtp commands could reach.\nDefault value: 100 Blocks")
                .defineInRange("TheNetherMaxHeight", 100, 0, 128);
        minRTPRadiusNetherConfig = this.builder
                .comment("The min radius (centered on player) in the nether that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("TheNetherMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusNetherConfig = this.builder
                .comment("The max radius (centered on player) in the nether that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("TheNetherMaxRadius", 10000, 0, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.push("TheEndSettings");
        minRTPHeightEndConfig = this.builder
                .comment("The min height in the end that the /rtp commands could reach.\nDefault value: 40 Blocks")
                .defineInRange("TheEndMinHeight", 40, 0, 256);
        maxRTPHeightEndConfig = this.builder
                .comment("The max height in the end that the /rtp commands could reach.\nDefault value: 140 Blocks")
                .defineInRange("TheEndMaxHeight", 140, 0, 256);
        minRTPRadiusEndConfig = this.builder
                .comment("The min radius (centered on player) in the end that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("TheEndMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusEndConfig = this.builder
                .comment("The max radius (centered on player) in the end that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("TheEndMaxRadius", 10000, 0, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.push("DefaultSettings");
        minRTPHeightDefaultConfig = this.builder
                .comment("The min height in any other world that the /rtp commands could reach.\nDefault value: 40 Blocks")
                .defineInRange("DefaultMinHeight", 40, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxRTPHeightDefaultConfig = this.builder
                .comment("The max height in any other world that the /rtp commands could reach.\nDefault value: 120 Blocks")
                .defineInRange("DefaultMaxHeight", 120, Integer.MIN_VALUE, Integer.MAX_VALUE);
        minRTPRadiusDefaultConfig = this.builder
                .comment("The min radius (centered on player) in any other world that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("DefaultMinRadius", 1000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxRTPRadiusDefaultConfig = this.builder
                .comment("The max radius (centered on player) in any other world that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("DefaultMaxRadius", 10000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.pop();

        this.builder.push("Fly");
        isFlyEnable = this.builder
                .comment("Set it to false to disable /fly command.",
                        "Default value: true")
                .define("IsFlyEnable", true);

        datePattern = this.builder
                .comment("The date format used to display the deadline of flying.",
                        "A valid date format should follow the pattern described in JavaDoc: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html",
                        "If you don't know what it is, please do not modify it.",
                        "Default value: \"hh:mm:ss MM/dd/yyyy\"")
                .define("DatePattern", "hh:mm:ss MM/dd/yyyy");
        this.builder.pop();

        // /scessential ....
        this.builder.push("Scessential");
        this.builder.push("Info");
        // getRegistryName
        entitiesWithinRadius = this.builder
                .comment("The searching radius of command /scessential getRegistryName mob to get the registry names of nearby mobs in certain radius",
                        "The radius is specified here.",
                        "Default value: 3 blocks (a 7 * 7 * 7 cube)")
                .defineInRange("Radius", 3, 1, Integer.MAX_VALUE);
        this.builder.pop();
        this.builder.pop();

        this.builder.pop();
    }

    @Override
    public void get() {
        // Spawn
        CommandSpawn.isSpawnEnable = isSpawnEnableConfig.get();
        CommandSpawn.spawnCooldownSeconds = spawnCooldownSecondsConfig.get();

        // Back
        CommandBack.isBackEnable = isBackEnableConfig.get();
        CommandBack.backCooldownSeconds = backCooldownSecondsConfig.get();
        CommandBack.maxBacks = maxBacksConfig.get();

        // Home
        CommandHome.isHomeEnable = isHomeEnableConfig.get();
        CommandHome.homeCooldownSeconds = homeCooldownSecondsConfig.get();
        CommandHome.homeOtherCooldownSeconds = homeOtherCooldownSecondsConfig.get();
        CommandHome.maxHomes = maxHomesConfig.get();

        // TPA
        CommandTPA.isTPAEnable = isTPAEnableConfig.get();
        CommandTPA.tpaCooldownSeconds = tpaCooldownSecondsConfig.get();
        CommandTPA.maxTPARequestTimeoutSeconds = maxTPARequestTimeoutSecondsConfig.get();

        // Warp
        CommandWarp.isWarpEnable = isWarpEnableConfig.get();
        CommandWarp.warpCooldownSeconds = warpCooldownSecondsConfig.get();

        // RTP
        CommandRTP.isRTPEnable = isRTPEnableConfig.get();
        CommandRTP.rtpCooldownSeconds = rtpCooldownSecondsConfig.get();
        CommandRTP.maxRTPAttempts = maxRTPAttemptsConfig.get();
        CommandRTP.minRTPHeightDefault = minRTPHeightDefaultConfig.get();
        CommandRTP.maxRTPHeightDefault = maxRTPHeightDefaultConfig.get();
        CommandRTP.minRTPRadiusDefault = minRTPRadiusDefaultConfig.get();
        CommandRTP.maxRTPRadiusDefault = maxRTPRadiusDefaultConfig.get();
        CommandRTP.minRTPHeightOverworld = minRTPHeightOverworldConfig.get();
        CommandRTP.maxRTPHeightOverworld = maxRTPHeightOverworldConfig.get();
        CommandRTP.minRTPRadiusOverworld = minRTPRadiusOverworldConfig.get();
        CommandRTP.maxRTPRadiusOverworld = maxRTPRadiusOverworldConfig.get();
        CommandRTP.minRTPHeightNether = minRTPHeightNetherConfig.get();
        CommandRTP.maxRTPHeightNether = maxRTPHeightNetherConfig.get();
        CommandRTP.minRTPRadiusNether = minRTPRadiusNetherConfig.get();
        CommandRTP.maxRTPRadiusNether = maxRTPRadiusNetherConfig.get();
        CommandRTP.minRTPHeightEnd = minRTPHeightEndConfig.get();
        CommandRTP.maxRTPHeightEnd = maxRTPHeightEndConfig.get();
        CommandRTP.minRTPRadiusEnd = minRTPRadiusEndConfig.get();
        CommandRTP.maxRTPRadiusEnd = maxRTPRadiusEndConfig.get();

        // fly
        CommandFly.datePattern = datePattern.get();

        // scessential getRegistryName mob
        CommandGetRegistryName.entitiesWithinRadius = entitiesWithinRadius.get();
    }

}
