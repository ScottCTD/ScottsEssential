package xyz.scottc.scessential.config;

import net.minecraftforge.common.ForgeConfigSpec;

public abstract class AbstractModConfig {

    protected ForgeConfigSpec.Builder builder;

    public AbstractModConfig(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
    }

    /**
     * Init the config file
     */
    abstract void init();

    /**
     * Get the @ConfigField values from config to mod
     */
    public abstract void get();

}
