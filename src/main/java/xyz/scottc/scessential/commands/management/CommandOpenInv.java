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
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.containers.OthersInvContainer;
import xyz.scottc.scessential.utils.TextUtils;

/**
 * 01/06/2021 22:53
 * /invsee
 * /openinv in the future, if forge fixed bug of redirecting commands
 */
public class CommandOpenInv {

    @ConfigField
    public static boolean isOpenInvEnable = true;
    @ConfigField
    public static String invseeAlias = "invsee";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(invseeAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> invSee(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "Target")))
                        )
        );
    }

    private static int invSee(ServerPlayerEntity source, ServerPlayerEntity target) {
        if (source.equals(target)) {
            source.sendStatusMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "cantopenselfinv")), false);
            return 1;
        }
        NetworkHooks.openGui(source, new INamedContainerProvider() {
            @Override
            public @NotNull ITextComponent getDisplayName() {
                return TextUtils.getContainerNameTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("text", "playerinv"), target.getGameProfile().getName());
            }

            @Override
            public @NotNull Container createMenu(int id, @NotNull PlayerInventory sourceInv, @NotNull PlayerEntity source) {
                return OthersInvContainer.getServerSideInstance(id, sourceInv, target.inventory);
            }
        });
        return 1;
    }

    public static class OthersInventory implements IInventory {

        PlayerInventory playerInventory;

        public OthersInventory(PlayerInventory player) {
            this.playerInventory = player;
        }

        private int getPlayerInvIndex(int index) {
            return index - 36;
        }

        @Override
        public int getSizeInventory() {
            return 41;
        }

        @Override
        public boolean isEmpty() {
            return this.playerInventory.isEmpty();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int index) {
            int realIndex = this.getPlayerInvIndex(index);
            return playerInventory.getStackInSlot(realIndex);
        }

        @Override
        public @NotNull ItemStack decrStackSize(int index, int count) {
            int realIndex = this.getPlayerInvIndex(index);
            ItemStack itemStack = this.playerInventory.decrStackSize(realIndex, count);
            this.markDirty();
            return itemStack;
        }

        @Override
        public @NotNull ItemStack removeStackFromSlot(int index) {
            int realIndex = this.getPlayerInvIndex(index);
            ItemStack itemStack = this.playerInventory.removeStackFromSlot(realIndex);
            this.markDirty();
            return itemStack;
        }

        @Override
        public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
            int realIndex = this.getPlayerInvIndex(index);
            this.playerInventory.setInventorySlotContents(realIndex, stack);
            this.markDirty();
        }

        @Override
        public void markDirty() {
            this.playerInventory.markDirty();
            this.playerInventory.player.container.detectAndSendChanges();
            this.playerInventory.player.openContainer.detectAndSendChanges();
        }

        @Override
        public boolean isUsableByPlayer(@NotNull PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            playerInventory.clear();
            this.markDirty();
        }

        @Override
        public int getInventoryStackLimit() {
            return this.playerInventory.getInventoryStackLimit();
        }

        @Override
        public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) {
            int realIndex = this.getPlayerInvIndex(index);
            return this.playerInventory.isItemValidForSlot(realIndex, stack);
        }
    }
}
