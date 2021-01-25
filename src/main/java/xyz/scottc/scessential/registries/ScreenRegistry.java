package xyz.scottc.scessential.registries;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.client.screen.ScreenOthersInv;
import xyz.scottc.scessential.client.screen.ScreenTrashcan;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ScreenRegistry {

    @SubscribeEvent
    public static void register(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ContainerTypeRegistry.othersContainerType, ScreenOthersInv::new);
        ScreenManager.registerFactory(ContainerTypeRegistry.trashcanContainerType, ScreenTrashcan::new);
    }

}
