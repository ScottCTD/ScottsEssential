package xyz.scottc.scessential.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.commands.management.CommandOpenInv;
import xyz.scottc.scessential.registries.ContainerTypeRegistry;

public class OthersInvContainer extends AbstractContainerMenu {

    private static final int OTHER_INV_SIZE = 77;

    private static final int SLOT_LENGTH = 18, START_X = 8,
            TARGETINV_HOTBAR_START_Y = 99, TARGETINV_MAININV_START_Y = 41,
            TARGETINV_ARMORANDSECONDHAND_START_Y = 19, TARGETINV_ARMOR_START_X = 62, TARGETINV_SECONDHAND_START_X = 98,
            PLAYER_MAININV_START_Y = 132, PLAYER_HOTBAR_START_Y = 190;

    private final Inventory playerInventory;
    private final Container targetInventory;

    public OthersInvContainer(int id, Inventory playerInventory, Container targetInventory) {
        super(ContainerTypeRegistry.othersContainerType, id);
        this.playerInventory = playerInventory;
        this.targetInventory = targetInventory;
        this.targetInventory.startOpen(this.playerInventory.player);
        this.addSlots();
    }

    public static OthersInvContainer getClientSideInstance(int id, Inventory playerInventory, FriendlyByteBuf data) {
        return new OthersInvContainer(id, playerInventory, new SimpleContainer(OTHER_INV_SIZE));
    }



    public static OthersInvContainer getServerSideInstance(int id, Inventory playerInventory, Inventory targetInv) {
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
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.targetInventory.stopOpen(playerIn);
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemStack = this.slots.get(index).getItem();
        if (!itemStack.equals(ItemStack.EMPTY)) {
            if (index < 36) {
                Item item = itemStack.getItem();
                if (item instanceof ArmorItem) {
                    ResourceLocation registryName = item.getRegistryName();
                    if (registryName != null) {
                        String s = registryName.toString();
                        if (s.contains("helmet")) {
                            if (this.moveItemStackTo(itemStack, 75, 76, false)) return itemStack;
                        } else if (s.contains("chestplate")) {
                            if (this.moveItemStackTo(itemStack, 74, 75, false)) return itemStack;
                        } else if (s.contains("leggings")) {
                            if (this.moveItemStackTo(itemStack, 73, 74, false)) return itemStack;
                        } else if (s.contains("boots")) {
                            if (this.moveItemStackTo(itemStack, 72, 73, false)) return itemStack;
                        }
                    }
                }
                if (!this.moveItemStackTo(itemStack, 45, 71, false)) {
                    if (!this.moveItemStackTo(itemStack, 36, 44, false)) return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack, 9, 35, false)) {
                    if (!this.moveItemStackTo(itemStack, 0, 8, false)) return ItemStack.EMPTY;
                }
            }
        }
        return itemStack;
    }
}
