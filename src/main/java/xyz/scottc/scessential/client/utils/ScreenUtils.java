package xyz.scottc.scessential.client.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

public class ScreenUtils {

    private ScreenUtils() {}

    public static void drawStringDropShadow(MatrixStack matrixStack, FontRenderer fontRenderer, ITextComponent text, int x, int y, int color) {
        fontRenderer.func_243246_a(matrixStack, text, (float)x, (float)y, color);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer fontRenderer, ITextComponent text, int x, int y, int color) {
        fontRenderer.func_243248_b(matrixStack, text, x, y, color);
    }

}
