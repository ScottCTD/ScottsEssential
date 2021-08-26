package xyz.scottc.scessential.events;

import net.minecraft.Util;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.commands.teleport.CommandTPA;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TPARequest;
import xyz.scottc.scessential.utils.TextUtils;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEventHandler {

    private static int counter = 0;

    /**
     * Determine if an tpa request was expired and if a player fly time expired.
     * Also ++played time
     * @param event ServerTickEvent
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (counter >= 20) {
                counter = 0;
                new Thread(() -> {
                    long now = System.currentTimeMillis();
                    // TPA Request
                    for (TPARequest next : TPARequest.getTpaRequest().values()) {
                        if ((next.getCreateTime() + CommandTPA.maxTPARequestTimeoutSeconds * 1000L) <= now) {
                            TPARequest.getTpaRequest().remove(next.getId());
                            next.getSource().sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "requesttimeout"), next.getTarget().getGameProfile().getName()), Util.NIL_UUID
                            );
                        }
                    }

                    // Player Fly Time
                    SCEPlayerData.PLAYER_DATA_LIST.stream().filter(player -> player.getPlayer() != null &&
                            player.isFlyable() &&
                            player.getCanFlyUntil() != -1 &&
                            player.getCanFlyUntil() <= now)
                            .forEach(player -> {
                                player.setFlyable(false);
                                player.setCanFlyUntil(-1);
                                player.getPlayer().sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                                        TextUtils.getTranslationKey("message", "cantflynow")), Util.NIL_UUID);

                            });

                    // Trashcan count down
                    SCEPlayerData.PLAYER_DATA_LIST.forEach(data -> {
                        CommandTrashcan.Trashcan trashcan = data.getTrashcan();
                        if (trashcan != null) {
                            long nextCleanTime = trashcan.getLastCleanLong() + CommandTrashcan.cleanTrashcanIntervalSeconds * 1000L;
                            if (nextCleanTime <= now) {
                                trashcan.clear();
                                trashcan.setNextCleanSeconds(CommandTrashcan.cleanTrashcanIntervalSeconds);
                            } else {
                                trashcan.setNextCleanSeconds((int) (nextCleanTime - now) / 1000);
                            }
                        }
                    });
                }).start();
            }
            counter++;
        }
    }
}
