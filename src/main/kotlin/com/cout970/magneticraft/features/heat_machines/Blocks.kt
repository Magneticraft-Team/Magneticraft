package com.cout970.magneticraft.features.heat_machines

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.toCelsius
import com.cout970.magneticraft.misc.vector.createAABBUsing
import com.cout970.magneticraft.misc.vector.rotateBox
import com.cout970.magneticraft.misc.vector.scale
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.blocks.BlockBase
import com.cout970.magneticraft.systems.blocks.BlockBuilder
import com.cout970.magneticraft.systems.blocks.CommonMethods
import com.cout970.magneticraft.systems.blocks.IBlockMaker
import com.cout970.magneticraft.systems.itemblocks.itemBlockListOf
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.ModuleHeatPipeConnections
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import com.cout970.magneticraft.systems.tilerenderers.px
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBlock
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by cout970 on 2017/08/10.
 */
@RegisterBlocks
object Blocks : IBlockMaker {

    lateinit var combustionChamber: BlockBase private set
    lateinit var steamBoiler: BlockBase private set
    lateinit var heatPipe: BlockBase private set
    lateinit var insulatedHeatPipe: BlockBase private set
    lateinit var heatSink: BlockBase private set
    lateinit var gasificationUnit: BlockBase private set
    lateinit var brickFurnace: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        combustionChamber = builder.withName("combustion_chamber").copy {
            states = CommonMethods.Orientation.values().toList()
            factory = factoryOf(::TileCombustionChamber)
            customModels = listOf(
                "model" to resource("models/block/mcx/combustion_chamber.mcx"),
                "inventory" to resource("models/block/mcx/combustion_chamber.mcx")
            )
            hasCustomModel = true
            generateDefaultItemBlockModel = false
            alwaysDropDefault = true
            //methods
            boundingBox = CommonMethods.updateBoundingBoxWithOrientation {
                listOf(
                    (vec3Of(0, 0, 0) createAABBUsing vec3Of(16, 12, 15)).scale(PIXEL),
                    (vec3Of(0, 12, 0) createAABBUsing vec3Of(16, 16, 16)).scale(PIXEL),
                    (vec3Of(3, 2, 15) createAABBUsing vec3Of(13, 10, 16)).scale(PIXEL)
                )
            }
            onBlockPlaced = CommonMethods::placeWithOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::delegateToModule
        }.build()

