package xyz.scottc.scessential.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmlclient.gui.GuiUtils;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;
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

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/gui/leaderboard.png");

    private RankList rankList;
    private RankList.PageCounter pageCounterWidget;
    private final List<Component> elements;
    private final int xSize = 256;
    private final int ySize = 256;
    private int x;
    private int y;

    public ScreenLeaderboard(Component titleIn, List<Component> elements) {
        super(titleIn);
        this.elements = elements;
    }

    public static void open(Component title, List<Component> elements) {
        Minecraft.getInstance().setScreen(new ScreenLeaderboard(title, elements));
    }

    @Override
    protected void init() {
        this.x = (this.width - this.xSize) / 2;
        this.y = (this.height - this.ySize) / 2;
        this.rankList = new RankList(this.x + 93, this.y + 20, 154, 205, this.font, elements);
        this.pageCounterWidget = new RankList.PageCounter(this.x + 77, this.y + 7, this.font, "/", 1, this.rankList.getTexts().getTotalPages());

        // page
        this.addWidget(new ExtendedButton(this.rankList.getX() - 6, this.rankList.getY() + this.rankList.getHeight() - 1, 60, 22,
                new TextComponent("<--"),
                button -> this.rankList.getTexts().prevPage()));
        this.addWidget(new ExtendedButton(this.rankList.getX() + this.rankList.getWidth() - 54,
                this.rankList.getY() + this.rankList.getHeight() - 1, 60, 22,
                new TextComponent("-->"),
                button -> this.rankList.getTexts().nextPage()));

        // Mode
        int buttonWidth = 77, buttonHeight = 20;
        ExtendedButton deathRankButton = new ExtendedButton(this.x + 8, this.rankList.getY() - 3, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "deathButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DEATH_AMOUNT)));
        ExtendedButton playedTimeButton = new ExtendedButton(deathRankButton.x, deathRankButton.y + deathRankButton.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "timePlayedButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.TIME_PLAYED)));
        ExtendedButton mobsKilledButton = new ExtendedButton(deathRankButton.x, playedTimeButton.y + playedTimeButton.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "mobsKilledButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.MOBS_KILLED)));
        ExtendedButton distanceWalked = new ExtendedButton(deathRankButton.x, mobsKilledButton.y + mobsKilledButton.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "distanceWalkedButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DISTANCE_WALKED)));
        ExtendedButton blocksBrokeButton = new ExtendedButton(deathRankButton.x, distanceWalked.y + distanceWalked.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "blocksBrokeButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.BLOCKS_BROKE)));
        ExtendedButton fishCaughtButton = new ExtendedButton(deathRankButton.x, blocksBrokeButton.y + blocksBrokeButton.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "fishCaughtButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.FISH_CAUGHT)));
        ExtendedButton distanceBoated = new ExtendedButton(deathRankButton.x, fishCaughtButton.y + fishCaughtButton.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "distanceBoatedButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DISTANCE_BOATED)));
        ExtendedButton damageDealtButton = new ExtendedButton(deathRankButton.x, distanceBoated.y + distanceBoated.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "damageDealtButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DAMAGE_DEALT)));
        ExtendedButton damageTakenButton = new ExtendedButton(deathRankButton.x, damageDealtButton.y + damageDealtButton.getHeight() + 5, buttonWidth, buttonHeight,
                new TranslatableComponent(TextUtils.getTranslationKey("text", "damageTakenButton")),
                button -> Network.sendToServer(new PacketChangeLeaderboard(PlayerStatistics.StatisticsType.DAMAGE_TAKEN)));

        this.addWidget(deathRankButton);
        this.addWidget(playedTimeButton);
        this.addWidget(mobsKilledButton);
        this.addWidget(distanceWalked);
        this.addWidget(blocksBrokeButton);
        this.addWidget(fishCaughtButton);
        this.addWidget(distanceBoated);
        this.addWidget(damageDealtButton);
        this.addWidget(damageTakenButton);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // Render the background dark shade
        this.renderBackground(matrixStack);
        this.getMinecraft().getTextureManager().bindForSetup(TEXTURE);
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
        InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.getMinecraft().options.keyInventory.isActiveAndMatches(input)) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
