package xyz.scottc.scessential.events.inforecorder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.utils.DateUtils;
import xyz.scottc.scessential.utils.FIleUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    public static final Gson GSON = new GsonBuilder().create();

    public static File folder;

    public static File loginLogFile;
    public static File chatLogFile;
    public static File commandLogFile;
    public static File playerDimensionLogFile;
    public static File playerDiedLogFile;
    public static File playerKilledLogFile;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        init();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        CommonInfoStorage info = new CommonInfoStorage(event.getPlayer(), "Player logged in.");
        new Thread(() -> writeJson(info.serializeJson(), loginLogFile, true)).start();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        CommonInfoStorage info = new CommonInfoStorage(event.getPlayer(), "Player logged out.");
        new Thread(() -> writeJson(info.serializeJson(), loginLogFile, true)).start();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChat(ServerChatEvent event) {
        CommonInfoStorage info = new CommonInfoStorage(event.getPlayer(), event.getMessage());
        new Thread(() -> writeJson(info.serializeJson(), chatLogFile, true)).start();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCommandParse(CommandEvent event) {
        try {
            String command = event.getParseResults().getReader().getString();
            CommonInfoStorage info = new CommonInfoStorage(event.getParseResults().getContext().getSource().asPlayer(), command);
            new Thread(() -> writeJson(info.serializeJson(), commandLogFile, true)).start();
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerJoinOtherDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        CommonInfoStorage info = new CommonInfoStorage(event.getPlayer(), "Player travel from " + event.getFrom().getLocation().toString() +
                " to " + event.getTo().getLocation().toString());
        new Thread(() -> writeJson(info.serializeJson(), playerDimensionLogFile, true)).start();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDied(LivingDeathEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();
        Entity source = event.getSource().getTrueSource();
        if (entityLiving instanceof PlayerEntity) {
            String cause = event.getSource().damageType;
            if (source instanceof PlayerEntity) {
                cause += " " + ((PlayerEntity) source).getGameProfile().getName();
            }
            CommonInfoStorage info = new CommonInfoStorage((PlayerEntity) entityLiving, "Died because of " + cause);
            new Thread(() -> writeJson(info.serializeJson(), playerDiedLogFile, true)).start();
        } else {
            if (source instanceof PlayerEntity) {
                PlayerEntity trueSource = (PlayerEntity) source;
                CommonInfoStorage info = new CommonInfoStorage(trueSource, "Player killed " + EntityType.getKey(entityLiving.getType()).toString());
                new Thread(() -> writeJson(info.serializeJson(), playerKilledLogFile, true)).start();
            }
        }
    }

    private static void init() {
        String date = DateUtils.toString(System.currentTimeMillis(), "MMddyyyy");
        Main.LOGGER.info(date);
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
            if (loginLogFile == null)
                loginLogFile = FIleUtils.createSubFile("logInAndOut.json", folder);
            if (chatLogFile == null)
                chatLogFile = FIleUtils.createSubFile("ChatLog.json", folder);
            if (commandLogFile == null)
                commandLogFile = FIleUtils.createSubFile("CommandLog.json", folder);
            if (playerDimensionLogFile == null)
                playerDimensionLogFile = FIleUtils.createSubFile("PlayerChangeDimensionLog.json", folder);
            if (playerDiedLogFile == null)
                playerDiedLogFile = FIleUtils.createSubFile("PlayerDiedLog.json", folder);
            if (playerKilledLogFile == null)
                playerKilledLogFile = FIleUtils.createSubFile("EntitiesKilledByPlayerLog.json", folder);
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
