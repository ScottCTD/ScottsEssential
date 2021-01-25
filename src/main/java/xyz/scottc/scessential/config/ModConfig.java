package xyz.scottc.scessential.config;

import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SERVER_CONFIG;

    private static final Set<? extends AbstractModConfig> CONFIGS;

    static {
        CONFIGS = init();
        SERVER_CONFIG = SERVER_BUILDER.build();
        get(CONFIGS);
    }

    public static Set<? extends AbstractModConfig> init() {
        Set<? extends AbstractModConfig> configs = Sets.newHashSet(
                new ConfigCommands(SERVER_BUILDER),
                new ConfigEntityCleaner(SERVER_BUILDER),
                new ConfigInfoRecorder(SERVER_BUILDER),
                new ConfigMotd(SERVER_BUILDER)
        );
        configs.forEach(AbstractModConfig::init);
        return configs;
    }

    public static void get(Set<? extends AbstractModConfig> configs) {
        configs.forEach(AbstractModConfig::get);
    }

    @SubscribeEvent
    public static void onLoading(net.minecraftforge.fml.config.ModConfig.Loading event) {
        get(CONFIGS);
        Main.LOGGER.info("SCE Config loaded!");
    }

    @SubscribeEvent
    public static void onReloading(net.minecraftforge.fml.config.ModConfig.Reloading event) {
        get(CONFIGS);
        Main.LOGGER.info("SCE Config Reloaded!");
    }

    public static boolean isResourceLocationList(Object o) {
        if (!(o instanceof List)) {
            return false;
        }
        List<?> list = (List<?>) o;
        for (Object s : list) {
            if (!s.toString().contains(":")) {
                return false;
            }
        }
        return true;
    }
}
