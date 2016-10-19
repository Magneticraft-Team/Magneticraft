package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_WORKING
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.misc.IBD
import com.cout970.magneticraft.util.misc.ValueAverage
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */

class TileFirebox(
) : TileBase() {

    companion object {
        val MAX_HEAT = 500.toKelvinFromCelsius()
        //fuel -> burning time -> heat -> electricity
        val FUEL_TO_WATTS = 10// 1 coal = 1600 burning time = 16000RF
        val FUEL_TO_HEAT = 0.5f
        val HEAT_TO_WATTS = FUEL_TO_WATTS / FUEL_TO_HEAT
    }

    val inventory = ItemStackHandler(1)
    var maxBurningTime = 0f
    var burningTime = 0f
    var heat = STANDARD_AMBIENT_TEMPERATURE.toFloat()
    val production = ValueAverage()

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
            if (burningTime > 0 && heat < MAX_HEAT) {
                val burningSpeed = Math.ceil(Config.incendiaryGeneratorMaxProduction / 10.0).toInt()
                burningTime -= burningSpeed
                heat += burningSpeed * FUEL_TO_HEAT
            }
            //updates the production counter
            production.tick()

            //sends an update to the client to start/stop the fan animation
            if (shouldTick(200)) {
                val data = IBD()
                data.setBoolean(DATA_ID_MACHINE_WORKING, heat > STANDARD_AMBIENT_TEMPERATURE + 1)
                data.setFloat(DATA_ID_MACHINE_HEAT, heat)
                sendSyncData(data, Side.CLIENT)
            }
        }
        super.update()
    }

    override fun receiveSyncData(data: IBD, side: Side) {
        super.receiveSyncData(data, side)
        if (side == Side.SERVER) {
            data.getBoolean(DATA_ID_MACHINE_WORKING, { fanAnimation.active = it })
            data.getFloat(DATA_ID_MACHINE_WORKING, { heat = it })
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setTag("inventory", inventory.serializeNBT())
        setFloat("maxBurningTime", maxBurningTime)
        setFloat("burningTime", burningTime)
        setFloat("heat", heat)
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        maxBurningTime = nbt.getFloat("maxBurningTime")
        burningTime = nbt.getFloat("burningTime")
        heat = nbt.getFloat("heat")
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == EnumFacing.UP
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return inventory as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    fun getDirection(): EnumFacing {
        val state = world.getBlockState(pos)
        if (PROPERTY_DIRECTION.isIn(state)) {
            return PROPERTY_DIRECTION[state]
        }
        return EnumFacing.NORTH
    }

    class TileIncendiaryGeneratorBottom() : TileBase() {

        @Suppress("UNCHECKED_CAST")
        override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
            val tile = worldObj.getTile<TileIncendiaryGenerator>(pos.up())
            if (tile != null) return tile.getCapability(capability, facing)
            return super.getCapability(capability, facing)
        }

        override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
            val tile = worldObj.getTile<TileIncendiaryGenerator>(pos.up())
            if (tile != null) return tile.hasCapability(capability, facing)
            return super.hasCapability(capability, facing)
        }

        override fun save(): NBTTagCompound = NBTTagCompound()
        override fun load(nbt: NBTTagCompound) = Unit
    }
}