package xyz.scottc.scessential;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import xyz.scottc.scessential.commands.CommandFly;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SERVER_CONFIG;
    // Enable
    public static boolean isSpawnEnable = true;
    public static boolean isHomeEnable = true;
    public static boolean isBackEnable = true;
    public static boolean isRTPEnable = true;
    public static boolean isWarpEnable = true;
    public static boolean isTPAEnable = true;
    // Cooldown
    public static int spawnCooldownSeconds = 3;
    public static int homeCooldownSeconds = 3;
    public static int homeOtherCooldownSeconds = 3;
    public static int backCooldownSeconds = 3;
    public static int rtpCooldownSeconds = 10;
    public static int warpCooldownSeconds = 3;
    public static int tpaCooldownSeconds = 3;
    // home
    public static int maxHomes = 5;
    // Back
    public static int maxBacks = 10;
    // RTP
    public static int maxRTPAttempts = 10;
    public static int minRTPHeightDefault = 40;
    public static int maxRTPHeightDefault = 120;
    public static int minRTPRadiusDefault = 1000;
    public static int maxRTPRadiusDefault = 10000;
    public static int minRTPHeightOverworld = 1;
    public static int maxRTPHeightOverworld = 150;
    public static int minRTPRadiusOverworld = 1000;
    public static int maxRTPRadiusOverworld = 10000;
    public static int minRTPHeightNether = 30;
    public static int maxRTPHeightNether = 100;
    public static int minRTPRadiusNether = 1000;
    public static int maxRTPRadiusNether = 10000;
    public static int minRTPHeightEnd = 40;
    public static int maxRTPHeightEnd = 140;
    public static int minRTPRadiusEnd = 1000;
    public static int maxRTPRadiusEnd = 10000;
    // TPA
    public static int maxTPARequestTimeoutSeconds = 30;

    private static ForgeConfigSpec.BooleanValue
            isSpawnEnableConfig,
            isHomeEnableConfig,
            isBackEnableConfig,
            isRTPEnableConfig,
            isWarpEnableConfig,
            isTPAEnableConfig;
    private static ForgeConfigSpec.IntValue
            spawnCooldownSecondsConfig,
            homeCooldownSecondsConfig,
            homeOtherCooldownSecondsConfig,
            backCooldownSecondsConfig,
            rtpCooldownSecondsConfig,
            warpCooldownSecondsConfig,
            tpaCooldownSecondsConfig,

            maxHomesConfig,

            maxBacksConfig,

            maxRTPAttemptsConfig,
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
            maxRTPRadiusEndConfig,

            maxTPARequestTimeoutSecondsConfig;

    private static ForgeConfigSpec.ConfigValue<? extends String> datePattern;

    static {
        initCommandsConfig();
        SERVER_CONFIG = SERVER_BUILDER.build();
        getData();
    }

    private static void initCommandsConfig() {
        SERVER_BUILDER.push("Commands");

        SERVER_BUILDER.push("Spawn");
        isSpawnEnableConfig = SERVER_BUILDER
                .comment("Set it to false to disable /spawn command.\nDefault value: true")
                .define("IsSpawnEnable", true);
        spawnCooldownSecondsConfig = SERVER_BUILDER
                .comment("The time interval between two /spawn commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("SpawnCooldown", 3, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Back");
        isBackEnableConfig = SERVER_BUILDER
                .comment("Set it to false to disable /back command.\nDefault value: true")
                .define("IsBackEnable", true);
        backCooldownSecondsConfig = SERVER_BUILDER
                .comment("The time interval between two /back commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("BackCooldown", 3, 0, Integer.MAX_VALUE);
        maxBacksConfig = SERVER_BUILDER
                .comment("Max amount of times of /back can use to go back to certain locations. \nDefault value: 10 Times")
                .defineInRange("MaxBacks", 10, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Home");
        isHomeEnableConfig = SERVER_BUILDER
                .comment("Set it to false to disable /home, /sethome, /delhome (/removehome), /listhomes, /homeother, and /listotherhomes command.\nDefault value: true")
                .define("IsSpawnEnable", true);
        homeCooldownSecondsConfig = SERVER_BUILDER
                .comment("The time interval between two /home commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("HomeCooldown", 3, 0, Integer.MAX_VALUE);
        homeOtherCooldownSecondsConfig = SERVER_BUILDER
                .comment("The time interval between two /homeother commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("HomeOtherCooldown", 3, 0, Integer.MAX_VALUE);
        maxHomesConfig = SERVER_BUILDER
                .comment("The max amount of homes that a player can set.\nDefault value: 5 homes")
                .defineInRange("MaxHomes", 5, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Warp");
        isWarpEnableConfig = SERVER_BUILDER
                .comment("Set it to false to disable /warp, /setwarp, /delwarp, and /listwarps command.\nDefault value: true")
                .define("IsWarpEnable", true);
        warpCooldownSecondsConfig = SERVER_BUILDER
                .comment("The time interval between two /warp commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("WarpCooldown", 3, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("TPA");
        isTPAEnableConfig = SERVER_BUILDER
                .comment("Set it to false to disable /tpa, /tpahere, /tpaaccept, /tpadeny, /tphere, and /tpallhere command.\nDefault value: true")
                .define("IsTPAEnable", true);
        tpaCooldownSecondsConfig = SERVER_BUILDER
                .comment("The time interval between two /tpa and /tpahere commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("TPACooldown", 3, 0, Integer.MAX_VALUE);
        maxTPARequestTimeoutSecondsConfig = SERVER_BUILDER
                .comment("If a tpa request last more than this seconds, that tpa request will be considered expired. \nDefault value: 60 seconds")
                .defineInRange("MaxTPARequestExpireTime", 60, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("RTP");
        isRTPEnableConfig = SERVER_BUILDER
                .comment("Set it to false to disable /rtp command.\nDefault value: true")
                .define("IsRTPEnable", true);
        rtpCooldownSecondsConfig = SERVER_BUILDER
                .comment("The time interval between two /rtp commands, or teleport cooldown, in seconds.\nDefault value: 10 seconds")
                .defineInRange("RTPCooldown", 10, 0, Integer.MAX_VALUE);
        maxRTPAttemptsConfig = SERVER_BUILDER
                .comment("Max attempts for /rtp to find a safe landing site.\nDefault value: 10 Attempts")
                .defineInRange("MaxRTPAttempts", 10, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.push("OverworldSettings");
        minRTPHeightOverworldConfig = SERVER_BUILDER
                .comment("The min height in overworld that the /rtp commands could reach.\nDefault value: 1 Blocks")
                .defineInRange("OverworldMinHeight", 1, 0, 256);
        maxRTPHeightOverworldConfig = SERVER_BUILDER
                .comment("The max height in overworld that the /rtp commands could reach.\nDefault value: 150 Blocks")
                .defineInRange("OverworldMaxHeight", 150, 0, 256);
        minRTPRadiusOverworldConfig = SERVER_BUILDER
                .comment("The min radius (centered on player) in overworld that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("OverworldMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusOverworldConfig = SERVER_BUILDER
                .comment("The max radius (centered on player) in overworld that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("OverworldMaxRadius", 10000, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("TheNetherSettings");
        minRTPHeightNetherConfig = SERVER_BUILDER
                .comment("The min height in the nether that the /rtp commands could reach.\nDefault value: 30 Blocks")
                .defineInRange("TheNetherMinHeight", 30, 0, 128);
        maxRTPHeightNetherConfig = SERVER_BUILDER
                .comment("The max height in the nether that the /rtp commands could reach.\nDefault value: 100 Blocks")
                .defineInRange("TheNetherMaxHeight", 100, 0, 128);
        minRTPRadiusNetherConfig = SERVER_BUILDER
                .comment("The min radius (centered on player) in the nether that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("TheNetherMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusNetherConfig = SERVER_BUILDER
                .comment("The max radius (centered on player) in the nether that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("TheNetherMaxRadius", 10000, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("TheEndSettings");
        minRTPHeightEndConfig = SERVER_BUILDER
                .comment("The min height in the end that the /rtp commands could reach.\nDefault value: 40 Blocks")
                .defineInRange("TheEndMinHeight", 40, 0, 256);
        maxRTPHeightEndConfig = SERVER_BUILDER
                .comment("The max height in the end that the /rtp commands could reach.\nDefault value: 140 Blocks")
                .defineInRange("TheEndMaxHeight", 140, 0, 256);
        minRTPRadiusEndConfig = SERVER_BUILDER
                .comment("The min radius (centered on player) in the end that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("TheEndMinRadius", 1000, 0, Integer.MAX_VALUE);
        maxRTPRadiusEndConfig = SERVER_BUILDER
                .comment("The max radius (centered on player) in the end that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("TheEndMaxRadius", 10000, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("DefaultSettings");
        minRTPHeightDefaultConfig = SERVER_BUILDER
                .comment("The min height in any other world that the /rtp commands could reach.\nDefault value: 40 Blocks")
                .defineInRange("DefaultMinHeight", 40, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxRTPHeightDefaultConfig = SERVER_BUILDER
                .comment("The max height in any other world that the /rtp commands could reach.\nDefault value: 120 Blocks")
                .defineInRange("DefaultMaxHeight", 120, Integer.MIN_VALUE, Integer.MAX_VALUE);
        minRTPRadiusDefaultConfig = SERVER_BUILDER
                .comment("The min radius (centered on player) in any other world that the /rtp commands could reach.\nDefault value: 1000 Blocks")
                .defineInRange("DefaultMinRadius", 1000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxRTPRadiusDefaultConfig = SERVER_BUILDER
                .comment("The max radius (centered on player) in any other world that the /rtp commands could reach.\nDefault value: 10000 Blocks")
                .defineInRange("DefaultMaxRadius", 10000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("Fly");
        datePattern = SERVER_BUILDER
                .comment("The date format used to display the deadline of flying.",
                        "A valid date format should follow the pattern described in JavaDoc: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html",
                        "If you don't know what it is, please do not modify it.",
                        "Default value: \"hh:mm:ss MM/dd/yyyy\"")
                .define("DatePattern", "hh:mm:ss MM/dd/yyyy");
        SERVER_BUILDER.pop();

        SERVER_BUILDER.pop();
    }

    public static void getData() {
        // Enable
        isSpawnEnable = isSpawnEnableConfig.get();
        isHomeEnable = isHomeEnableConfig.get();
        isBackEnable = isBackEnableConfig.get();
        isRTPEnable = isRTPEnableConfig.get();
        isWarpEnable = isWarpEnableConfig.get();
        isTPAEnable = isTPAEnableConfig.get();

        // Cooldown
        spawnCooldownSeconds = spawnCooldownSecondsConfig.get();
        homeCooldownSeconds = homeCooldownSecondsConfig.get();
        homeOtherCooldownSeconds = homeOtherCooldownSecondsConfig.get();
        backCooldownSeconds = backCooldownSecondsConfig.get();
        rtpCooldownSeconds = rtpCooldownSecondsConfig.get();
        warpCooldownSeconds = warpCooldownSecondsConfig.get();
        tpaCooldownSeconds = tpaCooldownSecondsConfig.get();

        // home
        maxHomes = maxHomesConfig.get();

        // back
        maxBacks = maxBacksConfig.get();

        // rtp
        maxRTPAttempts = maxRTPAttemptsConfig.get();
        minRTPHeightDefault = minRTPHeightDefaultConfig.get();
        maxRTPHeightDefault = maxRTPHeightDefaultConfig.get();
        minRTPRadiusDefault = minRTPRadiusDefaultConfig.get();
        maxRTPRadiusDefault = maxRTPRadiusDefaultConfig.get();
        minRTPHeightOverworld = minRTPHeightOverworldConfig.get();
        maxRTPHeightOverworld = maxRTPHeightOverworldConfig.get();
        minRTPRadiusOverworld = minRTPRadiusOverworldConfig.get();
        maxRTPRadiusOverworld = maxRTPRadiusOverworldConfig.get();
        minRTPHeightNether = minRTPHeightNetherConfig.get();
        maxRTPHeightNether = maxRTPHeightNetherConfig.get();
        minRTPRadiusNether = minRTPRadiusNetherConfig.get();
        maxRTPRadiusNether = maxRTPRadiusNetherConfig.get();
        minRTPHeightEnd = minRTPHeightEndConfig.get();
        maxRTPHeightEnd = maxRTPHeightEndConfig.get();
        minRTPRadiusEnd = minRTPRadiusEndConfig.get();
        maxRTPRadiusEnd = maxRTPRadiusEndConfig.get();

        // tpa
        maxTPARequestTimeoutSeconds = maxTPARequestTimeoutSecondsConfig.get();

        // fly
        CommandFly.datePattern = datePattern.get();
    }

    @SubscribeEvent
    public static void onLoading(ModConfig.Loading event) {
        getData();
    }

    @SubscribeEvent
    public static void onReloading(ModConfig.Reloading event) {
        getData();
    }
}
