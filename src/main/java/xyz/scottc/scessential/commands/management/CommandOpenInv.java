package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(invseeAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> invSee(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target")))
                        )
                        .requires(source -> source.hasPermission(2))
        );
    }

    private static int invSee(ServerPlayer source, ServerPlayer target) {
        if (source.equals(target)) {
            source.sendMessage(TextUtils.getYellowTextFromI18n(true, false, false,
                    TextUtils.getTranslationKey("message", "cantOpenSelfInv")), Util.NIL_UUID);
            return 1;
        }
        NetworkHooks.openGui(source, new MenuProvider() {


            @Override
            public @NotNull Component getDisplayName() {
                return TextUtils.getContainerNameTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("text", "playerInv"), target.getGameProfile().getName());
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory sourceInv, @NotNull Player source) {
                return OthersInvContainer.getServerSideInstance(id, sourceInv, target.getInventory());
            }
        });
        return 1;
    }

    public static class OthersInventory implements Container {

        Inventory playerInventory;

        public OthersInventory(Inventory player) {
            this.playerInventory = player;
        }

        private int getPlayerInvIndex(int index) {
            return index - 36;
        }

        @Override
        public int getContainerSize() {
            return 41;
        }

        @Override
        public boolean isEmpty() {
            return this.playerInventory.isEmpty();
        }

        @Override
        public ItemStack getItem(int index) {
            int realIndex = this.getPlayerInvIndex(index);
            return playerInventory.getItem(realIndex);
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            int realIndex = this.getPlayerInvIndex(index);
            ItemStack itemStack = this.playerInventory.removeItem(realIndex, count);
            this.setChanged();
            return itemStack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            int realIndex = this.getPlayerInvIndex(index);
            ItemStack itemStack = this.playerInventory.removeItemNoUpdate(realIndex);
            this.setChanged();
            return itemStack;
        }

        @Override
        public void setItem(int index, @NotNull ItemStack stack) {
            int realIndex = this.getPlayerInvIndex(index);
            this.playerInventory.setItem(realIndex, stack);
            this.setChanged();
        }

        @Override
        public void setChanged() {
            this.playerInventory.setChanged();
            this.playerInventory.player.getInventory().setChanged();
            this.playerInventory.player.inventoryMenu.broadcastChanges();
        }

        @Override
        public boolean stillValid(Player p_18946_) {
            return true;
        }


        @Override
        public int getMaxStackSize() {
            return this.playerInventory.getMaxStackSize();
        }


        @Override
        public void clearContent() {
            playerInventory.clearContent();
            this.setChanged();
        }


        @Override
        public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
            int realIndex = this.getPlayerInvIndex(index);
            return this.playerInventory.canPlaceItem(realIndex, stack);
        }



    }
}
