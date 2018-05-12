package com.cout970.magneticraft.tileentity.multiblock

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.multiblock.core.MultiblockContext
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleMultiblockCenter
import com.cout970.magneticraft.tileentity.modules.ModuleMultiblockGap
import com.cout970.magneticraft.util.vector.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/07/03.
 */

@RegisterTileEntity("multiblock_gap")
class TileMultiblockGap : TileBase() {

    val multiblockModule = ModuleMultiblockGap()

    init {
        initModules(multiblockModule)
    }
}

abstract class TileMultiblock : TileBase() {

    val facing: EnumFacing
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.facing ?: EnumFacing.NORTH
    val active: Boolean
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: false

    abstract fun getMultiblock(): Multiblock

    @Suppress("LeakingThis")
    abstract val multiblockModule: ModuleMultiblockCenter

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val size = getMultiblock().size.toVec3d()
        val center = getMultiblock().center.toVec3d()
        val box = Vec3d.ZERO toAABBWith size
        val boxWithOffset = box.offset(-center)
        val normalizedBox = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), boxWithOffset)
        val alignedBox = facing.rotateBox(vec3Of(0.5), normalizedBox)
        return alignedBox.offset(pos)
    }

    fun multiblockContext(): MultiblockContext {
        return MultiblockContext(getMultiblock(), world, pos, facing, null)
    }
}