package xyz.scottc.scessential.registries;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.commands.*;
import xyz.scottc.scessential.commands.info.CommandGetRegistryName;
import xyz.scottc.scessential.commands.management.CommandFly;
import xyz.scottc.scessential.commands.teleport.*;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandsRegistry {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        // Main Commands
        CommandSCE.register(dispatcher);

        // Teleport
        // TODO Control the enable and disable of commands
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

}
