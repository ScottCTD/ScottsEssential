package xyz.scottc.scessential.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xyz.scottc.scessential.commands.info.CommandGetRegistryName;
import xyz.scottc.scessential.commands.info.CommandRank;
import xyz.scottc.scessential.commands.management.CommandFly;
import xyz.scottc.scessential.commands.management.CommandHat;
import xyz.scottc.scessential.commands.management.CommandOpenInv;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.commands.teleport.*;

public class ConfigCommands extends AbstractModConfig {

    // Spawn
    private ForgeConfigSpec.BooleanValue isSpawnEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String> spawnAlias;
    private ForgeConfigSpec.IntValue spawnCooldownSecondsConfig;

    // Home
    private ForgeConfigSpec.BooleanValue isHomeEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String>
            setHomeAlias,
            homeAlias,
            homeOtherAlias,
            delHomeAlias,
            listHomesAlias,
            listOtherHomesAlias,
            delOtherHomeAlias;
    private ForgeConfigSpec.IntValue homeCooldownSecondsConfig, homeOtherCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxHomesConfig;

    // Back
    private ForgeConfigSpec.BooleanValue isBackEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String> backAlias;
    private ForgeConfigSpec.IntValue backCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxBacksConfig;

    // Warp
    private ForgeConfigSpec.BooleanValue isWarpEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String>
            setWarpAlias,
            warpAlias,
            listWarpsAlias,
            delWarpAlias;
    private ForgeConfigSpec.IntValue warpCooldownSecondsConfig;

    // TPA
    private ForgeConfigSpec.BooleanValue isTPAEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String>
            tpaAlias,
            tpaHereAlias,
            tpHereAlias,
            tpAllHereAlias;
    private ForgeConfigSpec.IntValue tpaCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxTPARequestTimeoutSecondsConfig;

    // RTP
    private ForgeConfigSpec.BooleanValue isRTPEnableConfig;
    private ForgeConfigSpec.ConfigValue<? extends String> rtpAlias;
    private ForgeConfigSpec.IntValue rtpCooldownSecondsConfig;
    private ForgeConfigSpec.IntValue maxRTPAttemptsConfig;
    private ForgeConfigSpec.IntValue
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
    private ForgeConfigSpec.BooleanValue isFlyEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> flyAlias;
    private ForgeConfigSpec.ConfigValue<? extends String> datePattern;

    // scessential getRegistryName mob
    private ForgeConfigSpec.IntValue entitiesWithinRadius;

    // invsee
    private ForgeConfigSpec.BooleanValue isOpenInvEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> invseeAlias;

    // hat
    private ForgeConfigSpec.BooleanValue isHatEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> hatAlias;

    // trashcan
    private ForgeConfigSpec.BooleanValue isTrashcanEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> trashcanAlias;
    private ForgeConfigSpec.IntValue cleanTrashcanIntervalSeconds;

    // Rank
    private ForgeConfigSpec.BooleanValue isRankEnable;
    private ForgeConfigSpec.ConfigValue<? extends String> rankAlias;

