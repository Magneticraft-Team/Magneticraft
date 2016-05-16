package com.cout970.magneticraft.world

import com.cout970.magneticraft.block.BlockOre
import com.cout970.magneticraft.block.states.BlockOreStates
import com.cout970.magneticraft.block.states.BlockProperties
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkGenerator
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

/**
 * Created by cout970 on 15/05/2016.
 */
object WorldGenerator : IWorldGenerator {

    lateinit var copperGenerator : OreGenerator
    lateinit var leadGenerator : OreGenerator
    lateinit var cobaltGenerator : OreGenerator
    lateinit var tungstenGenerator : OreGenerator

    fun init(){
        copperGenerator   = OreGenerator(BlockOre.defaultState.withProperty(BlockProperties.blockOreState, BlockOreStates.COPPER),   Config.copperOre.veinAmount)
        leadGenerator     = OreGenerator(BlockOre.defaultState.withProperty(BlockProperties.blockOreState, BlockOreStates.LEAD),     Config.leadOre.veinAmount)
        cobaltGenerator   = OreGenerator(BlockOre.defaultState.withProperty(BlockProperties.blockOreState, BlockOreStates.COBALT),   Config.cobaltOre.veinAmount)
        tungstenGenerator = OreGenerator(BlockOre.defaultState.withProperty(BlockProperties.blockOreState, BlockOreStates.TUNGSTEN), Config.tungstenOre.veinAmount)
    }

    override fun generate(random: Random?, chunkX: Int, chunkZ: Int, world: World?, chunkGenerator: IChunkGenerator?, chunkProvider: IChunkProvider?) {
        if(world == null || random == null)return
        generateChunkOres(copperGenerator, world, Vec2d(chunkX, chunkZ), random, Config.copperOre.chunkAmount, Config.copperOre.maxLevel, Config.copperOre.minLevel)
        generateChunkOres(leadGenerator, world, Vec2d(chunkX, chunkZ), random, Config.leadOre.chunkAmount, Config.leadOre.maxLevel, Config.leadOre.minLevel)
        generateChunkOres(cobaltGenerator, world, Vec2d(chunkX, chunkZ), random, Config.cobaltOre.chunkAmount, Config.cobaltOre.maxLevel, Config.cobaltOre.minLevel)
        generateChunkOres(tungstenGenerator, world, Vec2d(chunkX, chunkZ), random, Config.tungstenOre.chunkAmount, Config.tungstenOre.maxLevel, Config.tungstenOre.minLevel)
    }

    fun generateChunkOres(gen: OreGenerator, world: World, pos: Vec2d, random: Random, veins: Int, maxHeight: Int, minHeight: Int) {
        for (k in 0..veins - 1) {
            val x = pos.getXi() * 16 + random.nextInt(16)
            val y = minHeight + random.nextInt(maxHeight - minHeight)
            val z = pos.getYi() * 16 + random.nextInt(16)
            gen.generate(world, random, x, y, z)
        }
    }
}