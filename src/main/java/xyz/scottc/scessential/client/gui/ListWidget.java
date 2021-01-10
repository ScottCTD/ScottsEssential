package xyz.scottc.scessential.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.client.utils.ScreenUtils;
import xyz.scottc.scessential.utils.PageableList;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ListWidget implements IRenderable, IGuiEventListener {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int elementHeight;
    private final FontRenderer fontRenderer;

    private final PageableList<ITextComponent> texts;

    public ListWidget(int x, int y, int width, int height, FontRenderer fontRenderer, List<ITextComponent> elements) {
        this.x = x + 5;
        this.y = y + 5;
        this.width = width;
        this.height = height;
        this.fontRenderer = fontRenderer;
        this.elementHeight = this.fontRenderer.FONT_HEIGHT + 5;

        int onePage = this.height / this.elementHeight;
        int totalPages = elements.size() / onePage;
        this.texts = new PageableList<>(elements, onePage, elements.size() % onePage == 0 ? totalPages : totalPages + 1);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < this.texts.getCurrentPage().size(); i++) {
            int realY = this.y + this.elementHeight * i;
            if (realY < this.y + this.height) {
                ITextComponent text = this.texts.getCurrentPage().get(i);
                AtomicInteger color = new AtomicInteger(0xFFFFFF);
                Optional.ofNullable(text.getStyle().getColor()).ifPresent(c -> color.set(c.getColor()));
                ScreenUtils.drawStringDropShadow(matrixStack, this.fontRenderer, text, this.x, realY, color.get());
            }
        }
    }

    public PageableList<ITextComponent> getTexts() {
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

    public static class PageCounter implements IRenderable {

        private int x;
        private int y;
        private final FontRenderer fontRenderer;
        private final String separator;

        private int current;
        private int total;

        public PageCounter(int x, int y, FontRenderer fontRenderer, String separator, int current, int total) {
            this.x = x;
            this.y = y;
            this.fontRenderer = fontRenderer;
            this.separator = separator;
            this.current = current;
            this.total = total;
        }

        @Override
        public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            ScreenUtils.drawStringDropShadow(matrixStack, this.fontRenderer, this.getText(), this.x, this.y, 0xFFFFFF);
        }

        public ITextComponent getText() {
            return new StringTextComponent(this.current + " " + this.separator + " " + this.total);
        }

        public int getWidth() {
            return this.fontRenderer.getStringWidth(this.getText().getString());
        }

        public int getHeight() {
            return this.fontRenderer.FONT_HEIGHT;
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
