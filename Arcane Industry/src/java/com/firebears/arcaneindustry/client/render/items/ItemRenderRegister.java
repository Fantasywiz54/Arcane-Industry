package com.firebears.arcaneindustry.client.render.items;

import com.firebears.arcaneindustry.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ItemRenderRegister {

	public static String modid = Main.MODID;
	
	public static void registerItemRenderer() {

	}
	
	public static void reg(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, 
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
