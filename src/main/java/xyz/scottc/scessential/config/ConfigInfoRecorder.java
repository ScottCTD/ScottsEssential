package xyz.scottc.scessential.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xyz.scottc.scessential.events.inforecorder.CommonInfoStorage;
import xyz.scottc.scessential.events.inforecorder.EventHandler;

import java.util.Collections;
import java.util.List;

public class ConfigInfoRecorder extends AbstractModConfig {

    private ForgeConfigSpec.ConfigValue<? extends String> timePattern;
    private ForgeConfigSpec.BooleanValue isRecordPlayerUUID;
    private ForgeConfigSpec.BooleanValue
            isInfoRecorderEnable,
            isRecordPlayerLogInOut,
            isRecordPlayerChat,
            isRecordPlayerUseCommand,
            isRecordPlayerJoinDimension,
            isRecordPlayerDeath,
            isRecordPlayerKill,
            isRecordPlayerKillAnimal,
            isRecordPlayerKillMonster,
            isRecordPlayerPeriodically,
            isRecordPlayerOpenContainer,
            isRecordPlayerPlaceBlock;
    private ForgeConfigSpec.IntValue recordPlayerIntervalSeconds;
    private ForgeConfigSpec.ConfigValue<List<? extends String>> placeBlockListeningList;


    public ConfigInfoRecorder(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        this.builder.push("InformationRecorder");

        this.isInfoRecorderEnable = this.builder
                .comment("Set it to false to disable the entire information recording system.",
                        "Default value: false")
                .define("IsInfoRecorderEnable", false);
        this.timePattern = this.builder
                .comment("The pattern of time appears in every infomation file. (E.g ChatLog.json)",
                        "A valid date format should follow the pattern described in JavaDoc: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html",
                        "If you don't know what it is, please do not modify it.",
                        "Default value: \"hh:mm:ss MM/dd/yyyy\"")
                .define("TimePattern", "hh:mm:ss MM/dd/yyyy");
        this.isRecordPlayerUUID = this.builder
                .comment("Set it to false to disable recording player UUID.",
                        "Default value: true")
                .define("IsRecordPlayerUUID", true);

        this.builder.push("ModuleSettings");

        this.isRecordPlayerLogInOut = this.builder
                .comment("Set it to false to disable recording player login and logout.",
                        "Default value: true")
                .define("IsRecordPlayerLoginLogoutEnable", true);
        this.isRecordPlayerChat = this.builder
                .comment("Set it to false to disable recording player chat.",
                        "Default value: true")
                .define("IsRecordPlayerChatEnable", true);
        this.isRecordPlayerUseCommand = this.builder
                .comment("Set it to false to disable recording player used commands.",
                        "Default value: true")
                .define("IsRecordCommandsEnable", true);
        this.isRecordPlayerJoinDimension = this.builder
                .comment("Set it to false to disable recording player join other dimension.",
                        "Default value: true")
                .define("IsRecordPlayerJoinDimensionEnable", true);
        this.isRecordPlayerDeath = this.builder
                .comment("Set it to false to disable recording player's death.",
                        "Default value: true")
                .define("IsRecordPlayerDeathEnable", true);

        this.builder.push("EntitiesKilledByPlayerRecorder");
        this.isRecordPlayerKill = this.builder
                .comment("Set it to false to disable recording entities killed by players.",
                        "Default value: true")
                .define("IsRecordEntitiesKilledByPlayerEnable", true);
        this.isRecordPlayerKillAnimal = this.builder
                .comment("Set it to false to disable recording animals killed by plaers.",
                        "Default value: true")
                .define("IsRecordAnimalsKilledByPlayerEnable", true);
        this.isRecordPlayerKillMonster = this.builder
                .comment("Set it to false to disable recording monsters killed by players.",
                        "Default value: true")
                .define("IsRecordMonstersKilledByPlayersEnable", true);
        this.builder.pop();

        this.builder.push("PlayerPeriodicallyRecorder").comment("Settings about record the information of all players periodically with certain interval.");
        this.isRecordPlayerPeriodically = this.builder
                .comment("Set it to false to disable recording players' information periodically.",
                        "Default value: true")
                .define("IsRecordPlayersPerodicallyEnable", true);
        this.recordPlayerIntervalSeconds = this.builder
                .comment("The interval in seconds between two actions of recording players' information.",
                        "Default value: 600 seconds (10 min)")
                .defineInRange("RecordPlayerInterval", 600, 1, Integer.MAX_VALUE);
        this.builder.pop();

        this.isRecordPlayerOpenContainer = this.builder
                .comment("Set it to false to disable recording players open containers.",
                        "Default value: true")
                .define("IsRecordPlayerOpenContainersEnable", true);

        this.builder.push("PlayerPlaceBlockRecorder");
        this.isRecordPlayerPlaceBlock = this.builder
                .comment("Set it to false to disable recording players place blocks.",
                        "Default value: true")
                .define("IsRecordPlayersPlaceBlocksEnable", true);
        this.placeBlockListeningList = this.builder
                .comment("A list of registry names (e.g minecraft:stone) that will be logged when player tries to place them.")
                .define("ListeningList", Collections.singletonList("minecraft:tnt"), ModConfig::isResourceLocationList);
        this.builder.pop();

        this.builder.pop();
        this.builder.pop();
    }

    @Override
    public void get() {
        CommonInfoStorage.datePattern = this.timePattern.get();
        CommonInfoStorage.isRecordPlayerUUID = this.isRecordPlayerUUID.get();
        EventHandler.isInfoRecorderEnable = this.isInfoRecorderEnable.get();

        EventHandler.isRecordPlayerLogInOut = isRecordPlayerLogInOut.get();
        EventHandler.isRecordPlayerChat = isRecordPlayerChat.get();
        EventHandler.isRecordPlayerUseCommand = isRecordPlayerUseCommand.get();
        EventHandler.isRecordPlayerJoinDimension = isRecordPlayerJoinDimension.get();
        EventHandler.isRecordPlayerDeath = isRecordPlayerDeath.get();
        EventHandler.isRecordPlayerKill = isRecordPlayerKill.get();
        EventHandler.isRecordPlayerKillAnimal = isRecordPlayerKillAnimal.get();
        EventHandler.isRecordPlayerKillMonster = isRecordPlayerKillMonster.get();
        EventHandler.isRecordPlayerPeriodically = isRecordPlayerPeriodically.get();
        EventHandler.isRecordPlayerOpenContainer = isRecordPlayerOpenContainer.get();
        EventHandler.isRecordPlayerPlaceBlock = isRecordPlayerPlaceBlock.get();

        EventHandler.recordPlayerIntervalSeconds = this.recordPlayerIntervalSeconds.get();
        EventHandler.placeBlockListeningList = this.placeBlockListeningList.get();

        EventHandler.init();
    }
}