    public ConfigCommands(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        this.builder.push("Commands");

        this.builder.push("Spawn");
        isSpawnEnableConfig = this.builder
                .comment("Set it to false to disable /spawn command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsSpawnEnable", true);
        spawnAlias = this.builder
                .comment("How to trigger command spawn. If you set it to \"sp\", you will need to use /sp to back to the spawn point.",
                        "Default value: spawn",
                        "Do not add \"/\"!")
                .define("SpawnAlias", "spawn", ConfigCommands::isValidCommandAlias);
        spawnCooldownSecondsConfig = this.builder
                .comment("The time interval between two /spawn commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("SpawnCooldown", 3, 0, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Back");
        isBackEnableConfig = this.builder
                .comment("Set it to false to disable /back command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsBackEnable", true);
        backAlias = this.builder
                .comment("How to trigger command back. If you set it to \"bk\", you will need to use /bk to back.",
                        "Default value: back",
                        "Do not add \"/\"!")
                .define("BackAlias", "back", ConfigCommands::isValidCommandAlias);
        backCooldownSecondsConfig = this.builder
                .comment("The time interval between two /back commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("BackCooldown", 3, 0, Integer.MAX_VALUE);
        maxBacksConfig = this.builder
                .comment("Max amount of times of /back can use to go back to certain locations. \nDefault value: 10 Times")
                .defineInRange("MaxBacks", 10, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Home");
        isHomeEnableConfig = this.builder
                .comment("Set it to false to disable /home, /sethome, /delhome, /listhomes, /homeother, and /listotherhomes command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsHomeEnable", true);
        homeAlias = this.builder
                .comment("How to trigger command home.",
                        "Default value: home",
                        "Do not add \"/\"!")
                .define("HomeAlias", "home", ConfigCommands::isValidCommandAlias);
        setHomeAlias = this.builder
                .comment("How to trigger command to set a home.",
                        "Default value: sethome",
                        "Do not add \"/\"!")
                .define("SetHomeAlias", "sethome", ConfigCommands::isValidCommandAlias);
        delHomeAlias = this.builder
                .comment("How to trigger command to delete a home.",
                        "Default value: delhome",
                        "Do not add \"/\"!")
                .define("DelHomeAlias", "delhome", ConfigCommands::isValidCommandAlias);
        listHomesAlias = this.builder
                .comment("How to trigger command to list all your homes.",
                        "Default value: listhomes",
                        "Do not add \"/\"!")
                .define("ListHomesAlias", "listhomes", ConfigCommands::isValidCommandAlias);
        homeOtherAlias = this.builder
                .comment("How to trigger command to teleport to other's home.",
                        "Default value: homeother",
                        "Do not add \"/\"!")
                .define("HomeOtherAlias", "homeother", ConfigCommands::isValidCommandAlias);
        delOtherHomeAlias = this.builder
                .comment("How to trigger command to delete other's home.",
                        "Default value: delotherhome",
                        "Do not add \"/\"!")
                .define("DelOtherHomeAlias", "delotherhome", ConfigCommands::isValidCommandAlias);
        listOtherHomesAlias = this.builder
                .comment("How to trigger command to list all someone's homes.",
                        "Default value: listotherhomes",
                        "Do not add \"/\"!")
                .define("ListOtherHomesAlias", "listotherhomes", ConfigCommands::isValidCommandAlias);
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
                .comment("Set it to false to disable /warp, /setwarp, /delwarp, and /listwarps command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsWarpEnable", true);
        warpAlias = this.builder
                .comment("How to trigger command to teleport to a warp.",
                        "Default value: warp",
                        "Do not add \"/\"!")
                .define("WarpAlias", "warp", ConfigCommands::isValidCommandAlias);
        setWarpAlias = this.builder
                .comment("How to trigger command to set a warp.",
                        "Default value: setwarp",
                        "Do not add \"/\"!")
                .define("SetWarpAlias", "setwarp", ConfigCommands::isValidCommandAlias);
        delWarpAlias = this.builder
                .comment("How to trigger command to delete a warp.",
                        "Default value: delwarp",
                        "Do not add \"/\"!")
                .define("DelWarpAlias", "delwarp", ConfigCommands::isValidCommandAlias);
        listWarpsAlias = this.builder
                .comment("How to trigger command to list all warps.",
                        "Default value: listwarps",
                        "Do not add \"/\"!")
                .define("ListWarpsAlias", "listwarps", ConfigCommands::isValidCommandAlias);
        warpCooldownSecondsConfig = this.builder
                .comment("The time interval between two /warp commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("WarpCooldown", 3, 0, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("TPA");
        isTPAEnableConfig = this.builder
                .comment("Set it to false to disable /tpa, /tpahere, /tpaaccept, /tpadeny, /tphere, and /tpallhere command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsTPAEnable", true);
        tpaAlias = this.builder
                .comment("How to trigger command to tpa.",
                        "Default value: tpa",
                        "Do not add \"/\"!")
                .define("TPAAlias", "tpa", ConfigCommands::isValidCommandAlias);
        tpaHereAlias = this.builder
                .comment("How to trigger command to tpahere.",
                        "Default value: tpahere",
                        "Do not add \"/\"!")
                .define("TPAHereAlias", "tpahere", ConfigCommands::isValidCommandAlias);
        tpHereAlias = this.builder
                .comment("How to trigger command to teleport player to your position.",
                        "Default value: tphere",
                        "Do not add \"/\"!")
                .define("TPHereAlias", "tphere", ConfigCommands::isValidCommandAlias);
        tpAllHereAlias = this.builder
                .comment("How to trigger command to teleport all players to your position.",
                        "Default value: tpallhere",
                        "Do not add \"/\"!")
                .define("TPAllHereAlias", "tpallhere", ConfigCommands::isValidCommandAlias);
        tpaCooldownSecondsConfig = this.builder
                .comment("The time interval between two /tpa and /tpahere commands, or teleport cooldown, in seconds.\nDefault value: 3 seconds")
                .defineInRange("TPACooldown", 3, 0, Integer.MAX_VALUE);
        maxTPARequestTimeoutSecondsConfig = this.builder
                .comment("If a tpa request last more than this seconds, that tpa request will be considered expired. \nDefault value: 60 seconds")
                .defineInRange("MaxTPARequestExpireTime", 60, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("RTP");
        isRTPEnableConfig = this.builder
                .comment("Set it to false to disable /rtp command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsRTPEnable", true);
        rtpAlias = this.builder
                .comment("How to trigger command to randomly teleport to a safe location within the world.",
                        "Default value: rtp",
                        "Do not add \"/\"!")
                .define("RTPAlias", "rtp", ConfigCommands::isValidCommandAlias);
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
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsFlyEnable", true);
        flyAlias = this.builder
                .comment("How to trigger command to let a player flyable.",
                        "Default value: fly",
                        "Do not add \"/\"!")
                .define("FlyAlias", "fly", ConfigCommands::isValidCommandAlias);
        datePattern = this.builder
                .comment("The date format used to display the deadline of flying.",
                        "A valid date format should follow the pattern described in JavaDoc: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html",
                        "If you don't know what it is, please do not modify it.",
                        "Default value: \"hh:mm:ss MM/dd/yyyy\"")
                .define("DatePattern", "hh:mm:ss MM/dd/yyyy");
        this.builder.pop();

        this.builder.push("Hat");
        isHatEnable = this.builder
                .comment("Set it to false to disable /hat command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("IsHatEnable", true);
        hatAlias = this.builder
                .comment("How to trigger command hat.",
                        "Default value: hat",
                        "Do not add \"/\"!")
                .define("HatAlias", "hat", ConfigCommands::isValidCommandAlias);
        this.builder.pop();

        this.builder.push("Trashcan");
        isTrashcanEnable = this.builder
                .comment("Set it to false to disable /trashcan command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("isTrashcanEnable", true);
        trashcanAlias = this.builder
                .comment("How to trigger command to open trashcan.",
                        "Default value: trashcan",
                        "Do not add \"/\"!")
                .define("TrashcanAlias", "trashcan", ConfigCommands::isValidCommandAlias);
        cleanTrashcanIntervalSeconds = this.builder
                .comment("The interval between two actions of deleting items in the trashcan.",
                        "There is also a button available in the trashcan gui to clear the items.",
                        "Default value: 60 seconds.")
                .defineInRange("CleanTrashInterval", 60, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.builder.push("Invsee");
        isOpenInvEnable = this.builder
                .comment("Set it to false to disable /invsee (/openinv maybe in the future) command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("isInvseeEnable", true);
        invseeAlias = this.builder
                .comment("How to trigger command to open someone's inventory.",
                        "Default value: invsee",
                        "Do not add \"/\"!")
                .define("InvseeAlias", "invsee", ConfigCommands::isValidCommandAlias);
        this.builder.pop();

        this.builder.push("Rank");
        isRankEnable = this.builder
                .comment("Set it to false to disable /rank command.",
                        "Default value: true",
                        "This option only work after server restarted or typed /reload command")
                .define("isRankEnable", true);
        rankAlias = this.builder
                .comment("How to trigger command to open rank gui.",
                        "Default value: rank",
                        "Do not add \"/\"!")
                .define("RankAlias", "rank", ConfigCommands::isValidCommandAlias);
        this.builder.pop();

        // /scessential ....
        this.builder.push("Scessential");
        this.builder.push("GetRegistryName");
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
        CommandSpawn.spawnAlias = spawnAlias.get();
        CommandSpawn.spawnCooldownSeconds = spawnCooldownSecondsConfig.get();

        // Back
        CommandBack.isBackEnable = isBackEnableConfig.get();
        CommandBack.backAlias = backAlias.get();
        CommandBack.backCooldownSeconds = backCooldownSecondsConfig.get();
        CommandBack.maxBacks = maxBacksConfig.get();

        // Home
        CommandHome.isHomeEnable = isHomeEnableConfig.get();
        CommandHome.setHomeAlias = setHomeAlias.get();
        CommandHome.homeAlias = homeAlias.get();
        CommandHome.homeOtherAlias = homeOtherAlias.get();
        CommandHome.delHomeAlias = delHomeAlias.get();
        CommandHome.listHomesAlias = listHomesAlias.get();
        CommandHome.listOtherHomesAlias = listOtherHomesAlias.get();
        CommandHome.delOtherHomeAlias = delOtherHomeAlias.get();
        CommandHome.homeCooldownSeconds = homeCooldownSecondsConfig.get();
        CommandHome.homeOtherCooldownSeconds = homeOtherCooldownSecondsConfig.get();
        CommandHome.maxHomes = maxHomesConfig.get();

        // TPA
        CommandTPA.isTPAEnable = isTPAEnableConfig.get();
        CommandTPA.tpaAlias = tpaAlias.get();
        CommandTPA.tpaHereAlias = tpaHereAlias.get();
        CommandTPA.tpHereAlias = tpHereAlias.get();
        CommandTPA.tpAllHereAlias = tpAllHereAlias.get();
        CommandTPA.tpaCooldownSeconds = tpaCooldownSecondsConfig.get();
        CommandTPA.maxTPARequestTimeoutSeconds = maxTPARequestTimeoutSecondsConfig.get();

        // Warp
        CommandWarp.isWarpEnable = isWarpEnableConfig.get();
        CommandWarp.setWarpAlias = setWarpAlias.get();
        CommandWarp.warpAlias = warpAlias.get();
        CommandWarp.listWarpsAlias = listWarpsAlias.get();
        CommandWarp.delWarpAlias = delWarpAlias.get();
        CommandWarp.warpCooldownSeconds = warpCooldownSecondsConfig.get();

        // RTP
        CommandRTP.isRTPEnable = isRTPEnableConfig.get();
        CommandRTP.rtpAlias = rtpAlias.get();
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
        CommandFly.isFlyEnable = isFlyEnable.get();
        CommandFly.flyAlias = flyAlias.get();
        CommandFly.datePattern = datePattern.get();

        // scessential getRegistryName mob
        CommandGetRegistryName.entitiesWithinRadius = entitiesWithinRadius.get();

        // invsee
        CommandOpenInv.isOpenInvEnable = isOpenInvEnable.get();
        CommandOpenInv.invseeAlias = invseeAlias.get();

        // Hat
        CommandHat.isHatEnabel = isHatEnable.get();
        CommandHat.hatAlias = hatAlias.get();

        // Trashcan
        CommandTrashcan.isTrashcanEnable = isTrashcanEnable.get();
        CommandTrashcan.trashcanAlias = trashcanAlias.get();
        CommandTrashcan.cleanTrashcanIntervalSeconds = cleanTrashcanIntervalSeconds.get();

        // Rank
        CommandRank.isRankEnable = isRankEnable.get();
        CommandRank.rankAlias = rankAlias.get();
    }

    private static boolean isValidCommandAlias(Object o) {
        if (o instanceof String) {
            return !o.toString().contains("/") && !o.toString().contains(" ");
        }
        return false;
    }

}