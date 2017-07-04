package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.util.resource
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
    lateinit var conveyorBelt: BlockBase private set
    lateinit var inserter: BlockBase private set
    lateinit var waterGenerator: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        box = builder.withName("box").copy {
            material = Material.WOOD
            factory = factoryOf(::TileBox)
            onActivated = CommonMethods::openGui
        }.build()

        crushingTable = builder.withName("crushing_table").copy {
            factory = factoryOf(::TileCrushingTable)
            material = Material.ROCK
            customModels = listOf("normal" to resource("models/block/mcx/crushing_table.mcx"))
            hasCustomModel = true
            //methods
            boundingBox = { vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.875, 1) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        conveyorBelt = builder.withName("conveyor_belt").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileConveyorBelt)
            customModels = listOf(
                    "model" to resource("models/block/mcx/conveyor_belt.mcx"),
                    "inventory" to resource("models/block/mcx/conveyor_belt.mcx")
            )
            hasCustomModel = true
            generateDefaultItemModel = false
            alwaysDropDefault = true
            //methods
            boundingBox = { vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.8125, 1) }
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::delegateToModule
        }.build()

        inserter = builder.withName("inserter").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileInserter)
            customModels = listOf(
                    "model" to resource("models/block/mcx/inserter.mcx"),
                    "inventory" to resource("models/block/mcx/inserter.mcx")
            )
            hasCustomModel = true
            generateDefaultItemModel = false
            alwaysDropDefault = true
            //methods
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        waterGenerator = builder.withName("water_generator").copy {
            factory = factoryOf(::TileWaterGenerator)
        }.build()

        return itemBlockListOf(box, crushingTable, conveyorBelt, inserter, waterGenerator)
    }
}
