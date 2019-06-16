package com.cout970.magneticraft.features.automatic_machines

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticUtils
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.blocks.BlockBase
import com.cout970.magneticraft.systems.blocks.BlockBuilder
import com.cout970.magneticraft.systems.blocks.CommonMethods
import com.cout970.magneticraft.systems.blocks.IBlockMaker
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import com.cout970.magneticraft.systems.tilerenderers.px
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by cout970 on 2017/08/10.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    lateinit var feedingTrough: BlockBase private set
    lateinit var conveyorBelt: BlockBase private set
    lateinit var inserter: BlockBase private set
    lateinit var waterGenerator: BlockBase private set
    lateinit var pneumaticTube: BlockBase private set
    lateinit var pneumaticRestrictionTube: BlockBase private set
    lateinit var relay: BlockBase private set
    lateinit var filter: BlockBase private set
    lateinit var transposer: BlockBase private set

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
                "up_base" to resource("models/block/gltf/conveyor_belt_up_base.gltf"),
                "up_anim" to resource("models/block/gltf/conveyor_belt_up_anim.gltf"),
                "down_base" to resource("models/block/gltf/conveyor_belt_down_base.gltf"),
                "down_anim" to resource("models/block/gltf/conveyor_belt_down_anim.gltf"),
                "inventory" to resource("models/block/mcx/conveyor_belt.mcx")
            )
            hasCustomModel = true
            generateDefaultItemBlockModel = false
            alwaysDropDefault = true
            //methods
            boundingBox = { listOf(vec3Of(0, 0, 0) createAABBUsing vec3Of(1, 0.8125, 1)) }
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
            generateDefaultItemBlockModel = false
            alwaysDropDefault = true
            boundingBox = { listOf(vec3Of(0, 0, 0) createAABBUsing vec3Of(16.px, 3.px, 16.px)) }
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
            boundingBox = { listOf((vec3Of(0, 0, 0) createAABBUsing vec3Of(16, 12, 16)).scale(PIXEL)) }
        }.build()

        pneumaticTube = builder.withName("pneumatic_tube").copy {
            factory = factoryOf(::TilePneumaticTube)
            customModels = listOf(
                "model" to resource("models/block/gltf/pneumatic_tube.gltf"),
                "inventory" to resource("models/block/gltf/pneumatic_tube_inv.gltf")
            )
            hasCustomModel = true
            generateDefaultItemBlockModel = false
            boundingBox = { pneumaticTubeBoundingBox(it.source, it.pos) }
            onActivated = CommonMethods::delegateToModule
            onBlockPostPlaced = {
                if (it.world.isClient){
                    EnumFacing.values().forEach {side ->
                        it.world.getTile<AbstractTileTube>(it.pos.offset(side))?.connections?.clear()
                    }
                }
            }
        }.build()

        pneumaticRestrictionTube = builder.withName("pneumatic_restriction_tube").copy {
            factory = factoryOf(::TilePneumaticRestrictionTube)
            customModels = listOf(
                "model" to resource("models/block/gltf/pneumatic_restriction_tube.gltf"),
                "inventory" to resource("models/block/gltf/pneumatic_restriction_tube_inv.gltf")
            )
            hasCustomModel = true
            generateDefaultItemBlockModel = false
            boundingBox = { pneumaticTubeBoundingBox(it.source, it.pos) }
            onActivated = CommonMethods::delegateToModule
            onBlockPostPlaced = {
                if (it.world.isClient){
                    EnumFacing.values().forEach {side ->
                        it.world.getTile<AbstractTileTube>(it.pos.offset(side))?.connections?.clear()
                    }
                }
            }
        }.build()

        relay = builder.withName("relay").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileRelay)
            alwaysDropDefault = true
            onBlockPlaced = CommonMethods::placeWithOppositeFacing
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        filter = builder.withName("filter").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileFilter)
            alwaysDropDefault = true
            onBlockPlaced = CommonMethods::placeWithOppositeFacing
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        transposer = builder.withName("transposer").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileTransposer)
            alwaysDropDefault = true
            onBlockPlaced = CommonMethods::placeWithOppositeFacing
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        return itemBlockListOf(conveyorBelt, inserter, waterGenerator, feedingTrough, pneumaticTube, pneumaticRestrictionTube, relay, filter, transposer)
    }

    fun pneumaticTubeBoundingBox(world: IBlockAccess, pos: BlockPos): List<AABB> {
        val list = mutableListOf<AABB>()

        list += vec3Of(4.px) createAABBUsing vec3Of(1 - 4.px)

        pneumaticTubeSides(world, pos).forEach {
            list += it.second
        }

        return list
    }

    fun pneumaticTubeSides(world: IBlockAccess, pos: BlockPos): List<Pair<EnumFacing, AABB>> {
        val list = mutableListOf<Pair<EnumFacing, AABB>>()

        if (PneumaticUtils.canConnectToTube(world, pos, EnumFacing.DOWN))
            list += EnumFacing.DOWN to (vec3Of(4.px, 0, 4.px) createAABBUsing vec3Of(1 - 4.px, 4.px, 1 - 4.px))

        if (PneumaticUtils.canConnectToTube(world, pos, EnumFacing.UP))
            list += EnumFacing.UP to (vec3Of(4.px, 1 - 4.px, 4.px) createAABBUsing vec3Of(1 - 4.px, 1, 1 - 4.px))

        if (PneumaticUtils.canConnectToTube(world, pos, EnumFacing.NORTH))
            list += EnumFacing.NORTH to (vec3Of(4.px, 4.px, 0) createAABBUsing vec3Of(1 - 4.px, 1 - 4.px, 4.px))

        if (PneumaticUtils.canConnectToTube(world, pos, EnumFacing.SOUTH))
            list += EnumFacing.SOUTH to (vec3Of(4.px, 4.px, 1 - 4.px) createAABBUsing vec3Of(1 - 4.px, 1 - 4.px, 1))

        if (PneumaticUtils.canConnectToTube(world, pos, EnumFacing.WEST))
            list += EnumFacing.WEST to (vec3Of(0, 4.px, 4.px) createAABBUsing vec3Of(4.px, 1 - 4.px, 1 - 4.px))

        if (PneumaticUtils.canConnectToTube(world, pos, EnumFacing.EAST))
            list += EnumFacing.EAST to (vec3Of(1 - 4.px, 4.px, 4.px) createAABBUsing vec3Of(1, 1 - 4.px, 1 - 4.px))

        return list
    }
}