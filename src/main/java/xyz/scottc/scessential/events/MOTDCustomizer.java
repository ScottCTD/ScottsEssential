package xyz.scottc.scessential.events;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.ColorfulStringParser;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MOTDCustomizer {

    @ConfigField
    public static List<List<? extends String>> raws;
    @ConfigField
    public static boolean isCustomizedMOTDEnable = false;

    private static final List<TextComponent> TEXTS = new ArrayList<>();
    public static int counter = 0;
    private static int index = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (isCustomizedMOTDEnable) {
            if (counter >= 20) {
                counter = 0;
                ServerStatus response = Main.SERVER.getStatus();
                try {
                    response.setDescription(TEXTS.get(index));
                } catch (IndexOutOfBoundsException ignore) {}
                index = index == TEXTS.size() - 1 ? 0 : index + 1;
            }
            counter++;
        }
    }

    public static void init() {
        TEXTS.clear();
        raws.forEach(raw -> TEXTS.add(new ColorfulStringParser(raw).getText()));
        counter = 0;
    }

    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        init();
    }

}
