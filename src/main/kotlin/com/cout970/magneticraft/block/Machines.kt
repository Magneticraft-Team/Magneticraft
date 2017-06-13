package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileBox
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

/**
 * Created by cout970 on 2017/06/12.
 */
object Machines : IBlockMaker {

    lateinit var box: BlockBase private set
    lateinit var crushingTable: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.ROCK
            creativeTab = CreativeTabMg
        }

        box = builder.withName("box").apply {
            factory = { _, _ -> TileBox() }
            onActivated = BlockBuilder.openGui
        }.build()
        crushingTable = builder.withName("crushing_table").apply {
            factory = { _, _ -> TileCrushingTable() }
            onActivated = { it.worldIn.getTile<TileCrushingTable>(it.pos)?.crushingModule?.onActivated(it) ?: false }
            boundingBox = { vec3Of(0,0,0) toAABBWith vec3Of(1, 0.875, 1) }
            enableOcclusionOptimization = false
            translucent = true
        }.build()

        return itemBlockListOf(box, crushingTable)
    }
}