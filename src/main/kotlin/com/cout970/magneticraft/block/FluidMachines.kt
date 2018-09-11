package com.cout970.magneticraft.block

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.block.core.BlockBuilder
import com.cout970.magneticraft.block.core.CommonMethods
import com.cout970.magneticraft.block.core.IBlockMaker
import com.cout970.magneticraft.item.itemblock.itemBlockListOf
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileIronPipe
import com.cout970.magneticraft.tileentity.TileSmallTank
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModulePipe
import com.cout970.magneticraft.tilerenderer.core.px
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.createAABBUsing
import com.cout970.magneticraft.util.vector.vec3Of
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
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                "model" to resource("models/block/mcx/small_tank.mcx"),
                "inventory" to resource("models/block/mcx/small_tank.mcx")
            )
            onActivated = CommonMethods::delegateToModule
        }.build()

        ironPipe = builder.withName("iron_pipe").copy {
            factory = factoryOf(::TileIronPipe)
            generateDefaultItemBlockModel = false
            hasCustomModel = true
            customModels = listOf(
                "model" to resource("models/block/mcx/fluid_pipe.mcx"),
                "inventory" to resource("models/block/mcx/fluid_pipe.mcx")
            )
            boundingBox = { fluidPipeBoundingBox(it.source, it.pos) }
            onActivated = CommonMethods::delegateToModule
        }.build()

        return itemBlockListOf(smallTank, ironPipe)
    }

    fun fluidPipeBoundingBox(world: IBlockAccess, pos: BlockPos): List<AABB> {
        val list = mutableListOf<AABB>()

        list += vec3Of(4.px) createAABBUsing vec3Of(1 - 4.px)

        fluidPipeSides(world, pos).forEach {
            list += it.second
        }

        return list
    }

    fun fluidPipeSides(world: IBlockAccess, pos: BlockPos): List<Pair<EnumFacing, AABB>> {
        val pipe = world.getTile<TileBase>(pos)?.getModule<ModulePipe>() ?: return emptyList()
        val list = mutableListOf<Pair<EnumFacing, AABB>>()

        val none = ModulePipe.ConnectionType.NONE

        if (pipe.getConnectionType(EnumFacing.DOWN, false) != none)
            list += EnumFacing.DOWN to (vec3Of(4.px, 0, 4.px) createAABBUsing vec3Of(1 - 4.px, 4.px, 1 - 4.px))

        if (pipe.getConnectionType(EnumFacing.UP, false) != none)
            list += EnumFacing.UP to (vec3Of(4.px, 1 - 4.px, 4.px) createAABBUsing vec3Of(1 - 4.px, 1, 1 - 4.px))

        if (pipe.getConnectionType(EnumFacing.NORTH, false) != none)
            list += EnumFacing.NORTH to (vec3Of(4.px, 4.px, 0) createAABBUsing vec3Of(1 - 4.px, 1 - 4.px, 4.px))

        if (pipe.getConnectionType(EnumFacing.SOUTH, false) != none)
            list += EnumFacing.SOUTH to (vec3Of(4.px, 4.px, 1 - 4.px) createAABBUsing vec3Of(1 - 4.px, 1 - 4.px, 1))

        if (pipe.getConnectionType(EnumFacing.WEST, false) != none)
            list += EnumFacing.WEST to (vec3Of(0, 4.px, 4.px) createAABBUsing vec3Of(4.px, 1 - 4.px, 1 - 4.px))

        if (pipe.getConnectionType(EnumFacing.EAST, false) != none)
            list += EnumFacing.EAST to (vec3Of(1 - 4.px, 4.px, 4.px) createAABBUsing vec3Of(1, 1 - 4.px, 1 - 4.px))

        return list
    }
}