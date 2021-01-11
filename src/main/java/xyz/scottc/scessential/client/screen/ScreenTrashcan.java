package xyz.scottc.scessential.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.containers.ContainerTrashcan;
import xyz.scottc.scessential.network.Network;
import xyz.scottc.scessential.network.PacketClearTrashcan;
import xyz.scottc.scessential.utils.TextUtils;

public class ScreenTrashcan extends ContainerScreen<ContainerTrashcan> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/trashcan.png");

    private int x;
    private int y;

    public ScreenTrashcan(ContainerTrashcan screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 175;
        this.ySize = 221;
        this.playerInventoryTitleY = 128;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.xSize) / 2;
        this.y = (this.height - this.ySize) / 2;
        this.addButton(new ExtendedButton(this.x + 135, this.y + 17, 34, 34,
                TextUtils.getContainerNameTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("text", "clear")), button -> Network.sendToServer(new PacketClearTrashcan())));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@NotNull MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        this.blit(matrixStack, this.x, this.y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@NotNull MatrixStack matrixStack, int x, int y) {
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        drawString(matrixStack, this.font, TextUtils.getContainerNameTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("text", "trashcantitle"), this.container.getTrashcan().getNextCleanSeconds()),
                this.x + 65, this.y + this.titleY, 0xfbfb54);
    }
}
