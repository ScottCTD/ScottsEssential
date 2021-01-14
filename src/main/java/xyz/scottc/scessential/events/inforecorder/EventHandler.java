package xyz.scottc.scessential.events.inforecorder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.DateUtils;
import xyz.scottc.scessential.utils.FIleUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    public static final Gson GSON = new GsonBuilder().create();

    @ConfigField
    public static boolean
            isInfoRecorderEnable = true,
            isRecordPlayerLogInOut = true,
            isRecordPlayerChat = true,
            isRecordPlayerUseCommand = true,
            isRecordPlayerJoinDimension = true,
            isRecordPlayerDeath = true,
            isRecordPlayerKill = true,
            isRecordPlayerKillAnimal = true,
            isRecordPlayerKillMonster = true,
            isRecordPlayerPeriodically = true,
            isRecordPlayerOpenContainer = true,
            isRecordPlayerPlaceBlock = true;
    @ConfigField
    public static int recordPlayerIntervalSeconds = 600;
    @ConfigField
    public static List<? extends String> placeBlockListeningList = new ArrayList<>();

    public static File folder;

    public static File loginLogFile;
    public static File chatLogFile;
    public static File commandLogFile;
    public static File playerDimensionLogFile;
    public static File playerDiedLogFile;
    public static File playerKilledLogFile;
    public static File playerPeriodicallyFile;
    public static File playerOpenContainerLogFile;
    public static File playerPlaceBlockLogFile;

    private static long lastRecordTime = 0;
    private static int counter = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        init();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (isInfoRecorderEnable && isRecordPlayerLogInOut) {
            IInfoStorage info = new CommonInfoStorage(event.getPlayer(), "Player logged in.");
            new Thread(() -> writeJson(info.serializeJson(), loginLogFile, true)).start();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (isInfoRecorderEnable && isRecordPlayerLogInOut) {
            IInfoStorage info = new CommonInfoStorage(event.getPlayer(), "Player logged out.");
            new Thread(() -> writeJson(info.serializeJson(), loginLogFile, true)).start();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChat(ServerChatEvent event) {
        if (isInfoRecorderEnable && isRecordPlayerChat) {
            IInfoStorage info = new CommonInfoStorage(event.getPlayer(), event.getMessage());
            new Thread(() -> writeJson(info.serializeJson(), chatLogFile, true)).start();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCommandParse(CommandEvent event) {
        if (isInfoRecorderEnable && isRecordPlayerUseCommand) {
            try {
                String command = event.getParseResults().getReader().getString();
                IInfoStorage info = new CommonInfoStorage(event.getParseResults().getContext().getSource().asPlayer(), command);
                new Thread(() -> writeJson(info.serializeJson(), commandLogFile, true)).start();
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerJoinOtherDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (isInfoRecorderEnable && isRecordPlayerJoinDimension) {
            IInfoStorage info = new CommonInfoStorage(event.getPlayer(), "Player travel from " + event.getFrom().getLocation().toString() +
                    " to " + event.getTo().getLocation().toString());
            new Thread(() -> writeJson(info.serializeJson(), playerDimensionLogFile, true)).start();

        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDied(LivingDeathEvent event) {
        if (isInfoRecorderEnable) {
            LivingEntity entityLiving = event.getEntityLiving();
            Entity source = event.getSource().getTrueSource();
            if (isRecordPlayerDeath && entityLiving instanceof PlayerEntity) {
                String cause = event.getSource().damageType;
                if (source instanceof PlayerEntity) {
                    cause += " " + ((PlayerEntity) source).getGameProfile().getName();
                }
                IInfoStorage info = new CommonInfoStorage((PlayerEntity) entityLiving, "Died because of " + cause);
                new Thread(() -> writeJson(info.serializeJson(), playerDiedLogFile, true)).start();
            } else if (isRecordPlayerKill && source instanceof PlayerEntity) {
                if (!isRecordPlayerKillMonster && entityLiving instanceof MonsterEntity) {
                    return;
                } else if (!isRecordPlayerKillAnimal && !(entityLiving instanceof MonsterEntity)) {
                    return;
                }
                PlayerEntity trueSource = (PlayerEntity) source;
                IInfoStorage info = new CommonInfoStorage(trueSource, "Player killed " + EntityType.getKey(entityLiving.getType()).toString());
                new Thread(() -> writeJson(info.serializeJson(), playerKilledLogFile, true)).start();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (isInfoRecorderEnable && isRecordPlayerPeriodically) {
            if (counter < 2 * 20) {
                counter = 0;
                init();
                if (event.phase.equals(TickEvent.Phase.END)) {
                    long now = System.currentTimeMillis();
                    if (lastRecordTime + recordPlayerIntervalSeconds * 1000L <= now) {
                        lastRecordTime = now;
                        new Thread(() -> {
                            if (Main.SERVER == null) return;
                            Main.SERVER.getPlayerList().getPlayers().forEach(player -> {
                                IInfoStorage info = new CommonInfoStorage(player, "");
                                writeJson(info.serializeJson(), playerPeriodicallyFile, true);
                            });
                        }).start();
                    }
                }
            }
            counter++;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        if (isInfoRecorderEnable && isRecordPlayerOpenContainer) {
            Optional.ofNullable(event.getContainer().getType().getRegistryName()).ifPresent(location -> {
                String s = location.toString();
                IInfoStorage info = new CommonInfoStorage(event.getPlayer(), s);
                new Thread(() -> writeJson(info.serializeJson(), playerOpenContainerLogFile, true)).start();
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (isInfoRecorderEnable && isRecordPlayerPlaceBlock) {
            Entity entity = event.getEntity();
            if (entity instanceof PlayerEntity) {
                BlockState placedBlock = event.getPlacedBlock();
                Optional.ofNullable(placedBlock.getBlock().getRegistryName()).ifPresent(location -> new Thread(() -> {
                    String s = location.toString();
                    if (placeBlockListeningList.contains(s)) {
                        IInfoStorage info = new CommonInfoStorage((PlayerEntity) entity, s);
                        writeJson(info.serializeJson(), playerPlaceBlockLogFile, true);
                    }
                }).start());
            }
        }
    }

    private static void init() {
        String date = DateUtils.toString(System.currentTimeMillis(), "MMddyyyy");
        folder = new File(Main.INFO_STORAGE_FOLDER.getAbsolutePath() + "/" + date);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                try {
                    throw new IOException("Failed to create specified info folder!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (folder.exists()) {
            if (loginLogFile == null) loginLogFile = FIleUtils.createSubFile("logInAndOut.json", folder);
            if (chatLogFile == null) chatLogFile = FIleUtils.createSubFile("ChatLog.json", folder);
            if (commandLogFile == null) commandLogFile = FIleUtils.createSubFile("CommandLog.json", folder);
            if (playerDimensionLogFile == null) playerDimensionLogFile = FIleUtils.createSubFile("PlayerChangeDimensionLog.json", folder);
            if (playerDiedLogFile == null) playerDiedLogFile = FIleUtils.createSubFile("PlayerDiedLog.json", folder);
            if (playerKilledLogFile == null) playerKilledLogFile = FIleUtils.createSubFile("EntitiesKilledByPlayerLog.json", folder);
            if (playerOpenContainerLogFile == null) playerOpenContainerLogFile = FIleUtils.createSubFile("PlayerOpenContainerLog.json", folder);
            if (playerPlaceBlockLogFile == null) playerPlaceBlockLogFile = FIleUtils.createSubFile("PlayerPlaceBlockLog.json", folder);
            if (playerPeriodicallyFile == null) playerPeriodicallyFile = FIleUtils.createSubFile("PlayerPeriodicallyLog.json", folder);
        }

    }

    public static void writeJson(JsonElement json, File file, boolean append) {
        String jsonString = GSON.toJson(json) + "\n";
        try (FileOutputStream outputStream = new FileOutputStream(file, append)) {
            outputStream.write(jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
