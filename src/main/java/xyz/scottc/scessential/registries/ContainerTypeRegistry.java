package xyz.scottc.scessential.registries;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.containers.ContainerTrashcan;
import xyz.scottc.scessential.containers.OthersInvContainer;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerTypeRegistry {

    @ObjectHolder(Main.MOD_ID + ":others_inventory")
    public static ContainerType<OthersInvContainer> othersContainerType;

    @ObjectHolder(Main.MOD_ID + ":trashcan")
    public static ContainerType<ContainerTrashcan> trashcanContainerType;

    @SubscribeEvent
    public static void onContainerTypeRegister(RegistryEvent.Register<ContainerType<?>> event) {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();

        registry.register(IForgeContainerType
                .create(OthersInvContainer::getClientSideInstance)
                .setRegistryName("others_inventory"));

        registry.register(IForgeContainerType
                .create(ContainerTrashcan::getClientSideInstance)
                .setRegistryName("trashcan"));
    }

}
