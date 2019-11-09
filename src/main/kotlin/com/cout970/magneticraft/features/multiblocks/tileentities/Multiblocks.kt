package com.cout970.magneticraft.features.multiblocks.tileentities

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.multiblocks.MultiblockContext
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.ModuleMultiblockCenter
import com.cout970.magneticraft.systems.tilemodules.ModuleMultiblockGap
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

/**
 * Created by cout970 on 2017/07/03.
 */

@RegisterTileEntity("multiblock_gap")
class TileMultiblockGap(type: TileType) : TileBase(type) {

    val multiblockModule = ModuleMultiblockGap()

    init {
        initModules(multiblockModule)
    }
}

abstract class TileMultiblock(type: TileType) : TileBase(type) {

    val facing: EnumFacing
        get() = blockState[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.facing ?: EnumFacing.NORTH
    val active: Boolean
        get() = blockState[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: false

    var clientErrors: List<ITextComponent> = emptyList()

    abstract fun getMultiblock(): Multiblock

    @Suppress("LeakingThis")
    abstract val multiblockModule: ModuleMultiblockCenter

    // TODO
//    override fun shouldRenderInPass(pass: Int): Boolean {
//        return if (active) super.shouldRenderInPass(pass) else pass == 1
//    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val size = getMultiblock().size.toVec3d()
        val center = getMultiblock().center.toVec3d()
        val box = Vec3d.ZERO createAABBUsing size
        val boxWithOffset = box.offset(-center)
        val normalizedBox = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), boxWithOffset)
        val alignedBox = facing.rotateBox(vec3Of(0.5), normalizedBox)
        return alignedBox.offset(pos)
    }

    fun multiblockContext(): MultiblockContext {
        return MultiblockContext(getMultiblock(), theWorld, pos, facing, null)
    }
}