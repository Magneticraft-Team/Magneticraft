package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.TileCopperTank
import com.cout970.magneticraft.tileentity.TileIronPipe
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.scale
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

/**
 * Created by cout970 on 2017/08/28.
 */
object FluidMachines : IBlockMaker {

    lateinit var copperTank: BlockBase private set
    lateinit var ironPipe: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        copperTank = builder.withName("copper_tank").copy {
            factory = factoryOf(::TileCopperTank)
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/copper_tank.mcx"),
                    "inventory" to resource("models/block/mcx/copper_tank.mcx")
            )
            onActivated = CommonMethods::delegateToModule
            boundingBox = { listOf((vec3Of(2, 0, 2) toAABBWith vec3Of(14, 16, 14)).scale(PIXEL)) }
        }.build()

        ironPipe = builder.withName("iron_pipe").copy {
            factory = factoryOf(::TileIronPipe)
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/iron_pipe.mcx"),
                    "inventory" to resource("models/block/mcx/iron_pipe.mcx")
            )
            onActivated = CommonMethods::delegateToModule
        }.build()

        return itemBlockListOf(copperTank, ironPipe)
    }
}