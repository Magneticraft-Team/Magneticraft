package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleElectricity
import com.cout970.magneticraft.tileentity.modules.ModuleMultiblock
import com.cout970.magneticraft.tileentity.modules.ModuleMultiblockCenter
import com.cout970.magneticraft.util.interpolate
import com.cout970.magneticraft.util.vector.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/07/03.
 */

@RegisterTileEntity("multiblock_gap")
class TileMultiblockGap : TileBase() {

    val multiblockModule = ModuleMultiblock()

    init {
        initModules(multiblockModule)
    }
}


@RegisterTileEntity("solar_panel")
class TileSolarPanel : TileBase(), ITickable {

    val facing: EnumFacing
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.facing ?: EnumFacing.NORTH
    val active: Boolean
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: false

    val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = MultiblockSolarPanel,
            facingGetter = { facing }
    )

    val node = ElectricNode(container.ref)

    val electricModule = ModuleElectricity(
            electricNodes = listOf(node),
            canConnectAtSide = this::canConnectAtSide
    )

    init {
        initModules(multiblockModule, electricModule)
    }

    override fun update() {
        super.update()
        if (world.isServer) {
            if (active && world.isDaytime && world.provider.hasSkyLight()) {
                var count = 0
                for (i in 0..2) {
                    for (j in 0..2) {
                        val offset = facing.rotatePoint(BlockPos.ORIGIN, BlockPos(i - 1, 0, j))
                        if (world.canBlockSeeSky(pos + offset)) {
                            count++
                        }
                    }
                }
                if (count > 0) {
                    val limit = interpolate(node.voltage, ElectricConstants.TIER_1_MAX_VOLTAGE,
                            ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE)
                    node.applyPower((1 - limit) * Config.solarPanelMaxProduction * (count / 9f), false)
                }
            }
        }
    }


    fun canConnectAtSide(facing: EnumFacing?): Boolean = facing?.axis != EnumFacing.Axis.Y

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val size = MultiblockSolarPanel.size.toVec3d()
        val center = MultiblockSolarPanel.center.toVec3d()
        val box = Vec3d.ZERO toAABBWith size
        val boxWithOffset = box.offset(-center)
        val normalizedBox = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), boxWithOffset)
        val alignedBox = facing.rotateBox(vec3Of(0.5), normalizedBox)
        return alignedBox.offset(pos)
    }
}

