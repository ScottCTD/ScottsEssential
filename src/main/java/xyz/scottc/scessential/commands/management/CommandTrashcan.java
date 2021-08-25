package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.containers.ContainerTrashcan;
import xyz.scottc.scessential.core.SCEPlayerData;
import xyz.scottc.scessential.utils.TextUtils;

public class CommandTrashcan {

    @ConfigField
    public static boolean isTrashcanEnable = true;
    @ConfigField
    public static String trashcanAlias = "trashcan";
    @ConfigField
    public static int cleanTrashcanIntervalSeconds = 60;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(trashcanAlias)
                        .executes(context -> trashcan(context.getSource().getPlayerOrException()))
        );
    }

    private static int trashcan(ServerPlayer source) {
        SCEPlayerData data = SCEPlayerData.getInstance(source);
        Trashcan trashcan = data.getTrashcan();
        if (trashcan == null) {
            trashcan = new Trashcan();
            data.setTrashcan(trashcan);
        }
        NetworkHooks.openGui(source, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return TextUtils.getContainerNameTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("text", "trashcan"));
            }

            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                return ContainerTrashcan.getServerSideInstance(id, playerInventory, data.getTrashcan());
            }
        });
        return 1;
    }

    public static class Trashcan implements ContainerData {

        public static final int SIZE = 50 + 36;

        private final ItemStackHandler currentContents;
        private long lastCleanLong;
        private int nextCleanSeconds;

        public Trashcan() {
            this.currentContents = new ItemStackHandler(SIZE);
        }

        public void clear() {
            for (int i = 0; i < 50; i++) {
                this.currentContents.setStackInSlot(i + 36, ItemStack.EMPTY);
            }
            this.lastCleanLong = System.currentTimeMillis();
        }

        public ItemStackHandler getCurrentContents() {
            return currentContents;
        }

        public int getNextCleanSeconds() {
            return this.nextCleanSeconds;
        }

        public void setNextCleanSeconds(int nextCleanSeconds) {
            this.nextCleanSeconds = nextCleanSeconds;
        }

        public long getLastCleanLong() {
            return lastCleanLong;
        }

        public void setLastCleanLong(long lastCleanLong) {
            this.lastCleanLong = lastCleanLong;
        }

        @Override
        public int get(int index) {
            return this.nextCleanSeconds;
        }

        @Override
        public void set(int index, int value) {
            this.nextCleanSeconds = value;
        }

        @Override
        public int getCount() {
            return 1;
        }

    }

}
