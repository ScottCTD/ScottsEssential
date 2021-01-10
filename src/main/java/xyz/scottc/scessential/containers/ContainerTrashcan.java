package xyz.scottc.scessential.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.registries.ContainerTypeRegistry;

public class ContainerTrashcan extends Container {

    private final PlayerInventory playerInventory;
    private final ItemStackHandler itemStackHandler;
    private final CommandTrashcan.Trashcan trashcan;

    protected ContainerTrashcan(int id, PlayerInventory playerInventory, CommandTrashcan.Trashcan trashcan) {
        super(ContainerTypeRegistry.trashcanContainerType, id);
        this.trackIntArray(trashcan);
        this.playerInventory = playerInventory;
        this.itemStackHandler = trashcan.getCurrentContents();
        this.trashcan = trashcan;
        this.addSlots();
    }

    public static ContainerTrashcan getServerSideInstance(int id, PlayerInventory playerInventory, CommandTrashcan.Trashcan trashcan) {
        return new ContainerTrashcan(id, playerInventory, trashcan);
    }

    public static ContainerTrashcan getClientSideInstance(int id, PlayerInventory playerInventory, PacketBuffer data) {
        return new ContainerTrashcan(id, playerInventory, new CommandTrashcan.Trashcan());
    }

    @Override
    public @NotNull ItemStack transferStackInSlot(@NotNull PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot == null) return ItemStack.EMPTY;
        ItemStack itemStack = slot.getStack();
        if (index < 36) {
            if (!this.mergeItemStack(itemStack, 36, 85, false)) return ItemStack.EMPTY;
        } else {
            if (!this.mergeItemStack(itemStack, 9, 35, false)) {
                if (!this.mergeItemStack(itemStack, 0, 8, true)) return ItemStack.EMPTY;
            }
        }
        return itemStack;
    }

    private void addSlots() {
        final int slotLength = 18, startX = 8, playerHotbarStartY = 198, playerMainInvStartY = 140, chestInvStartY = 18;

        int index = 0;
        // Player hotbar 0 - 8 inclusive
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(this.playerInventory, index, startX + slotLength * i, playerHotbarStartY));
            index++;
        }
        // player main inv 9 - 35 inclusive
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(this.playerInventory, index, startX + slotLength * j, playerMainInvStartY + slotLength * i));
                index++;
            }
        }
        // chest inv 36 - 49
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                this.addSlot(new SlotItemHandler(this.itemStackHandler, index, startX + slotLength * j, chestInvStartY + slotLength * i));
                index++;
            }
        }
        // chest inv 50 - 85
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new SlotItemHandler(this.itemStackHandler, index, startX + slotLength * j, chestInvStartY + slotLength * i));
                index++;
            }
        }
    }

    public CommandTrashcan.Trashcan getTrashcan() {
        return this.trashcan;
    }

    @Override
    public boolean canInteractWith(@NotNull PlayerEntity playerIn) {
        return true;
    }
}
