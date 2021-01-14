package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
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

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(trashcanAlias)
                        .executes(context -> trashcan(context.getSource().asPlayer()))
        );
    }

    private static int trashcan(ServerPlayerEntity source) {
        SCEPlayerData data = SCEPlayerData.getInstance(source);
        Trashcan trashcan = data.getTrashcan();
        if (trashcan == null) {
            trashcan = new Trashcan();
            data.setTrashcan(trashcan);
        }
        NetworkHooks.openGui(source, new INamedContainerProvider() {
            @Override
            public @NotNull ITextComponent getDisplayName() {
                return TextUtils.getContainerNameTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("text", "trashcan"));
            }

            @Override
            public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
                return ContainerTrashcan.getServerSideInstance(id, playerInventory, data.getTrashcan());
            }
        });
        return 1;
    }

    public static class Trashcan implements IIntArray {

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
        public int size() {
            return 1;
        }
    }

}
