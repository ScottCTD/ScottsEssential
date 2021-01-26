package xyz.scottc.scessential.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.events.Shutdowner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Scott_CTD
 * @create 2021/1/26 8:47
 */
@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigShutdowner extends AbstractModConfig {

    private ForgeConfigSpec.BooleanValue isShutdownerEnable;
    private ForgeConfigSpec.BooleanValue shutdownMode;
    private ForgeConfigSpec.ConfigValue<List<? extends String>> rawRealTimes;
    private ForgeConfigSpec.IntValue minutesInterval;
    private ForgeConfigSpec.ConfigValue<List<List<? extends String>>> rawNotifications;

    public ConfigShutdowner(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        this.builder
                .comment("Server auto shutdown configurations.",
                        "I could just shutdown the server, and you should make a script or something else to restart your server after I shut it down.",
                        "For a simple script, please check my mod description: https://github.com/ScottCTD/ScottsEssential/blob/Dev/README.md")
                .push("AutoShutdown");

        this.isShutdownerEnable = this.builder
                .comment("Set it to true to enable automatic shutdown.",
                        "Default value: false (Not automatically shutdown)")
                .define("IsShutdownerEnable", false);

        this.shutdownMode = this.builder
                .comment("True to enable shutdown server by realtime (00:00, 12:00 for example)",
                        "False to enable shutdown server by time intervals (300 Minutes for example)",
                        "Default value: true (By realtime)")
                .define("ShutdownMode", true);

        this.rawRealTimes = this.builder
                .comment("The realtime strings, and you should follow the format of \"HH:mm\"",
                        "For example, the server will be shutdown at everyday 12:00 and 18:00 if you set it to [\"12:00\", \"18:00\"]",
                        "Default value: []")
                .define("RealTimes", Collections.emptyList(), ConfigShutdowner::isRealTimes);

        this.minutesInterval = this.builder
                .comment("The interval in minutes between two shutdowns.",
                        "Default value: 300 Minutes (5 Hours)")
                .defineInRange("MinutesInterval", 300, 1, Integer.MAX_VALUE);

        this.rawNotifications = this.builder
                .comment("The notifications before next shutdown happens.",
                        "Format: [[\"Shutdown countdown seconds\", \"Message String\"], [...], ...]",
                        "Default value: [[\"5\", \"&6Server will restart in &b&l5 &6seconds!\"]] (The message will be sent 5 seconds before auto stop.)",
                        "You could add as many as you want.")
                .define("Notifications", Collections.singletonList(Arrays.asList("5", "Server will restart after 5 seconds!")), ConfigShutdowner::isNotifications);

        this.builder.pop();
    }

    @Override
    public void get() {
        Shutdowner.isShutdownerEnable = this.isShutdownerEnable.get();
        Shutdowner.mode = this.shutdownMode.get();
        Shutdowner.rawRealTimes = this.rawRealTimes.get();
        Shutdowner.minutesInterval = this.minutesInterval.get();
        Shutdowner.rawNotifications = this.rawNotifications.get();

        Shutdowner.init();
    }

    private static boolean isRealTimes(Object o) {
        if (o instanceof List) {
            List<?> list = (List<?>) o;
            if (list.size() == 0) return true;
            for (Object element : list) {
                return element.toString().contains(":");
            }
        }
        return false;
    }

    private static boolean isNotifications(Object o) {
        if (o instanceof List) {
            List<?> list = (List<?>) o;
            if (list.size() == 0) return true;
            for (Object element : list) {
                if (element instanceof List) {
                    List<?> subList = (List<?>) element;
                    if (subList.size() < 2) return false;
                    try {
                        int test = Integer.parseInt(subList.get(0).toString());
                        if (test < 0) return false;
                    } catch (NumberFormatException e) {
                        Main.LOGGER.error("The first element of each notification must be number (E.g. 100)!");
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
