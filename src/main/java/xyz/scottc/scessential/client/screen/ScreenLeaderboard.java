package xyz.scottc.scessential.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.client.gui.RankList;
import xyz.scottc.scessential.client.utils.ScreenUtils;
import xyz.scottc.scessential.core.PlayerStatistics;
import xyz.scottc.scessential.network.Network;
import xyz.scottc.scessential.network.PacketChangeLeaderboard;
import xyz.scottc.scessential.utils.TextUtils;

import java.util.List;

public class ScreenLeaderboard extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/leaderboard.png");

    private RankList rankList;
    private RankList.PageCounter pageCounterWidget;
    private final List<ITextComponent> elements;
    private final int xSize = 256;
    private final int ySize = 256;
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
        this.rankList = new RankList(this.x + 93, this.y + 20, 154, 205, this.font, elements);
        this.pageCounterWidget = new RankList.PageCounter(this.x + 77, this.y + 7, this.font, "/", 1, this.rankList.getTexts().getTotalPages());

        // page
        this.addButton(new ExtendedButton(this.rankList.getX() - 6, this.rankList.getY() + this.rankList.getHeight() - 1, 60, 22,
                new StringTextComponent("<--"),
                button -> this.rankList.getTexts().prevPage()));
        this.addButton(new ExtendedButton(this.rankList.getX() + this.rankList.getWidth() - 60 - 4,
                this.rankList.getY() + this.rankList.getHeight() - 1, 60, 22,
                new StringTextComponent("-->"),
                button -> this.rankList.getTexts().nextPage()));

        // Mode
        int buttonWidth = 77, buttonHeight = 20;
        ExtendedButton deathRankButton = new ExtendedButton(this.x + 8, this.rankList.getY() - 3, buttonWidth, buttonHeight,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "deathButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DEATH_AMOUNT)));
        ExtendedButton playedTimeButton = new ExtendedButton(deathRankButton.x, deathRankButton.y + deathRankButton.getHeightRealms() + 5, buttonWidth, buttonHeight,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "timePlayedButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.TIME_PLAYED)));
        ExtendedButton mobsKilledButton = new ExtendedButton(deathRankButton.x, playedTimeButton.y + playedTimeButton.getHeightRealms() + 5, buttonWidth, buttonHeight,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "mobsKilledButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.MOBS_KILLED)));
        ExtendedButton distanceWalked = new ExtendedButton(deathRankButton.x, mobsKilledButton.y + mobsKilledButton.getHeightRealms() + 5, buttonWidth, buttonHeight,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "distanceWalkedButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DISTANCE_WALKED)));
        ExtendedButton blocksBrokeRank = new ExtendedButton(deathRankButton.x, distanceWalked.y + distanceWalked.getHeightRealms() + 5, buttonWidth, buttonHeight,
                new TranslationTextComponent(TextUtils.getTranslationKey("text", "blocksBrokeButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.BLOCKS_BROKE)));

        this.addButton(deathRankButton);
        this.addButton(playedTimeButton);
        this.addButton(mobsKilledButton);
        this.addButton(distanceWalked);
        this.addButton(blocksBrokeRank);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        // Render the background dark shade
        this.renderBackground(matrixStack);
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(matrixStack, this.x, this.y, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize, 0, 0, 0, 0, 0);
        //this.blit(matrixStack, this.x, this.y, 0, 0, this.xSize, this.ySize);
        ScreenUtils.drawString(matrixStack, this.font, this.title, this.x + 5, this.y + 7, TextUtils.TITLE_COLOR);

        // counter
        int counterX = this.rankList.getX() + (this.rankList.getWidth() - this.pageCounterWidget.getWidth()) / 2;
        this.pageCounterWidget.setX(counterX);
        this.pageCounterWidget.setCurrent(this.rankList.getTexts().getIndex() + 1);
        this.pageCounterWidget.setTotal(this.rankList.getTexts().getTotalPages());
        this.pageCounterWidget.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.rankList.render(matrixStack, mouseX, mouseY, partialTicks);
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
