package xyz.scottc.scessential.events.motd;

import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    private static int counter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (counter >= 20) {
            counter = 0;
            ServerStatusResponse response = Main.SERVER.getServerStatusResponse();
            // Max 2 lines
            response.setServerDescription(new StringTextComponent("1\n2\n3\n4\n5"));
            response.setFavicon("1");
        }
        counter++;
    }

}
