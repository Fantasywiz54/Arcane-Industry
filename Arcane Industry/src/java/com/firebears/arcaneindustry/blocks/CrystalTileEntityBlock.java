package com.firebears.arcaneindustry.blocks;

import com.firebears.arcaneindustry.Main;
import com.firebears.arcaneindustry.handler.ModGuiHandler;
import com.firebears.arcaneindustry.tileentity.CrystalTileEntity;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CrystalTileEntityBlock extends ModBlock implements ITileEntityProvider {
	
	public CrystalTileEntityBlock(String name) {
		super(Material.iron);
		
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(2.0f);
		this.setResistance(6.0f);
		this.setHarvestLevel("pickaxe", 2);
		this.isBlockContainer = true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new CrystalTileEntity();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate) {
		CrystalTileEntity te = (CrystalTileEntity) world.getTileEntity(pos);
		InventoryHelper.dropInventoryItems(world, pos, te);
		super.breakBlock(world, pos, blockstate);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			((CrystalTileEntity) world.getTileEntity(pos)).setCustomName(stack.getDisplayName());
		}
	}
	
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(Main.instance, ModGuiHandler.CrystalTileEntityGui, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
        return true;
    }

}
