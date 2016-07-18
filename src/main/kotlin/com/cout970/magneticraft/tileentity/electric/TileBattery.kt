package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.block.states.PROPERTY_DIRECTION
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.misc.ValueAverage
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 11/07/2016.
 */
class TileBattery : TileElectricBase() {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)
    var storage: Int = 0
    val inventory = ItemStackHandler(2)
    val chargeRate = ValueAverage(20)
    val itemChargeRate = ValueAverage(20)

    override fun getMainNode(): IElectricNode = mainNode

    override fun update() {
        if (!worldObj.isRemote) {
            if (mainNode.voltage > UPPER_LIMIT) {
                val speed = interpolate(mainNode.voltage, UPPER_LIMIT, 120.0) * MAX_CHARGE_SPEED
                val finalSpeed = Math.min(Math.floor(speed).toInt(), MAX_STORAGE - storage)
                mainNode.applyPower(-finalSpeed.toDouble())
                storage += finalSpeed
                chargeRate += finalSpeed
            } else if (mainNode.voltage < LOWER_LIMIT) {
                val speed = (1 - interpolate(mainNode.voltage, 60.0, LOWER_LIMIT)) * MAX_CHARGE_SPEED
                val finalSpeed = Math.min(Math.floor(speed).toInt(), storage)
                mainNode.applyPower(finalSpeed.toDouble())
                storage -= finalSpeed
                chargeRate -= finalSpeed
            }
            chargeRate.tick()
            //TODO
            itemChargeRate.tick()
        }
        super.update()
    }

    override fun save(): NBTTagCompound {
        val nbt = NBTTagCompound()
        nbt.setInteger("storage", storage)
        nbt.setTag("inventory", inventory.serializeNBT())
        return nbt
    }

    override fun load(nbt: NBTTagCompound) {
        storage = nbt.getInteger("storage")
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
    }

    fun getFacing(): EnumFacing {
        val state = world.getBlockState(pos)
        return PROPERTY_DIRECTION[state]
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == getFacing()
    }

    companion object {
        val MAX_STORAGE = 1000000
        val MAX_CHARGE_SPEED = 400
        val UPPER_LIMIT = 100.0
        val LOWER_LIMIT = 90.0
    }
}