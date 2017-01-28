package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.minus
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockSolarPanel
import com.cout970.magneticraft.tileentity.electric.TileElectricBase
import com.cout970.magneticraft.util.*
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2016/09/06.
 */
class TileSolarPanel : TileElectricBase(), IMultiblockCenter {

    val node = ElectricNode({ worldObj }, { pos })

    override val electricNodes: List<IElectricNode> = listOf(node)
    var ambientTemperature = STANDARD_AMBIENT_TEMPERATURE.toFloat()

    override var multiblock: Multiblock?
        get() = MultiblockSolarPanel
        set(value) {
            //ignored
        }

    override var centerPos: BlockPos?
        get() = BlockPos.ORIGIN
        set(value) {
            //ignored
        }

    override var multiblockFacing: EnumFacing? = null

    val direction: EnumFacing get() = if (PROPERTY_DIRECTION.isIn(getBlockState()))
        PROPERTY_DIRECTION[getBlockState()] else EnumFacing.NORTH

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        PROPERTY_ACTIVE[getBlockState()] else false

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean = false

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? = null

    override fun update() {
        super.update()
        if (worldObj.isServer) {
            if (active && worldObj.isDaytime && !worldObj.provider.hasNoSky) {
                var count = 0
                for (i in 0..2) {
                    for (j in 0..2) {
                        if (worldObj.canBlockSeeSky(pos + direction.rotatePoint(BlockPos.ORIGIN, BlockPos(i - 1, 0, j)))) {
                            count++
                        }
                    }
                }
                if (count > 0) { //Generate slightly less than double power in desert, 75% power in ice plains
                    node.applyPower((1 - interpolate(node.voltage, TIER_1_MAX_VOLTAGE, TIER_1_GENERATORS_MAX_VOLTAGE)) * Config.solarPanelMaxProduction * (count / 9f) * (1 + ambientTemperature / 2.0), false)
                }
            }
        }
    }


    override fun canConnectAtSide(facing: EnumFacing?): Boolean = facing?.axis != EnumFacing.Axis.Y

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(2, 0, 2)) to (pos + BlockPos(3, 1, 3))

    override fun onActivate() {
    }

    override fun onDeactivate() {
    }

    override fun onLoad() {
        super.onLoad()
        ambientTemperature = world.getBiome(pos).getFloatTemperature(pos)
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        if (multiblockFacing != null) setEnumFacing("direction", multiblockFacing!!)
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }
}