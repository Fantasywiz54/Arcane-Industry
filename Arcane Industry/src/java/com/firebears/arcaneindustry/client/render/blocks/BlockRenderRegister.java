package com.firebears.arcaneindustry.client.render.blocks;

import com.firebears.arcaneindustry.Main;
import com.firebears.arcaneindustry.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class BlockRenderRegister {

	public static String modid = Main.MODID;
	
	public static void registerBlockRenderer() {
		reg(ModBlocks.crystalTileEntityBlock);
		reg(ModBlocks.rubyOre);
		reg(ModBlocks.sapphireOre);
		reg(ModBlocks.peridotOre);
	}
	
	public static void reg(Block block) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, 
				new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
}
