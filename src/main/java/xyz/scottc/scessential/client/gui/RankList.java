package xyz.scottc.scessential.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fmlclient.gui.GuiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.scottc.scessential.client.utils.ScreenUtils;
import xyz.scottc.scessential.utils.PageableList;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RankList implements Widget, GuiEventListener {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int elementHeight;
    private final Font fontRenderer;

    private final PageableList<Component> texts;

    public RankList(int x, int y, int width, int height, Font fontRenderer, List<Component> elements) {
        this.x = x + 5;
        this.y = y + 5;
        this.width = width - 5 * 2;
        this.height = height;
        this.fontRenderer = fontRenderer;
        this.elementHeight = this.fontRenderer.lineHeight + 5;

        int onePage = this.height / this.elementHeight;
        int totalPages = elements.size() / onePage;
        this.texts = new PageableList<>(elements, onePage, elements.size() % onePage == 0 ? totalPages : totalPages + 1);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < this.texts.getCurrentPage().size(); i++) {
            int realY = this.y + this.elementHeight * i;
            if (realY < this.y + this.height) {
                Component text = this.texts.getCurrentPage().get(i);
                String string = text.getString();
                if (this.fontRenderer.width(string) > this.width) {
                    int index = string.length() - 1;
                    while (this.fontRenderer.width(string) > this.width) {
                        string = string.substring(0, index) + "...";
                        index--;
                    }
                    index = string.indexOf(':');
                    // +2 because of space
                    String rank = string.substring(0, index + 2);
                    string = string.substring(index + 2);
                    text = new TextComponent(rank).append(TextUtils.getWhiteTextFromString(false, false, false, string)).withStyle(text.getStyle());
                }
                AtomicInteger color = new AtomicInteger(0xFFFFFF);
                Optional.ofNullable(text.getStyle().getColor()).ifPresent(c -> color.set(c.getValue()));
                ScreenUtils.drawStringDropShadow(matrixStack, this.fontRenderer, text, this.x, realY, color.get());
            }
        }
        Optional.ofNullable(this.getMouseOver(mouseX, mouseY)).ifPresent(tooltip ->
                GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(tooltip), mouseX, mouseY, this.x + this.width, this.y + this.height, -1, this.fontRenderer));
    }

    public @Nullable Component getMouseOver(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            int index = (int) ((mouseY - this.y) / this.elementHeight);
            if (index >= this.texts.getCurrentPage().size()) {
                return null;
            }
            return this.texts.getCurrentPage().get(index);
        }
        return null;
    }



    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }

    public PageableList<Component> getTexts() {
        return texts;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getElementHeight() {
        return elementHeight;
    }

    public static class PageCounter implements Widget {

        private int x;
        private int y;
        private final Font fontRenderer;
        private final String separator;

        private int current;
        private int total;

        public PageCounter(int x, int y, Font fontRenderer, String separator, int current, int total) {
            this.x = x;
            this.y = y;
            this.fontRenderer = fontRenderer;
            this.separator = separator;
            this.current = current;
            this.total = total;
        }

        @Override
        public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            ScreenUtils.drawStringDropShadow(matrixStack, this.fontRenderer, this.getText(), this.x, this.y, 0xFFFFFF);
        }

        public Component getText() {
            return new TextComponent(this.current + " " + this.separator + " " + this.total);
        }

        public int getWidth() {
            return this.fontRenderer.width(this.getText().getString());
        }

        public int getHeight() {
            return this.fontRenderer.lineHeight;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

}
