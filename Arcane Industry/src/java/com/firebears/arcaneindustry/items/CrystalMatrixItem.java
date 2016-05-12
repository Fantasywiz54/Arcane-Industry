package com.firebears.arcaneindustry.items;

import java.util.ArrayList;
import java.util.List;

import com.firebears.arcaneindustry.blocks.CrystalTileEntityBlock;
import com.firebears.arcaneindustry.tileentity.CrystalTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrystalMatrixItem extends ModItem {
	
	BlockPos linkStartPos;
	BlockPos linkEndPos;

	public CrystalMatrixItem(String unlocalizedName) {
		super(unlocalizedName);
		
		setMaxStackSize(1);
	}

	@Override
	 public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (linkStartPos == null) {
				// the start of the link must be on a crystal
				if (worldIn.getBlockState(pos).getBlock() instanceof CrystalTileEntityBlock) {
					linkStartPos = pos;
					System.out.println("Start Link at: " + linkStartPos);
				}
			} else {
				// the end of the link can be a crystal or an inventory
				TileEntity te = worldIn.getTileEntity(pos);
				
				if (te instanceof CrystalTileEntity) {
					if (linkStartPos != pos) {
						linkEndPos = pos;
						System.out.println("End Link at:" + linkEndPos);
						
						CrystalTileEntity cte;
						TileEntity crystal = worldIn.getTileEntity(linkEndPos);
						
						if (crystal instanceof CrystalTileEntity) {
							cte = (CrystalTileEntity)crystal;
							
							cte.addChildCrystal(linkEndPos);
							
							ArrayList<BlockPos> links = cte.getChildCrystals();
							for (int i = 0; i < links.size(); i++) {
								System.out.println("Linked Crystal " + "#" + i + " " + ": " + new BlockPos(links.get(i).getX(), links.get(i).getY(), links.get(i).getZ()));
							}
	
							linkStartPos = null;
							linkEndPos = null;
						}
					}
				} else if (te instanceof IInventory) {
					if (linkStartPos != pos) {
						linkEndPos = pos;
						System.out.println("End Link at: " + linkEndPos);
	
						CrystalTileEntity cte;
						TileEntity crystal = worldIn.getTileEntity(linkStartPos);
						
						if (crystal instanceof CrystalTileEntity) {
							cte = (CrystalTileEntity)crystal;
							
							cte.setLinkedInventory(linkEndPos);
							System.out.println("Linked Inventory: " + cte.getLinkedInventory());
							
							linkStartPos = null;
							linkEndPos = null;
						}
					}
				}
			}
		}
		
        return EnumActionResult.PASS;
    }
	
	@Override
	 public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add("Right click on a crystal to start a link");
		tooltip.add("Right click on a crystal or a block with an inventory to finish a link");
    }
}
