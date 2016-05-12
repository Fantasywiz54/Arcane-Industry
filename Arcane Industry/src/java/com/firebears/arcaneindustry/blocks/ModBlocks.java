package com.firebears.arcaneindustry.blocks;

import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlocks {

	public static CrystalTileEntityBlock crystalTileEntityBlock;
	
	public static void createBlocks() {
		// crystal tile entity block
		crystalTileEntityBlock = GameRegistry.register(new CrystalTileEntityBlock("crystal_tile_entity_block"));		
	}
}
