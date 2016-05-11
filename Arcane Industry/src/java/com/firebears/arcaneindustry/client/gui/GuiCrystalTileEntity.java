package com.firebears.arcaneindustry.client.gui;

import com.firebears.arcaneindustry.tileentity.CrystalTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiCrystalTileEntity extends GuiContainer {

	private IInventory playerInv;
	private CrystalTileEntity te;
	
	public GuiCrystalTileEntity(IInventory playerInv, CrystalTileEntity te) {
		super(new ContainerCrystalTileEntity(playerInv, te));
		
		this.playerInv = playerInv;
		this.te = te;
		
		this.xSize = 176;
		this.ySize = 192;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("arcaneindustry:textures/gui/container/crystal_tile_entity.png"));
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
	
	@Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// tile entity name text
        String s = this.te.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, 88 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);

        // input text
        this.fontRendererObj.drawString("Input", 7, 19, 4210752);

        // output text
        this.fontRendererObj.drawString("Output", 7, 59, 4210752);
        
        // inventory text
        this.fontRendererObj.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 97, 4210752);
    }
}
