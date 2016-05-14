package com.firebears.arcaneindustry.world;

import java.util.Random;

import com.firebears.arcaneindustry.blocks.ModBlocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class ArcaneIndustryWorldGen implements IWorldGenerator {

	// Ore Generators
	private WorldGenerator gen_ruby_ore;	// Generates ruby ore, lowest tier gem
	private WorldGenerator gen_sapphire_ore;// Generates sapphire ore, mid-tier gem
	private WorldGenerator gen_peridot_ore;	// Generates peridot ore, highest-tier gem
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator generator, IChunkProvider provider) {
		switch(world.provider.getDimension()) {
		case 0: // Overworld
			
			this.runGenerator(this.gen_ruby_ore, world, random, chunkX, chunkZ, 20, 0, 64);
			this.runGenerator(this.gen_sapphire_ore, world, random, chunkX, chunkZ, 20, 0, 32);
			this.runGenerator(this.gen_peridot_ore, world, random, chunkX, chunkZ, 20, 0, 16);
			
			break;
		case -1: // Nether
			
			break;
		case 1: // The End
			
			break;
		}
	}
	
	public ArcaneIndustryWorldGen() {
		this.gen_ruby_ore = new WorldGenMinable(ModBlocks.rubyOre.getDefaultState(), 8);
		this.gen_sapphire_ore = new WorldGenMinable(ModBlocks.sapphireOre.getDefaultState(), 8);
		this.gen_peridot_ore = new WorldGenMinable(ModBlocks.peridotOre.getDefaultState(), 8);
	}
	
	private void runGenerator(WorldGenerator generator, World world, Random rand,
			int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight) {
		if (minHeight < 0 || minHeight > maxHeight) {
			throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator!");
		}
		
		int heightDiff = maxHeight - minHeight + 1;
		for (int i = 0; i < chancesToSpawn; i++) {
			int x = chunk_X * 16 + rand.nextInt(16);
			int y = minHeight + rand.nextInt(heightDiff);
			int z = chunk_Z * 16 + rand.nextInt(16);
			generator.generate(world, rand, new BlockPos(x, y, z));
		}
	}
}
