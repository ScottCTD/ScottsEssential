package xyz.scottc.scessential.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import xyz.scottc.scessential.Main;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SCEDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        if (event.includeClient()) {
            DataGenerator generator = event.getGenerator();
            generator.addProvider(new SCELanguageProvider(generator, Main.MOD_ID, "en_us"));
        }
    }

}
