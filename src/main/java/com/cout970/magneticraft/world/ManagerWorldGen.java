package com.cout970.magneticraft.world;

import com.cout970.magneticraft.ManagerBlocks;
import net.darkaqua.blacksmith.api.util.Vect2i;
import net.darkaqua.blacksmith.api.world.IIChunkProvider;
import net.darkaqua.blacksmith.api.world.IWorld;
import net.darkaqua.blacksmith.api.world.generation.IWorldGeneratorDefinition;
import net.darkaqua.blacksmith.api.world.generation.OreGenerator;

import java.util.Random;

/**
 * Created by cout970 on 18/12/2015.
 */
public class ManagerWorldGen implements IWorldGeneratorDefinition{

    private OreGenerator copper;
    private OreGenerator tungsten;

    public ManagerWorldGen(){
        copper = new OreGenerator(ManagerBlocks.CopperOre.getBlock(), 8);
        tungsten = new OreGenerator(ManagerBlocks.TungstenOre.getBlock(), 5);
    }

    @Override
    public void generateChunk(IWorld world, IIChunkProvider chunkGenerator, IIChunkProvider chunkProvider, Random chunkRandom, Vect2i chunkPos) {
        generateChunkOres(copper, world, chunkPos, chunkRandom, 20, 100, 0);
        generateChunkOres(tungsten, world, chunkPos, chunkRandom, 20, 100, 0);
    }

    public void generateChunkOres(OreGenerator gen, IWorld world, Vect2i pos, Random random, int veins, int maxHeight, int minHeight){
        for (int k = 0; k < veins; k++) {
            int x = pos.getX() * 16 + random.nextInt(16);
            int y = minHeight + random.nextInt(maxHeight - minHeight);
            int z = pos.getY() * 16 + random.nextInt(16);
            gen.generate(world, random, x, y, z);
        }
    }
}
