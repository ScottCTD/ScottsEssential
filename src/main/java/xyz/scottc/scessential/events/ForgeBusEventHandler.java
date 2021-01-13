package xyz.scottc.scessential.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.api.ISCEPlayerData;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.commands.teleport.CommandTPA;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.core.TPARequest;
import xyz.scottc.scessential.core.TeleportPos;
import xyz.scottc.scessential.utils.TextUtils;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
                new Thread(() -> {
                    long now = System.currentTimeMillis();
                    // TPA Request
                    TPARequest.getTpaRequest().values().forEach(request -> {
                        if ((request.getCreateTime() + CommandTPA.maxTPARequestTimeoutSeconds * 1000L) <= now) {
                            TPARequest.getTpaRequest().remove(request.getId());
                            request.getSource().sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                                    TextUtils.getTranslationKey("message", "requesttimeout"), request.getTarget().getGameProfile().getName()), false
                            );
                        }
                    });

                    // Player Fly Time
                    SCEPlayerData.PLAYER_DATA_LIST.stream().filter(player -> player.getPlayer() != null &&
                            player.isFlyable() &&
                            player.getCanFlyUntil() != -1 &&
                            player.getCanFlyUntil() <= now)
                            .forEach(player -> {
                                player.setFlyable(false);
                                player.setCanFlyUntil(-1);
                                player.getPlayer().sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                                        TextUtils.getTranslationKey("message", "cantflynow")), false);

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

    /**
     * Listen this event for adding a new back history for a player when that player died, allowing player use /back
     * to return to the death pos.
     * @param event Player Death event
     */
    @SubscribeEvent
    public static void onPlayerDied(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.world.isRemote) {
            if (entity instanceof PlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                ISCEPlayerData data = SCEPlayerData.getInstance(player);
                data.addTeleportHistory(new TeleportPos(player));
            }
        }
    }

    /**
     * Fix the bug that flyable player not fly after logged in.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ISCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
        data.setFlyable(data.isFlyable());
    }

    /**
     * Let flyable player flyable again after respawn.
     * @param e PlayerEvent.PlayerRespawnEvent
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        ISCEPlayerData data = SCEPlayerData.getInstance(e.getPlayer());
        data.setFlyable(data.isFlyable());
    }

    @SubscribeEvent
    public static void onPlayerChangeGamemode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getCurrentGameMode().isCreative() && event.getNewGameMode().isSurvivalOrAdventure()) {
            new Thread(() -> {
                // after gamemode changed, if player is flyable, then flyable
                ISCEPlayerData data = SCEPlayerData.getInstance(event.getPlayer());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                data.setFlyable(data.isFlyable());
            }).start();
        }
    }

}
