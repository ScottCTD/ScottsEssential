package xyz.scottc.scessential.config;

import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SERVER_CONFIG;

    private static final Set<AbstractModConfig> CONFIGS;

    static {
        CONFIGS = init();
        SERVER_CONFIG = SERVER_BUILDER.build();
        get(CONFIGS);
    }

    public static Set<AbstractModConfig> init() {
        Set<AbstractModConfig> configs = Sets.newHashSet(new ConfigCommands(SERVER_BUILDER), new ConfigEntityCleaner(SERVER_BUILDER));
        configs.forEach(AbstractModConfig::init);
        return configs;
    }

    public static void get(Set<AbstractModConfig> configs) {
        configs.forEach(AbstractModConfig::get);
    }


    @SubscribeEvent
    public static void onLoading(net.minecraftforge.fml.config.ModConfig.Loading event) {
        get(CONFIGS);
    }

    @SubscribeEvent
    public static void onReloading(net.minecraftforge.fml.config.ModConfig.Reloading event) {
        get(CONFIGS);
    }
}