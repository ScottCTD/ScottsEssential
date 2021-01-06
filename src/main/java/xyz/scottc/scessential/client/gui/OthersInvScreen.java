package xyz.scottc.scessential.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.containers.OthersInvContainer;

public class OthersInvScreen extends ContainerScreen<OthersInvContainer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/others_inventory.png");

    private int x, y;

    public OthersInvScreen(OthersInvContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 175;
        this.ySize = 214;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.xSize) / 2;
        this.y = (this.height - this.ySize) / 2;
        this.playerInventoryTitleY = 120;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        blit(matrixStack, this.x, this.y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
}
