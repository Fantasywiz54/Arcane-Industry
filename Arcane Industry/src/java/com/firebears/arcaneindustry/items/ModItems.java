package com.firebears.arcaneindustry.items;

import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModItems {

	public static CrystalMatrixItem crystalMatrix;
	
	public static void createItems() {
		crystalMatrix = GameRegistry.register(new CrystalMatrixItem("crystal_matrix"));
	}
}
