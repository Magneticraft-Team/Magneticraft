package com.cout970.magneticraft.block

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.TileIronPipe
import com.cout970.magneticraft.tileentity.TileSmallTank
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModulePipe
import com.cout970.magneticraft.tileentity.modules.pipe.PipeType
import com.cout970.magneticraft.tilerenderer.core.px
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.*
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess


/**
 * Created by cout970 on 2017/08/28.
 */
object FluidMachines : IBlockMaker {

    lateinit var smallTank: BlockBase private set
    lateinit var ironPipe: BlockBase private set

    override fun initBlocks(): List<Pair<Block, ItemBlock>> {
        val builder = BlockBuilder().apply {
            material = Material.IRON
            creativeTab = CreativeTabMg
        }

        smallTank = builder.withName("small_tank").copy {
            factory = factoryOf(::TileSmallTank)
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/small_tank.mcx"),
                    "inventory" to resource("models/block/mcx/small_tank.mcx")
            )
            onActivated = CommonMethods::delegateToModule
        }.build()

        ironPipe = builder.withName("iron_pipe").copy {
            factory = factoryOf(::TileIronPipe)
            generateDefaultItemModel = false
            hasCustomModel = true
            customModels = listOf(
                    "model" to resource("models/block/mcx/fluid_pipe.mcx"),
                    "inventory" to resource("models/block/mcx/fluid_pipe.mcx")
            )
            boundingBox = { pipeBoundingBox(it.source, it.pos) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        return itemBlockListOf(smallTank, ironPipe)
    }

    fun pipeBoundingBox(world: IBlockAccess, pos: BlockPos): List<AABB> {
        val (x, y, z) = pos

        val renderUp = checkPipeOrTank(world, BlockPos(x, y + 1, z), EnumFacing.DOWN)
        val renderDown = checkPipeOrTank(world, BlockPos(x, y - 1, z), EnumFacing.UP)
        val renderSouth = checkPipeOrTank(world, BlockPos(x, y, z + 1), EnumFacing.NORTH)
        val renderNorth = checkPipeOrTank(world, BlockPos(x, y, z - 1), EnumFacing.SOUTH)
        val renderEast = checkPipeOrTank(world, BlockPos(x + 1, y, z), EnumFacing.WEST)
        val renderWest = checkPipeOrTank(world, BlockPos(x - 1, y, z), EnumFacing.EAST)

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

    fun pipeBoundingBox2(world: IBlockAccess, pos: BlockPos): List<Pair<EnumFacing, AABB>> {
        val (x, y, z) = pos

        val renderUp = checkPipeOrTank(world, BlockPos(x, y + 1, z), EnumFacing.DOWN)
        val renderDown = checkPipeOrTank(world, BlockPos(x, y - 1, z), EnumFacing.UP)
        val renderSouth = checkPipeOrTank(world, BlockPos(x, y, z + 1), EnumFacing.NORTH)
        val renderNorth = checkPipeOrTank(world, BlockPos(x, y, z - 1), EnumFacing.SOUTH)
        val renderEast = checkPipeOrTank(world, BlockPos(x + 1, y, z), EnumFacing.WEST)
        val renderWest = checkPipeOrTank(world, BlockPos(x - 1, y, z), EnumFacing.EAST)

        val list = mutableListOf<Pair<EnumFacing, AABB>>()

        if (renderDown) list += EnumFacing.DOWN to (vec3Of(4.px, 0, 4.px) toAABBWith vec3Of(1 - 4.px, 4.px, 1 - 4.px))
        if (renderUp) list += EnumFacing.UP to (vec3Of(4.px, 1 - 4.px, 4.px) toAABBWith vec3Of(1 - 4.px, 1, 1 - 4.px))
        if (renderNorth) list += EnumFacing.NORTH to (vec3Of(4.px, 4.px, 0) toAABBWith vec3Of(1 - 4.px, 1 - 4.px, 4.px))
        if (renderSouth) list += EnumFacing.SOUTH to (vec3Of(4.px, 4.px, 1 - 4.px) toAABBWith vec3Of(1 - 4.px, 1 - 4.px, 1))
        if (renderWest) list += EnumFacing.WEST to (vec3Of(0, 4.px, 4.px) toAABBWith vec3Of(4.px, 1 - 4.px, 1 - 4.px))
        if (renderEast) list += EnumFacing.EAST to (vec3Of(1 - 4.px, 4.px, 4.px) toAABBWith vec3Of(1, 1 - 4.px, 1 - 4.px))

        return list
    }

    fun checkPipeOrTank(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean {
        val tile = world.getTileEntity(pos) ?: return false
        return tile.getOrNull(FLUID_HANDLER!!, facing) != null ||
                (tile is TileBase && tile.getModule<ModulePipe>()?.type == PipeType.IRON)
    }
}