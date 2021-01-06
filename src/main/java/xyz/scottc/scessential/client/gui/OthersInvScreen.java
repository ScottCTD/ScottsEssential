package xyz.scottc.scessential.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.commands.management.CommandOpenInv;

public class OthersInvScreen extends ContainerScreen<CommandOpenInv.OthersInvContainer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/others_inventory.png");

    public static final int TEXTURE_WIDTH = 175, TEXTURE_HEIGHT = 214;

    public OthersInvScreen(CommandOpenInv.OthersInvContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = TEXTURE_WIDTH;
        this.ySize = TEXTURE_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.playerInventoryTitleY = 120;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        blit(matrixStack, (this.width - this.xSize) / 2, (this.height - this.ySize) / 2, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
}
