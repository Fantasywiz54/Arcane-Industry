package com.firebears.arcaneindustry.handler;

import com.firebears.arcaneindustry.client.gui.GuiCrystalTileEntity;

import com.firebears.arcaneindustry.client.gui.ContainerCrystalTileEntity;
import com.firebears.arcaneindustry.tileentity.CrystalTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {

	public static final int CrystalTileEntityGui = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == CrystalTileEntityGui) {
			return new ContainerCrystalTileEntity(player.inventory, (CrystalTileEntity)world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == CrystalTileEntityGui) {
			return new GuiCrystalTileEntity(player.inventory, (CrystalTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}
}
