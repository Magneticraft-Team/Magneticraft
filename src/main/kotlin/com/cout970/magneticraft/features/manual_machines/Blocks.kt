package com.cout970.magneticraft.features.manual_machines

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.createAABBUsing
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.blocks.BlockBase
import com.cout970.magneticraft.systems.blocks.BlockBuilder
import com.cout970.magneticraft.systems.blocks.CommonMethods
import com.cout970.magneticraft.systems.blocks.IBlockMaker
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/06/12.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    lateinit var box: BlockBase private set
    lateinit var crushingTable: BlockBase private set
    lateinit var sluiceBox: BlockBase private set
    lateinit var fabricator: BlockBase private set

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
            boundingBox = { listOf(vec3Of(0, 0, 0) createAABBUsing vec3Of(1, 0.875, 1)) }
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
            generateDefaultItemBlockModel = false
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
            onBlockBreak = func@{
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

        fabricator = builder.withName("fabricator").copy {
            factory = factoryOf(::TileFabricator)
            onActivated = CommonMethods::openGui
        }.build()

        return itemBlockListOf(box, crushingTable, sluiceBox, fabricator)
    }
}
