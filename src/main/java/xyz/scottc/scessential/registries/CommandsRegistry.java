package xyz.scottc.scessential.registries;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.commands.CommandSCE;
import xyz.scottc.scessential.commands.info.CommandGetRegistryName;
import xyz.scottc.scessential.commands.management.CommandFly;
import xyz.scottc.scessential.commands.teleport.*;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandsRegistry {

    /**
     * See https://github.com/TeamCovertDragon/Harbinger/discussions/96
     * @param event
     */
    @SubscribeEvent
    public static void register(FMLServerAboutToStartEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();

        // Main Commands
        CommandSCE.register(dispatcher);

        // Teleport
        if (CommandSpawn.isSpawnEnable) CommandSpawn.register(dispatcher);
        if (CommandHome.isHomeEnable) CommandHome.register(dispatcher);
        if (CommandBack.isBackEnable) CommandBack.register(dispatcher);
        if (CommandRTP.isRTPEnable) CommandRTP.register(dispatcher);
        if (CommandWarp.isWarpEnable) CommandWarp.register(dispatcher);
        if (CommandTPA.isTPAEnable) CommandTPA.register(dispatcher);

        // Util Commands
        if (CommandFly.isFlyEnable) CommandFly.register(dispatcher);

        // Info Commands
        CommandGetRegistryName.register(dispatcher);

        Main.LOGGER.info("All commands registered!");
    }

    /**
     * Not use it because I want config to be loaded before registering commands
     * @param event RegisterCommandsEvent
     */
    //@SubscribeEvent
    @Deprecated
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
    }

}
