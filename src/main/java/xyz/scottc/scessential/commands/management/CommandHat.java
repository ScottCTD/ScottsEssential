package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.TextUtils;

public class CommandHat {

    @ConfigField
    public static boolean isHatEnabel = true;
    @ConfigField
    public static String hatAlias = "hat";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(hatAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> hat(context.getSource().asPlayer(), EntityArgument.getPlayer(context, "Target")))
                        )
                .executes(context -> hat(context.getSource().asPlayer(), context.getSource().asPlayer()))
        );
    }

    private static int hat(ServerPlayerEntity source, ServerPlayerEntity target) {
        ItemStack itemStack = source.getHeldItemMainhand();
        ItemStack originStack = target.inventory.getStackInSlot(39);
        // 39 -> Head
        target.inventory.setInventorySlotContents(39, itemStack);
        source.setHeldItem(Hand.MAIN_HAND, originStack);
        target.inventory.markDirty();
        source.sendStatusMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "ok")), false);
        return 1;
    }

}
