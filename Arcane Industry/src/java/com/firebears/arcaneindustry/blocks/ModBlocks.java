package com.firebears.arcaneindustry.blocks;

import com.firebears.arcaneindustry.items.ModItems;

import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlocks {

	public static CrystalTileEntityBlock crystalTileEntityBlock;
	
	public static ModBlockOre rubyOre;
	public static ModBlockOre sapphireOre;
	public static ModBlockOre peridotOre;
	
	public static void createBlocks() {
		// crystal tile entity block
		crystalTileEntityBlock = GameRegistry.register(new CrystalTileEntityBlock("crystal_tile_entity_block"));
		
		// Ores
		rubyOre = GameRegistry.register(new ModBlockOre("rubyOre", Material.rock, ModItems.ruby, 1, 6, 1));
		sapphireOre = GameRegistry.register(new ModBlockOre("sapphireOre", Material.rock, ModItems.sapphire, 1, 4, 2));
		peridotOre = GameRegistry.register(new ModBlockOre("peridotOre", Material.rock, ModItems.peridot, 1, 2, 3));
	}
}
