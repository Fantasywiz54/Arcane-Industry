package com.firebears.arcaneindustry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION)
public class Main {
	public static final String MODID = "arcaneindustry";
	public static final String MODNAME = "Arcane Industry";
	public static final String VERSION = "1.0.0";
	

	@SidedProxy(clientSide="com.firebears.arcaneindustry.ClientProxy", serverSide="com.firebears.arcaneindustry.ServerProxy")
	public static CommonProxy proxy;
	
	@Instance
	public static Main instance = new Main();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		Main.proxy.preInit(e);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		Main.proxy.init(e);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		Main.proxy.postInit(e);
	}
}
