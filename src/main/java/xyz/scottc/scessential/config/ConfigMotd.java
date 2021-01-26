package xyz.scottc.scessential.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.events.MOTDCustomizer;

import java.util.Arrays;
import java.util.List;

public class ConfigMotd extends AbstractModConfig {

    private ForgeConfigSpec.ConfigValue<List<List<? extends String>>> raws;
    private ForgeConfigSpec.BooleanValue isCustomizedMOTDEnable;

    public ConfigMotd(ForgeConfigSpec.Builder builder) {
        super(builder);
    }

    @Override
    public void init() {
        this.builder.push("MOTD");
        this.isCustomizedMOTDEnable = this.builder
                .comment("Set it to true to enable customized server motd (server description)",
                        "Default value: false")
                .define("IsCustomizedMOTDEnable", false);
        this.raws = this.builder
                .comment("The description lines of motd, with max two lines.",
                        "Every [\"\", \"\"] inside the outermost is a motd that players will see.",
                        "You could add many [\"\", \"\"] to dynamically change the motd.",
                        "You could also use '&' or '§' to specify the format of each line of description.",
                        "For more information about formatting, please check google or minecraft wiki.",
                        "Default value: [[\"&a&lFirst line &fof &b&lMOTD&f!\", \"&kSecond!\"], [\"Thanks for using &d&lScott's Essential&f!\", \"&6&lWuuuhoooooo\"]]")
                .define("Descriptions", Arrays.asList(Arrays.asList("&a&lFirst line &fof &b&lMOTD&f!", "&kSecond!"),
                                             Arrays.asList("Thanks for using &d&lScott's Essential&f!", "&6&lWuuuhoooooo")),
                        ConfigMotd::isValidMOTD);
        this.builder.pop();
    }

    @Override
    public void get() {
        MOTDCustomizer.raws = this.raws.get();
        MOTDCustomizer.isCustomizedMOTDEnable = this.isCustomizedMOTDEnable.get();
        if (MOTDCustomizer.isCustomizedMOTDEnable) {
            MOTDCustomizer.init();
            Main.LOGGER.info("MOTD Reloaded!");
        }
    }

    private static boolean isValidMOTD(Object o) {
        if (o instanceof List) {
            List<?> list = (List<?>) o;
            if (list.size() > 0) {
                return list.get(0) instanceof List;
            } else {
                return true;
            }
        }
        return false;
    }

}
