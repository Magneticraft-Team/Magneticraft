package com.cout970.magneticraft.block

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.TileCombustionChamber
import com.cout970.magneticraft.tileentity.TileHeatPipe
import com.cout970.magneticraft.tileentity.TileSteamBoiler
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.px
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.toCelsius
import com.cout970.magneticraft.util.vector.*
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
object HeatMachines : IBlockMaker {

    lateinit var combustionChamber: BlockBase private set
    lateinit var steamBoiler: BlockBase private set
    lateinit var heatPipe: BlockBase private set

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
            generateDefaultItemModel = false
            alwaysDropDefault = true
            //methods
            boundingBox = CommonMethods.updateBoundingBoxWithOrientation {
                listOf(
                        (vec3Of(0, 0, 0) toAABBWith vec3Of(16, 12, 15)).scale(PIXEL),
                        (vec3Of(0, 12, 0) toAABBWith vec3Of(16, 16, 16)).scale(PIXEL),
                        (vec3Of(3, 2, 15) toAABBWith vec3Of(13, 10, 16)).scale(PIXEL)
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
            generateDefaultItemModel = false
            hasCustomModel = true
            //methods
            boundingBox = { listOf((vec3Of(1, 0, 1) toAABBWith vec3Of(15, 16, 15)).scale(PIXEL)) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        heatPipe = builder.withName("heat_pipe").copy {
            factory = factoryOf(::TileHeatPipe)
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/iron_pipe.mcx"),
                    "inventory" to resource("models/block/mcx/iron_pipe.mcx")
            )
            boundingBox = { pipeBoundingBox(it.source, it.pos) }
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

        return itemBlockListOf(combustionChamber, steamBoiler, heatPipe)
    }

    fun pipeBoundingBox(world: IBlockAccess, pos: BlockPos): List<AABB> {
        val (x, y, z) = pos

        val renderUp = checkPipe(world, BlockPos(x, y + 1, z), EnumFacing.DOWN)
        val renderDown = checkPipe(world, BlockPos(x, y - 1, z), EnumFacing.UP)
        val renderSouth = checkPipe(world, BlockPos(x, y, z + 1), EnumFacing.NORTH)
        val renderNorth = checkPipe(world, BlockPos(x, y, z - 1), EnumFacing.SOUTH)
        val renderEast = checkPipe(world, BlockPos(x + 1, y, z), EnumFacing.WEST)
        val renderWest = checkPipe(world, BlockPos(x - 1, y, z), EnumFacing.EAST)

        val list = mutableListOf<AABB>()

        list += vec3Of(4.px) toAABBWith vec3Of(1 - 4.px)

        if (renderDown) list += vec3Of(4.px, 0, 4.px) toAABBWith vec3Of(1 - 4.px, 4.px, 1 - 4.px)
        if (renderUp) list += vec3Of(4.px, 1 - 4.px, 4.px) toAABBWith vec3Of(1 - 4.px, 1, 1 - 4.px)

        if (renderNorth) list += vec3Of(4.px, 4.px, 0) toAABBWith vec3Of(1 - 4.px, 1 - 4.px, 4.px)
        if (renderSouth) list += vec3Of(4.px, 4.px, 1 - 4.px) toAABBWith vec3Of(1 - 4.px, 1 - 4.px, 1)

        if (renderWest) list += vec3Of(0, 4.px, 4.px) toAABBWith vec3Of(4.px, 1 - 4.px, 1 - 4.px)
        if (renderEast) list += vec3Of(1 - 4.px, 4.px, 4.px) toAABBWith vec3Of(1, 1 - 4.px, 1 - 4.px)

        return list
    }

    fun checkPipe(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos) ?: return false
        return tile.getOrNull(HEAT_NODE_HANDLER!!, facing) != null
    }
}