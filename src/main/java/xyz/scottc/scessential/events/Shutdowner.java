package xyz.scottc.scessential.events;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.ColorfulStringParser;
import xyz.scottc.scessential.utils.DateUtils;

import java.text.ParseException;
import java.util.*;

/**
 * @author Scott_CTD
 * @create 2021/1/25 22:41
 */
@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Shutdowner {

    @ConfigField
    public static boolean isShutdownerEnable = false;
    // true -> Use real time false -> Use time interval
    @ConfigField
    public static boolean mode = true;
    @ConfigField
    public static List<? extends String> rawRealTimes = new ArrayList<>();
    @ConfigField
    public static int minutesInterval = 60;
    @ConfigField
    public static List<List<? extends String>> rawNotifications = new ArrayList<>();

    private static final List<Calendar> realTimes = new ArrayList<>();
    public static long startTime = 0;

    // Countdown in seconds and the corresponding messages
    private static final Map<Integer, IFormattableTextComponent> notifications = new TreeMap<>((a, b) -> b - a);
    private static int counter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (counter >= 20) {
                counter = 0;
                Calendar now = Calendar.getInstance();
                if (mode) {
                    // Real time
                    for (Calendar time : realTimes) {
                        if (now.after(time)) {
                            Main.LOGGER.info("Stopping Server!");
                            Main.SERVER.initiateShutdown(false);
                            break;
                        } else {
                            long diffSeconds = (time.getTimeInMillis() - now.getTimeInMillis()) / 1000;
                            for (Map.Entry<Integer, IFormattableTextComponent> notification : notifications.entrySet()) {
                                if (diffSeconds == notification.getKey()) {
                                    Main.sendMessageToAllPlayers(notification.getValue(), false);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    // Interval Shutdown
                    long nowLong = now.getTimeInMillis();
                    long shutdownTime = startTime + minutesInterval * 60 * 1000L;
                    if (nowLong >= shutdownTime) {
                        Main.LOGGER.info("Stopping Server!");
                        Main.SERVER.initiateShutdown(false);
                    } else {
                        long diff = (shutdownTime - nowLong) / 1000;
                        for (Map.Entry<Integer, IFormattableTextComponent> notification : notifications.entrySet()) {
                            if (diff == notification.getKey()) {
                                Main.sendMessageToAllPlayers(notification.getValue(), false);
                                break;
                            }
                        }
                    }
                }
            }
            counter++;
        }
    }

    public static void init() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar setter = Calendar.getInstance();
        rawRealTimes.forEach(raw -> {
            try {
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                setter.setTimeInMillis(DateUtils.getTime(raw, "HH:mm"));
                today.add(Calendar.HOUR_OF_DAY, setter.get(Calendar.HOUR_OF_DAY));
                today.add(Calendar.MINUTE, setter.get(Calendar.MINUTE));
                if (today.after(Calendar.getInstance())) {
                    realTimes.add(today);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Main.LOGGER.error("Your configs of real times to close server are incorrect!");
            }
        });
        rawNotifications.forEach(list -> notifications.put(Integer.valueOf(list.get(0)), new ColorfulStringParser(list.get(1)).getText()));
    }

    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        startTime = System.currentTimeMillis();
    }

}
