package xyz.scottc.scessential.client.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;


public class ScreenUtils {

    private ScreenUtils() {}

    public static void drawStringDropShadow(PoseStack matrixStack, Font fontRenderer, Component text, int x, int y, int color) {
        fontRenderer.draw(matrixStack, text, (float)x, (float)y, color);
    }

    public static void drawString(PoseStack matrixStack, Font fontRenderer, Component text, int x, int y, int color) {
        fontRenderer.draw(matrixStack, text, x, y, color);
    }

}
