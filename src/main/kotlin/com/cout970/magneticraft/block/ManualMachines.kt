package com.cout970.magneticraft.block

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.tileentity.TileBox
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileSluiceBox
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.toBlockPos
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/06/12.
 */
object ManualMachines : IBlockMaker {

    lateinit var box: BlockBase private set
    lateinit var crushingTable: BlockBase private set
    lateinit var sluiceBox: BlockBase private set

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
            forceModelBake = true
            customModels = listOf("normal" to resource("models/block/mcx/crushing_table.mcx"))
            hasCustomModel = true
            //methods
            boundingBox = { listOf(vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.875, 1)) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        sluiceBox = builder.withName("sluice_box").copy {
            states = CommonMethods.CenterOrientation.values().toList()
            factory = factoryOf(::TileSluiceBox)
            factoryFilter = { it[CommonMethods.PROPERTY_CENTER_ORIENTATION]?.center ?: false }
            customModels = listOf(
                    "model" to resource("models/block/mcx/sluice_box.mcx"),
                    "inventory" to resource("models/block/mcx/sluice_box_inv.mcx"),
                    "water" to resource("models/block/mcx/sluice_box_water.mcx")
            )
            hasCustomModel = true
            generateDefaultItemModel = false
            alwaysDropDefault = true
            //methods
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::delegateToModule
            blockStatesToPlace = {
                val facing = it.player.horizontalFacing
                val center = CommonMethods.CenterOrientation.of(facing, true)
                val noCenter = CommonMethods.CenterOrientation.of(facing.opposite, false)

                val thisState = it.default.withProperty(CommonMethods.PROPERTY_CENTER_ORIENTATION, center)
                val otherState = it.default.withProperty(CommonMethods.PROPERTY_CENTER_ORIENTATION, noCenter)

                listOf(BlockPos.ORIGIN to thisState, facing.toBlockPos() to otherState)
            }
            onBlockBreak = func@ {
                val facing = it.state[CommonMethods.PROPERTY_CENTER_ORIENTATION]?.facing ?: return@func
                it.worldIn.destroyBlock(it.pos + facing.toBlockPos(), true)
            }
            onDrop = {
                val center = it.state[CommonMethods.PROPERTY_CENTER_ORIENTATION]?.center ?: false
                if (center) it.default else emptyList()
            }
            boundingBox = {
                val center = it.state[CommonMethods.PROPERTY_CENTER_ORIENTATION]?.center ?: false
                if (center) listOf(Block.FULL_BLOCK_AABB) else listOf(AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0))
            }
        }.build()

        return itemBlockListOf(box, crushingTable, sluiceBox)
    }
}
