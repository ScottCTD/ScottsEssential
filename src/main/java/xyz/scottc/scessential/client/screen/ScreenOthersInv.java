package xyz.scottc.scessential.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.containers.OthersInvContainer;

public class ScreenOthersInv extends AbstractContainerScreen<OthersInvContainer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/gui/others_inventory.png");

    private int x, y;

    public ScreenOthersInv(OthersInvContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth= 175;
        this.imageHeight = 214;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.getXSize()) / 2;
        this.y = (this.height - this.getYSize()) / 2;
        this.titleLabelX = 120;
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        this.getMinecraft().getTextureManager().bindForSetup(TEXTURE);
        blit(matrixStack, this.x, this.y, 0, 0, this.getXSize(), this.getYSize());
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