        steamBoiler = builder.withName("steam_boiler").copy {
            factory = factoryOf(::TileSteamBoiler)
            customModels = listOf(
                "model" to resource("models/block/mcx/steam_boiler.mcx"),
                "inventory" to resource("models/block/mcx/steam_boiler.mcx")
            )
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            //methods
            boundingBox = { listOf((vec3Of(1, 0, 1) createAABBUsing vec3Of(15, 16, 15)).scale(PIXEL)) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        heatPipe = builder.withName("heat_pipe").copy {
            factory = factoryOf(::TileHeatPipe)
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                "model" to resource("models/block/mcx/iron_pipe.mcx"),
                "inventory" to resource("models/block/mcx/iron_pipe_dark.mcx")
            )
            boundingBox = { heatPipeBoundingBox(it.source, it.pos, 4) }
            onActivated = CommonMethods::delegateToModule
            onEntityCollidedWithBlock = func@{
                val entity = it.entityIn as? EntityLivingBase ?: return@func
                val tile = it.worldIn.getTile<TileHeatPipe>(it.pos) ?: return@func
                val temp = tile.heatNode.temperature.toCelsius()

                val damage = when {
                    temp < 80 -> return@func
                    temp > 80 -> 0.5f
                    temp > 150 -> 1.0f
                    temp > 250 -> 1.5f
                    temp > 500 -> 2.0f
                    temp > 750 -> 2.5f
                    temp > 1000 -> 3.0f
                    temp > 1250 -> 3.5f
                    temp > 1500 -> 4.0f
                    temp > 2000 -> 4.5f
                    else -> 5.0f
                }

                entity.attackEntityFrom(DamageSource.ON_FIRE, damage * 4)
            }
        }.build()

        insulatedHeatPipe = builder.withName("insulated_heat_pipe").copy {
            factory = factoryOf(::TileInsulatedHeatPipe)
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                "model" to resource("models/block/mcx/insulated_heat_pipe.mcx"),
                "inventory" to resource("models/block/mcx/insulated_heat_pipe.mcx")
            )
            boundingBox = { heatPipeBoundingBox(it.source, it.pos, 3) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        heatSink = builder.withName("heat_sink").copy {
            states = CommonMethods.Facing.values().toList()
            factory = factoryOf(::TileHeatSink)
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                "model" to resource("models/block/mcx/heat_sink.mcx"),
                "inventory" to resource("models/block/mcx/heat_sink.mcx")
            )
            alwaysDropDefault = true
            onBlockPlaced = CommonMethods::placeWithOppositeFacing
            pickBlock = CommonMethods::pickDefaultBlock
            boundingBox = {
                val f = it.state[CommonMethods.PROPERTY_FACING] ?: CommonMethods.Facing.DOWN
                listOf(f.facing.rotateBox(vec3Of(0.5), AABB(0.0, 0.0, 0.0, 1.0, 1.0, 5f.px)))
            }
        }.build()

        gasificationUnit = builder.withName("gasification_unit").copy {
            factory = factoryOf(::TileGasificationUnit)
            customModels = listOf(
                "model" to resource("models/block/mcx/gasification_unit.mcx"),
                "inventory" to resource("models/block/mcx/gasification_unit.mcx")
            )
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            boundingBox = { listOf((vec3Of(1, 0, 1) createAABBUsing vec3Of(15, 16, 15)).scale(PIXEL)) }
            onActivated = CommonMethods::delegateToModule
            pickBlock = CommonMethods::pickDefaultBlock
        }.build()

        brickFurnace = builder.withName("brick_furnace").copy {
            material = Material.ROCK
            states = CommonMethods.OrientationActive.values().toList()
            factory = factoryOf(::TileBrickFurnace)
            alwaysDropDefault = true
            hasCustomModel = true
            //methods
            onBlockPlaced = CommonMethods::placeInactiveWithOppositeOrientation
            pickBlock = CommonMethods::pickDefaultBlock
            onActivated = CommonMethods::openGui
        }.build()

        return itemBlockListOf(combustionChamber, steamBoiler, heatPipe, insulatedHeatPipe, heatSink,
            gasificationUnit, brickFurnace)
    }

    // size is (8 - realSize)
    fun heatPipeBoundingBox(world: IBlockAccess, pos: BlockPos, size: Int): List<AABB> {
        val pipe = world.getTile<TileBase>(pos)?.getModule<ModuleHeatPipeConnections>()
        val list = mutableListOf<AABB>()

        list += vec3Of(size.px) createAABBUsing vec3Of(1 - size.px)

        if (pipe != null) {
            if (pipe.canConnect(EnumFacing.DOWN))
                list += vec3Of(size.px, 0, size.px) createAABBUsing vec3Of(1 - size.px, size.px, 1 - size.px)

            if (pipe.canConnect(EnumFacing.UP))
                list += vec3Of(size.px, 1 - size.px, size.px) createAABBUsing vec3Of(1 - size.px, 1, 1 - size.px)

            if (pipe.canConnect(EnumFacing.NORTH))
                list += vec3Of(size.px, size.px, 0) createAABBUsing vec3Of(1 - size.px, 1 - size.px, size.px)

            if (pipe.canConnect(EnumFacing.SOUTH))
                list += vec3Of(size.px, size.px, 1 - size.px) createAABBUsing vec3Of(1 - size.px, 1 - size.px, 1)

            if (pipe.canConnect(EnumFacing.WEST))
                list += vec3Of(0, size.px, size.px) createAABBUsing vec3Of(size.px, 1 - size.px, 1 - size.px)

            if (pipe.canConnect(EnumFacing.EAST))
                list += vec3Of(1 - size.px, size.px, size.px) createAABBUsing vec3Of(1, 1 - size.px, 1 - size.px)
        }
        return list
    }

}