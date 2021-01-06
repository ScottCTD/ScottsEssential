package xyz.scottc.scessential.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.network.Network;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEventHandler {

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        Network.register();
    }

}
