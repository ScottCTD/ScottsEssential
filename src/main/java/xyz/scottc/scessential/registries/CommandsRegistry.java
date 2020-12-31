package xyz.scottc.scessential.registries;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.commands.CommandBack;
import xyz.scottc.scessential.commands.CommandHome;
import xyz.scottc.scessential.commands.CommandSpawn;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandsRegistry {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        CommandSpawn.register(dispatcher);
        CommandHome.register(dispatcher);
        CommandBack.register(dispatcher);

        Main.LOGGER.info("All commands registered!");
    }

}
