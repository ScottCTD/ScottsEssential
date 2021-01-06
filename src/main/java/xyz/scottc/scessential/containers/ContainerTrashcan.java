package xyz.scottc.scessential.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.scottc.scessential.commands.management.CommandTrashcan;
import xyz.scottc.scessential.registries.ContainerTypeRegistry;

public class ContainerTrashcan extends Container {

    private static final int SLOT_LENGTH = 18, START_X = 8,
            PLAYER_HOTBAR_START_Y = 198, PLAYER_MAININV_START_Y = 140,
            CHEST_INV_START_Y = 18;

    private PlayerInventory playerInventory;
    private ItemStackHandler itemStackHandler;
    private CommandTrashcan.Trashcan trashcan;

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
        // chest inv
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                this.addSlot(new SlotItemHandler(this.itemStackHandler, index, START_X + SLOT_LENGTH * j, CHEST_INV_START_Y + SLOT_LENGTH * i));
                index++;
            }
        }
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new SlotItemHandler(this.itemStackHandler, index, START_X + SLOT_LENGTH * j, CHEST_INV_START_Y + SLOT_LENGTH * i));
                index++;
            }
        }
    }

    public CommandTrashcan.Trashcan getTrashcan() {
        return trashcan;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
