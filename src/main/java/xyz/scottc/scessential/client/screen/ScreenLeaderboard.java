package xyz.scottc.scessential.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.client.gui.ListWidget;
import xyz.scottc.scessential.client.utils.ScreenUtils;
import xyz.scottc.scessential.core.PlayerStatistics;
import xyz.scottc.scessential.network.Network;
import xyz.scottc.scessential.network.PacketChangeLeaderboard;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.List;

public class ScreenLeaderboard extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/leaderboard.png");

    private ListWidget listWidget;
    private ListWidget.PageCounter pageCounterWidget;
    private final List<ITextComponent> elements;
    private final int xSize = 253;
    private final int ySize = 192;
    private int x;
    private int y;

    public ScreenLeaderboard(ITextComponent titleIn, List<ITextComponent> elements) {
        super(titleIn);
        this.elements = elements;
    }

    public static void open(ITextComponent title, List<ITextComponent> elements) {
        Minecraft.getInstance().displayGuiScreen(new ScreenLeaderboard(title, elements));
    }

    @Override
    protected void init() {
        this.x = (this.width - this.xSize) / 2;
        this.y = (this.height - this.ySize) / 2;
        this.listWidget = new ListWidget(this.x + 81, this.y + 20, 160, 151, this.font, elements);
        this.pageCounterWidget = new ListWidget.PageCounter(this.x + 77, this.y + 7, this.font, "/", 1, this.listWidget.getTexts().getTotalPages());

        // page
        this.addButton(new ExtendedButton(this.x + 80, this.y + 172, 40, 15,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "prevpage")),
                button -> this.listWidget.getTexts().prevPage()));
        this.addButton(new ExtendedButton(this.listWidget.getX() + this.listWidget.getWidth() - 40 - 4, this.y + 172, 40, 15,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "nextpage")),
                button -> this.listWidget.getTexts().nextPage()));

        // Mode
        this.addButton(new ExtendedButton(this.x + 5, this.listWidget.getY(), 73, 20,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "deathRank")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DEATH_AMOUNT))));
        this.addButton(new ExtendedButton(this.x + 5, this.listWidget.getY() + 20 + 5,  73, 20,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "timeplayedrank")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.TIME_PLAYED))));
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        this.blit(matrixStack, this.x, this.y, 0, 0, this.xSize, this.ySize);
        ScreenUtils.drawString(matrixStack, this.font, this.title, this.x + 5, this.y + 7, TextUtils.TITLE_COLOR);
        this.listWidget.render(matrixStack, mouseX, mouseY, partialTicks);

        // counter
        int counterX = this.listWidget.getX() + (this.listWidget.getWidth() - this.pageCounterWidget.getWidth()) / 2;
        this.pageCounterWidget.setX(counterX);
        this.pageCounterWidget.setCurrent(this.listWidget.getTexts().getIndex() + 1);
        this.pageCounterWidget.setTotal(this.listWidget.getTexts().getTotalPages());
        this.pageCounterWidget.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(input)) {
            this.closeScreen();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
