package xyz.scottc.scessential.client.screen;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import xyz.scottc.scessential.Main;
import xyz.scottc.scessential.containers.ContainerTrashcan;
import xyz.scottc.scessential.network.Network;
import xyz.scottc.scessential.network.PacketClearTrashcan;
import xyz.scottc.scessential.utils.TextUtils;

public class ScreenTrashcan extends AbstractContainerScreen<ContainerTrashcan> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/gui/trashcan.png");

    private int x;
    private int y;

    public ScreenTrashcan(ContainerTrashcan screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 175;
        this.imageHeight = 221;
        this.titleLabelX = 128;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.getXSize()) / 2;
        this.y = (this.height - this.getYSize()) / 2;
        this.addWidget(new ExtendedButton(this.x + 135, this.y + 17, 34, 34,
                TextUtils.getContainerNameTextFromI18n(false, false, false,
                        TextUtils.getTranslationKey("text", "clear")), button -> Network.sendToServer(new PacketClearTrashcan())));
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        this.getMinecraft().getTextureManager().bindForSetup(TEXTURE);
        this.blit(matrixStack, this.x, this.y, 0, 0, this.getXSize(), this.getYSize());
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrixStack, int x, int y) {
        super.renderLabels(matrixStack, x, y);
    }


    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        drawString(matrixStack, this.font, TextUtils.getContainerNameTextFromI18n(false, false, false,
                TextUtils.getTranslationKey("text", "trashcanTitle"), this.getMenu().getTrashcan().getNextCleanSeconds()),
                this.x + 65, this.y + this.titleLabelY, 0xfbfb54);
    }
}
