package com.firebears.arcaneindustry.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlockOre extends Block {

	private Item drop;
	private int meta;
	private int least_quantity;
	private int most_quantity;
	
	protected ModBlockOre(String unlocalizedName, Material mat, Item drop, int meta, int least_quantity, int most_quantity,
			int harvestLevel) {
		super(mat);
		this.drop = drop;
		this.meta = meta;
		this.least_quantity = least_quantity;
		this.most_quantity = most_quantity;
		this.setHarvestLevel("pickaxe", harvestLevel);
		this.setHardness(3.0f);
		this.setResistance(15.0f);
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(unlocalizedName);
		this.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.register((new ItemBlock(this).setRegistryName(unlocalizedName)));
	}
	
	protected ModBlockOre(String unlocalizedName, Material mat, int meta, Item drop, int least_quantity, int most_quantity, int harvestLevel) {
		this(unlocalizedName, mat, drop, harvestLevel, meta, least_quantity, most_quantity);
	}
	
	@Override
	public Item getItemDropped(IBlockState blockstate, Random random, int fortune) {
		return this.drop;
	}
	
	@Override
	public int damageDropped(IBlockState blockstate) {
		return this.meta;
	}
	
	@Override
	public int quantityDropped(IBlockState blockstate, int fortune, Random random) {
		if (this.least_quantity >= this.most_quantity)
			return this.least_quantity;
		return this.least_quantity + random.nextInt(this.most_quantity - this.least_quantity + fortune + 1);
	}
}
