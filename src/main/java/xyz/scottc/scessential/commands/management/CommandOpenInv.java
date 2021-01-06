package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.registries.ContainerTypeRegistry;
import xyz.scottc.scessential.utils.TextUtils;

/**
 *
 * /invsee
 * /openinv in the future, if forge fixed bug of redirecting commands
 */
public class CommandOpenInv {

    // TODO
    @ConfigField
    public static boolean isOpenInvEnable = true;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("invsee")
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> invSee(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "Target")))
                        )
        );
    }

    private static int invSee(ServerPlayerEntity source, ServerPlayerEntity target) {
        NetworkHooks.openGui(source, new INamedContainerProvider() {
            @Override
            public @NotNull
            ITextComponent getDisplayName() {
                return TextUtils.getContainerNameTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("text", "playerinv"), target.getGameProfile().getName());
            }

            @Override
            public @NotNull Container createMenu(int id, PlayerInventory sourceInv, PlayerEntity source) {
                return OthersInvContainer.getServerSideInstance(id, sourceInv, target.inventory);
            }
        }, buffer -> buffer.writeInt(41));
        return 1;
    }

    private static class OthersInventory implements IInventory {

        PlayerInventory playerInventory;

        public static final int row = 5;
        public static final int column = 9;

        public OthersInventory(PlayerInventory player) {
            this.playerInventory = player;
        }

        private int getPlayerInvIndex(int index) {
            // Invalid slot
            if (index >= 5 && index < 9) {
                return -1;
            }
            //Second hand
            if (index == 4) {
                return 40;
            }
            // Armor
            if (index >= 0 && index < 4) {
                return 39 - index;
            }
            // main inv
            if (index >= 9 && index < 36) {
                return index;
            }
            // hot bar
            if (index >= 36 && index < this.getSizeInventory()) {
                return index - 36;
            }
            return -1;
        }

        @Override
        public int getSizeInventory() {
            return row * column;
        }

        @Override
        public boolean isEmpty() {
            return this.playerInventory.isEmpty();
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            int realIndex = this.getPlayerInvIndex(index);
            if (realIndex == -1) return ItemStack.EMPTY;
            return playerInventory.getStackInSlot(realIndex);
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            int realIndex = this.getPlayerInvIndex(index);
            if (realIndex == -1) return ItemStack.EMPTY;
            return this.playerInventory.decrStackSize(realIndex, count);
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            int realIndex = this.getPlayerInvIndex(index);
            if (realIndex == -1) return ItemStack.EMPTY;
            return this.playerInventory.removeStackFromSlot(realIndex);
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            int realIndex = this.getPlayerInvIndex(index);
            if (realIndex != -1) this.playerInventory.setInventorySlotContents(realIndex, stack);
            this.markDirty();
        }

        @Override
        public void markDirty() {
            this.playerInventory.markDirty();
            this.playerInventory.player.container.detectAndSendChanges();
        }

        @Override
        public boolean isUsableByPlayer(PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            playerInventory.clear();
        }

        @Override
        public int getInventoryStackLimit() {
            return this.playerInventory.getInventoryStackLimit();
        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            int realIndex = this.getPlayerInvIndex(index);
            if (realIndex == -1) return false;
            return this.playerInventory.isItemValidForSlot(realIndex, stack);
        }
    }

    // TODO Complete functionality please
    public static class OthersInvContainer extends Container {

        private static final int SLOT_LENGTH = 18, START_X = 8,
                TARGETINV_HOTBAR_START_Y = 99, TARGETINV_MAININV_START_Y = 41,
                TARGETINV_ARMORANDSECONDHAND_START_Y = 19, TARGETINV_ARMOR_START_X = 62, TARGETINV_SECONDHAND_START_X = 98,
                PLAYER_MAININV_START_Y = 132, PLAYER_HOTBAR_START_Y = 190;

        private OthersItemHandler contents;

        public OthersInvContainer(int id, PlayerInventory playerInventory, OthersItemHandler content) {
            super(ContainerTypeRegistry.othersContainerType, id);
            this.contents = content;
            int index = 0;
            // Player hotbar 0 - 8 inclusive
            for (int i = 0; i < 9; i++) {
                this.addSlot(new Slot(playerInventory, index, START_X + SLOT_LENGTH * i, PLAYER_HOTBAR_START_Y));
                index++;
            }
            // player main inv 9 - 35 inclusive
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new Slot(playerInventory, index, START_X + SLOT_LENGTH * j, PLAYER_MAININV_START_Y + SLOT_LENGTH * i));
                    index++;
                }
            }
            // Target inv hot bar 36 - 44 inclusive
            for (int i = 0; i < 9; i++) {
                this.addSlot(new SlotItemHandler(content, index, START_X + SLOT_LENGTH * i, TARGETINV_HOTBAR_START_Y));
                index++;
            }
            // Target inv main inventory 45 - 71
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new SlotItemHandler(content, index, START_X + SLOT_LENGTH * j, TARGETINV_MAININV_START_Y + SLOT_LENGTH * i));
                    index++;
                }
            }
            // Target inv armor slots 72 - 75
            for (int i = 0; i < 4; i++) {
                this.addSlot(new SlotItemHandler(content, index, TARGETINV_ARMOR_START_X - SLOT_LENGTH * i, TARGETINV_ARMORANDSECONDHAND_START_Y));
                index++;
            }
            // Target inv second hand 76
            this.addSlot(new SlotItemHandler(content, index, TARGETINV_SECONDHAND_START_X, TARGETINV_ARMORANDSECONDHAND_START_Y));
        }

        public static OthersInvContainer getClientSideInstance(int id, PlayerInventory playerInventory, PacketBuffer data) {
            return new OthersInvContainer(id, playerInventory, new OthersItemHandler(data.readInt()));
        }

        public static OthersInvContainer getServerSideInstance(int id, PlayerInventory playerInventory, PlayerInventory targetInv) {
            return new OthersInvContainer(id, playerInventory, new OthersItemHandler(targetInv));
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return true;
        }

        @Override
        public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
            ItemStack itemStack = this.inventorySlots.get(index).getStack();
            if (!itemStack.isItemEqual(ItemStack.EMPTY)) {
                if (index < 36) {
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

    public static class OthersItemHandler implements IItemHandler, IItemHandlerModifiable {

        private PlayerInventory targetInv;

        public OthersItemHandler(PlayerInventory targetInv) {
            this.targetInv = targetInv;
        }

        public OthersItemHandler(int size) {

        }

        private int getTargetInvSlot(int slot) {
            if (slot >= 36 && slot < 77) {
                return slot - 36;
            } else {
                return -1;
            }
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            int realSlot = this.getTargetInvSlot(slot);
            if (realSlot != -1) {
                this.targetInv.setInventorySlotContents(realSlot, stack);
                this.targetInv.markDirty();
            }
        }

        @Override
        public int getSlots() {
            return 41;
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot) {
            int realSlot = this.getTargetInvSlot(slot);
            return realSlot == -1 ? ItemStack.EMPTY : this.targetInv.getStackInSlot(realSlot);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!simulate) {
                int realSlot = this.getTargetInvSlot(slot);
                if (this.targetInv.add(realSlot, stack)) {
                    return ItemStack.EMPTY;
                } else {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!simulate) {
                int realSlot = this.getTargetInvSlot(slot);
                return this.targetInv.decrStackSize(realSlot, amount);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            int realSlot = this.getTargetInvSlot(slot);
            return this.targetInv.getStackInSlot(realSlot).getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            int realSlot = this.getTargetInvSlot(slot);
            return realSlot != -1 && this.targetInv.isItemValidForSlot(realSlot, stack);
        }
    }

}
