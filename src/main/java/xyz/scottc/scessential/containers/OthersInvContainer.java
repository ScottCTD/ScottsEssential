package xyz.scottc.scessential.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.commands.management.CommandOpenInv;
import xyz.scottc.scessential.registries.ContainerTypeRegistry;

public class OthersInvContainer extends Container {

    private static final int OTHER_INV_SIZE = 77;

    private static final int SLOT_LENGTH = 18, START_X = 8,
            TARGETINV_HOTBAR_START_Y = 99, TARGETINV_MAININV_START_Y = 41,
            TARGETINV_ARMORANDSECONDHAND_START_Y = 19, TARGETINV_ARMOR_START_X = 62, TARGETINV_SECONDHAND_START_X = 98,
            PLAYER_MAININV_START_Y = 132, PLAYER_HOTBAR_START_Y = 190;

    private final PlayerInventory playerInventory;
    private final IInventory targetInventory;

    public OthersInvContainer(int id, PlayerInventory playerInventory, IInventory targetInventory) {
        super(ContainerTypeRegistry.othersContainerType, id);
        this.playerInventory = playerInventory;
        this.targetInventory = targetInventory;
        this.targetInventory.openInventory(this.playerInventory.player);
        this.addSlots();
    }

    public static OthersInvContainer getClientSideInstance(int id, PlayerInventory playerInventory, PacketBuffer data) {
        return new OthersInvContainer(id, playerInventory, new Inventory(OTHER_INV_SIZE));
    }

    public static OthersInvContainer getServerSideInstance(int id, PlayerInventory playerInventory, PlayerInventory targetInv) {
        return new OthersInvContainer(id, playerInventory, new CommandOpenInv.OthersInventory(targetInv));
    }

    private void addSlots() {
        int index = 0;
        // Player hotbar 0 - 8 inclusive
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(this.playerInventory, index, START_X + SLOT_LENGTH * i, PLAYER_HOTBAR_START_Y));
            index++;
        }
        // player main inv 9 - 35 inclusive
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(this.playerInventory, index, START_X + SLOT_LENGTH * j, PLAYER_MAININV_START_Y + SLOT_LENGTH * i));
                index++;
            }
        }
        // Target inv hot bar 36 - 44 inclusive
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(this.targetInventory, index, START_X + SLOT_LENGTH * i, TARGETINV_HOTBAR_START_Y));
            index++;
        }
        // Target inv main inventory 45 - 71
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(this.targetInventory, index, START_X + SLOT_LENGTH * j, TARGETINV_MAININV_START_Y + SLOT_LENGTH * i));
                index++;
            }
        }
        // Target inv armor slots 72 - 75
        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(this.targetInventory, index, TARGETINV_ARMOR_START_X - SLOT_LENGTH * i, TARGETINV_ARMORANDSECONDHAND_START_Y));
            index++;
        }
        // Target inv second hand 76
        this.addSlot(new Slot(this.targetInventory, index, TARGETINV_SECONDHAND_START_X, TARGETINV_ARMORANDSECONDHAND_START_Y));
    }

    @Override
    public void onContainerClosed(@NotNull PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.targetInventory.closeInventory(playerIn);
    }

    @Override
    public boolean canInteractWith(@NotNull PlayerEntity playerIn) {
        return true;
    }

    @Override
    public @NotNull ItemStack transferStackInSlot(@NotNull PlayerEntity playerIn, int index) {
        ItemStack itemStack = this.inventorySlots.get(index).getStack();
        if (!itemStack.isItemEqual(ItemStack.EMPTY)) {
            if (index < 36) {
                Item item = itemStack.getItem();
                if (item instanceof ArmorItem) {
                    ResourceLocation registryName = item.getRegistryName();
                    if (registryName != null) {
                        String s = registryName.toString();
                        if (s.contains("helmet")) {
                            if (this.mergeItemStack(itemStack, 75, 76, false)) return itemStack;
                        } else if (s.contains("chestplate")) {
                            if (this.mergeItemStack(itemStack, 74, 75, false)) return itemStack;
                        } else if (s.contains("leggings")) {
                            if (this.mergeItemStack(itemStack, 73, 74, false)) return itemStack;
                        } else if (s.contains("boots")) {
                            if (this.mergeItemStack(itemStack, 72, 73, false)) return itemStack;
                        }
                    }
                }
                if (!this.mergeItemStack(itemStack, 45, 71, false)) {
                    if (!this.mergeItemStack(itemStack, 36, 44, false)) return ItemStack.EMPTY;
                }
            } else {
                if (!this.mergeItemStack(itemStack, 9, 35, false)) {
                    if (!this.mergeItemStack(itemStack, 0, 8, false)) return ItemStack.EMPTY;
                }
            }
        }
        return itemStack;
    }
}
