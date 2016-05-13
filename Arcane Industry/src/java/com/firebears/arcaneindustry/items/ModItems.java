package com.firebears.arcaneindustry.items;

import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModItems {

	// Gems
	public static ModItem ruby;
	public static ModItem sapphire;
	public static ModItem peridot;
	
	public static void createItems() {
		// Gems
		ruby = GameRegistry.register(new ModItem("ruby"));
		sapphire = GameRegistry.register(new ModItem("sapphire"));
		peridot = GameRegistry.register(new ModItem("peridot"));
	}
}
