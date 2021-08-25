package xyz.scottc.scessential.commands.management;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import xyz.scottc.scessential.config.ConfigField;
import xyz.scottc.scessential.utils.TextUtils;

public class CommandHat {

    @ConfigField
    public static boolean isHatEnabel = true;
    @ConfigField
    public static String hatAlias = "hat";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(hatAlias)
                        .then(Commands.argument("Target", EntityArgument.player())
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> hat(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "Target")))
                        )
                .executes(context -> hat(context.getSource().getPlayerOrException(), context.getSource().getPlayerOrException()))
        );
    }

    private static int hat(ServerPlayer source, ServerPlayer target) {
        ItemStack itemStack = source.getMainHandItem();
        // 39 -> Head
        target.containerMenu.setRemoteSlot(39, itemStack);
        if (source.equals(target) && !source.isCreative()) source.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        target.containerMenu.broadcastChanges();
        source.sendMessage(TextUtils.getGreenTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("message", "ok")), Util.NIL_UUID);
        return 1;
    }

}
