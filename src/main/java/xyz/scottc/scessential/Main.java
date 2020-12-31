package xyz.scottc.scessential;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "scessential";
    public static final Logger LOGGER = LogManager.getLogger();

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
