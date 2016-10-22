package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_WORKING
import com.cout970.magneticraft.registry.HEAT_HANDLER
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.misc.IBD
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */

class TileFirebox(
) : TileBase(), ITickable {

    companion object {
        val FUEL_TO_HEAT = 0.5f
        val DEFAULT_MAX_TEMP = 400.toKelvinFromCelsius()
    }

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = (COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT).toLong(),
            conductivity = 0.05,
            tile = this)
    val inventory = ItemStackHandler(1)
    var maxBurningTime = 0f
    var maxBurningTemp = DEFAULT_MAX_TEMP
    var burningTime = 0f

    override fun update() {

        if (!worldObj.isRemote) {
            //consumes fuel
            if (burningTime <= 0) {
                if (inventory[0] != null) {
                    val time = TileEntityFurnace.getItemBurnTime(inventory[0])

                    if (time > 0) {
                        maxBurningTime = time.toFloat()
                        burningTime = time.toFloat()
                        inventory[0] = inventory[0]!!.consumeItem()
                        markDirty()
                    }
                }
            }
            //burns fuel
            if (burningTime > 0 && heat.heat < heat.maxHeat) {
                val burningSpeed = Math.ceil(Config.fireboxMaxProduction / 10.0).toInt()
                burningTime -= burningSpeed
                heat.pushHeat((burningSpeed * FUEL_TO_HEAT).toLong(), false)
            }

            //sends an update to the client to start/stop the fan animation
            if (shouldTick(200)) {
                val data = IBD()
                data.setBoolean(DATA_ID_MACHINE_WORKING, heat.temperature > STANDARD_AMBIENT_TEMPERATURE + 1)
                data.setLong(DATA_ID_MACHINE_HEAT, heat.heat)
                sendSyncData(data, Side.CLIENT)
            }

            heat.updateHeat()
        }
    }

    override fun receiveSyncData(data: IBD, side: Side) {
        super.receiveSyncData(data, side)
        if (side == Side.SERVER) {
            data.getLong(DATA_ID_MACHINE_WORKING, { heat.heat = it })
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setTag("inventory", inventory.serializeNBT())
        setFloat("maxBurningTime", maxBurningTime)
        setFloat("burningTime", burningTime)
        setLong("heat", heat.heat)

    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        maxBurningTime = nbt.getFloat("maxBurningTime")
        burningTime = nbt.getFloat("burningTime")
        heat.heat = nbt.getLong("heat")
        heat.refreshConnections()
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return inventory as T
        if (capability == HEAT_HANDLER) return heat as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        if (capability == HEAT_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    fun getDirection(): EnumFacing {
        val state = world.getBlockState(pos)
        if (PROPERTY_DIRECTION.isIn(state)) {
            return PROPERTY_DIRECTION[state]
        }
        return EnumFacing.NORTH
    }
}