package com.firebears.arcaneindustry;

import com.firebears.arcaneindustry.blocks.ModBlocks;
import com.firebears.arcaneindustry.crafting.ModCrafting;
import com.firebears.arcaneindustry.handler.ModGuiHandler;
import com.firebears.arcaneindustry.items.ModItems;
import com.firebears.arcaneindustry.tileentity.ModTileEntities;
import com.firebears.arcaneindustry.world.ArcaneIndustryWorldGen;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		ModBlocks.createBlocks();
		ModItems.createItems();
		ModTileEntities.init();
	}
	
	public void init(FMLInitializationEvent e) {
		ModCrafting.initCrafting();

		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new ModGuiHandler());

		GameRegistry.registerWorldGenerator(new ArcaneIndustryWorldGen(), 0);
	}
	
	public void postInit(FMLPostInitializationEvent e) {
		
	}
}
