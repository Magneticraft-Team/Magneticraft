package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tileentity.TileInserter
import com.cout970.magneticraft.tileentity.TileWaterGenerator
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.px
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/08/10.
 */
object AutomaticMachines : IBlockMaker {

    lateinit var feedingTrough: BlockBase private set
    lateinit var conveyorBelt: BlockBase private set
    lateinit var inserter: BlockBase private set
    lateinit var waterGenerator: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        conveyorBelt = builder.withName("conveyor_belt").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileConveyorBelt)
            customModels = listOf(
                    "base" to resource("models/block/mcx/conveyor_belt_base.mcx"),
                    "anim" to resource("models/block/mcx/conveyor_belt_anim.mcx"),
                    "corner_base" to resource("models/block/mcx/conveyor_belt_corner_base.mcx"),
                    "corner_anim" to resource("models/block/mcx/conveyor_belt_corner_anim.mcx"),
                    "inventory" to resource("models/block/mcx/conveyor_belt.mcx")
            )
            hasCustomModel = true
            generateDefaultItemModel = false
            alwaysDropDefault = true
            //methods
            boundingBox = { listOf(vec3Of(0, 0, 0) toAABBWith vec3Of(1, 0.8125, 1)) }
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::delegateToModule
        }.build()

        inserter = builder.withName("inserter").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileInserter)
            customModels = listOf(
                    "model" to resource("models/block/gltf/inserter.gltf"),
                    "inventory" to resource("models/block/gltf/inserter.gltf")
            )
            hasCustomModel = true
            generateDefaultItemModel = false
            alwaysDropDefault = true
            boundingBox = { listOf(vec3Of(0, 0, 0) toAABBWith vec3Of(16.px, 3.px, 16.px)) }
            //methods
            onBlockPlaced = CommonMethods::placeWithOppositeOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        waterGenerator = builder.withName("water_generator").copy {
            factory = factoryOf(::TileWaterGenerator)
            onActivated = CommonMethods::delegateToModule
        }.build()
        
        feedingTrough = builder.withName("feeding_trough").copy {
            material = Material.WOOD
            states = CommonMethods.CenterOrientation.values().toList()
            factory = factoryOf(::TileFeedingTrough)
            factoryFilter = { it[CommonMethods.PROPERTY_CENTER_ORIENTATION]?.center ?: false }
            customModels = listOf(
                    "model" to resource("models/block/mcx/feeding_trough.mcx"),
                    "inventory" to resource("models/block/mcx/feeding_trough_inv.mcx")
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
            boundingBox = { listOf((vec3Of(0, 0, 0) toAABBWith vec3Of(16, 12, 16)).scale(PIXEL)) }
        }.build()

        return itemBlockListOf(conveyorBelt, inserter, waterGenerator, feedingTrough)
    }
}