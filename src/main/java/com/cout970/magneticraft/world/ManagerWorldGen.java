package com.cout970.magneticraft.world;

import com.cout970.magneticraft.ManagerBlocks;
import net.darkaqua.blacksmith.vectors.Vect2i;
import net.darkaqua.blacksmith.world.OreGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Created by cout970 on 18/12/2015.
 */
public class ManagerWorldGen implements IWorldGenerator {

    private OreGenerator copper;
    private OreGenerator tungsten;

    public ManagerWorldGen() {
        copper = new OreGenerator(ManagerBlocks.CopperOre.getBlock(), 8);
        tungsten = new OreGenerator(ManagerBlocks.TungstenOre.getBlock(), 5);
    }

    public void generateChunkOres(OreGenerator gen, World world, Vect2i pos, Random random, int veins, int maxHeight, int minHeight) {
        for (int k = 0; k < veins; k++) {
            int x = pos.getX() * 16 + random.nextInt(16);
            int y = minHeight + random.nextInt(maxHeight - minHeight);
            int z = pos.getY() * 16 + random.nextInt(16);
            gen.generate(world, random, x, y, z);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        generateChunkOres(copper, world, new Vect2i(chunkX, chunkZ), random, 20, 100, 0);
        generateChunkOres(tungsten, world, new Vect2i(chunkX, chunkZ), random, 20, 100, 0);
    }
}
