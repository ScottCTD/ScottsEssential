package xyz.scottc.scessential.registries;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.capability.CapabilitySCEPlayerData;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistry {

    /**
     * Register Capability
     */
    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        CapabilitySCEPlayerData.register();
    }

    /**
     * Attach Capability
     */
    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CapabilityAttacher {

        @SubscribeEvent
        public static void onAttachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
            Entity entity = event.getObject();
            if (!entity.world.isRemote && entity instanceof PlayerEntity) {
                event.addCapability(new ResourceLocation(Main.MODID, "sce_player_data"), new CapabilitySCEPlayerData.Provider());
            }
        }

    }

}
