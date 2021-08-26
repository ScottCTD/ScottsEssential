package xyz.scottc.scessential.registries;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.commands.CommandSCE;
import xyz.scottc.scessential.commands.info.CommandGetRegistryName;
import xyz.scottc.scessential.commands.info.CommandRank;
import xyz.scottc.scessential.commands.management.*;
import xyz.scottc.scessential.commands.teleport.*;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandsRegistry {

    public static boolean init = false;

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Main Commands
        CommandSCE.register(dispatcher);
        CommandGetRegistryName.register(dispatcher);
        CommandClear.register(dispatcher);

        // Teleport
        if (CommandSpawn.isSpawnEnable) CommandSpawn.register(dispatcher);
        if (CommandHome.isHomeEnable) CommandHome.register(dispatcher);
        if (CommandBack.isBackEnable) CommandBack.register(dispatcher);
        if (CommandRTP.isRTPEnable) CommandRTP.register(dispatcher);
        if (CommandWarp.isWarpEnable) CommandWarp.register(dispatcher);
        if (CommandTPA.isTPAEnable) CommandTPA.register(dispatcher);

        // Util Commands
        if (CommandFly.isFlyEnable) CommandFly.register(dispatcher);
        if (CommandOpenInv.isOpenInvEnable) CommandOpenInv.register(dispatcher);
        if (CommandHat.isHatEnabel) CommandHat.register(dispatcher);
        if (CommandTrashcan.isTrashcanEnable) CommandTrashcan.register(dispatcher);
        if (CommandRank.isRankEnable) CommandRank.register(dispatcher);

        Main.LOGGER.info("All commands registered!");
    }

    /**
     * See https://github.com/TeamCovertDragon/Harbinger/discussions/96
     * @param event FMLServerAboutToStartEvent
     */
    @SubscribeEvent
    public static void register(FMLServerAboutToStartEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        if (!init) {
            register(dispatcher);
            init = true;
        }
    }

    /**
     * @param event RegisterCommandsEvent
     */
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        if (init) {
            register(dispatcher);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        init = false;
    }

}
