package com.firebears.arcaneindustry.tileentity;

import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModTileEntities {

	public static void init() {
		GameRegistry.registerTileEntity(CrystalTileEntity.class, "crystal_tile_entity");
	}
}
