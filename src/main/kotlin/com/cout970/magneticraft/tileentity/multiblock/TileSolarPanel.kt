package com.cout970.magneticraft.tileentity.multiblock


import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockSolarPanel
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.rotatePoint
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.unaryMinus
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2016/09/06.
 */
@TileRegister("solar_panel")
class TileSolarPanel : TileBase(), IMultiblockCenter {

    val node = ElectricNode({ worldObj }, { pos })
    val traitElectricity = TraitElectricity(this, listOf(node),
            canConnectAtSideImpl = this::canConnectAtSide)

    override val traits: List<ITileTrait> = listOf(traitElectricity)

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
        getBlockState()[PROPERTY_DIRECTION] else EnumFacing.NORTH

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        getBlockState()[PROPERTY_ACTIVE] else false

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
                    val interp = interpolate(node.voltage, ElectricConstants.TIER_1_MAX_VOLTAGE, ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE)
                    node.applyPower((1 - interp) * Config.solarPanelMaxProduction * (count / 9f) * (1 + ambientTemperature / 2.0), false)
                }
            }
        }
    }


    fun canConnectAtSide(facing: EnumFacing?): Boolean = facing?.axis != EnumFacing.Axis.Y

    override fun getRenderBoundingBox(): AxisAlignedBB = (BlockPos.ORIGIN toAABBWith direction.rotatePoint(BlockPos.ORIGIN,
            multiblock!!.size)).offset(direction.rotatePoint(BlockPos.ORIGIN, -multiblock!!.center)).offset(pos)


    override fun onActivate() {
    }

    override fun onDeactivate() {
    }

    override fun onLoad() {
        super.onLoad()
        ambientTemperature = world.getBiome(pos).getFloatTemperature(pos)
    }

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            if (multiblockFacing != null) add("direction", multiblockFacing!!)
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        super.load(nbt)
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }
}