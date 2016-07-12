package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.consumeItem
import com.cout970.magneticraft.util.fluid.Tank
import com.cout970.magneticraft.util.misc.ValueAverage
import com.cout970.magneticraft.util.toKelvinFromCelsius
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */

class TileIncendiaryGenerator(
        val tank: Tank = object : Tank(4000) {
            override fun canFillFluidType(fluid: FluidStack): Boolean = fluid.fluid.name == "water"
        }
) : TileElectricBase(), IFluidHandler by tank {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)
    val inventory = ItemStackHandler(1)
    var maxBurningTime = 0f
    var burningTime = 0f
    var heat = STANDARD_AMBIENT_TEMPERATURE.toFloat()
    val production = ValueAverage()
    var nanoBuckets = 0

    override fun getMainNode(): IElectricNode = mainNode

    override fun update() {

        if (!worldObj.isRemote) {
            //consumes fuel
            if (burningTime <= 0 && node.voltage < 120) {
                if (inventory[0] != null) {
                    val time = TileEntityFurnace.getItemBurnTime(inventory[0])
                    if (time > 0) {
                        maxBurningTime = time.toFloat()
                        burningTime = time.toFloat()
                        inventory[0] = consumeItem(inventory[0]!!)
                        markDirty()
                    }
                }
            }
            //burns fuel
            if (burningTime > 0 && heat < MAX_HEAT) {
                val burningSpeed = 1
                burningTime -= burningSpeed
                heat += burningSpeed
            }
            //makes electricity from heat
            if (heat > STANDARD_AMBIENT_TEMPERATURE && tank.fluidAmount > 0) {
                val speed = interpolate(heat.toDouble(), STANDARD_AMBIENT_TEMPERATURE, MAX_HEAT - 10)
                val prod = Config.incendiaryGeneratorMaxProduction * speed
                val applied = node.applyPower((1 - interpolate(node.voltage, 120.0, 125.0)) *
                        prod) / Config.incendiaryGeneratorMaxProduction
                production += applied * prod
                heat -= applied.toFloat()
                // 1 coal -> 1600 ticks, 1 bucket 1000*1000 nanoBuckets,
                // we want to use 1 bucket ever 8 coal,
                // so every tick uses (1000*1000)/(1600*8) = 78.125 nanoBuckets
                nanoBuckets += (applied * 78.125).toInt()
                if (nanoBuckets > 1000) {
                    nanoBuckets -= 1000
                    tank.drainInternal(1, true)
                }
            }
            production.tick()
        }
        super.update()
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setTag("inventory", inventory.serializeNBT())
        setFloat("maxBurningTime", maxBurningTime)
        setFloat("burningTime", burningTime)
        setFloat("heat", heat)
        setTag("tank", NBTTagCompound().apply { tank.writeToNBT(this) })
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        maxBurningTime = nbt.getFloat("maxBurningTime")
        burningTime = nbt.getFloat("burningTime")
        heat = nbt.getFloat("heat")
        tank.readFromNBT(nbt.getCompoundTag("tank"))
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }

    companion object {
        val MAX_HEAT = 500.toKelvinFromCelsius()
    }
}