package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_WORKING
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.misc.inventory.consumeItem
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitHeat
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */

@TileRegister("firebox")
class TileFirebox : TileBase() {

    companion object {
        val FUEL_TO_HEAT = 0.5f
    }

    var fuelHelper = FuelCache()
    var maxFuelTemp: Double = Config.defaultMaxTemp
    val inventory = ItemStackHandler(1)
    var maxBurningTime = 0f
    var burningTime = 0f

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT,
            conductivity = DEFAULT_CONDUCTIVITY,
            worldGetter = { this.world },
            posGetter = { this.getPos() })

    val heatNodes: List<IHeatNode> = listOf(heat)
    val traitHeat: TraitHeat = TraitHeat(this, heatNodes)

    override val traits: List<ITileTrait> = listOf(traitHeat)

    override fun update() {
        if (worldObj.isServer) {
            //consumes fuel
            if (burningTime <= 0) {
                if (inventory[0] != null) {
                    val time = TileEntityFurnace.getItemBurnTime(inventory[0])

                    if (time > 0) {
                        maxBurningTime = time.toFloat()
                        burningTime = time.toFloat()
                        maxFuelTemp = fuelHelper.getOrChange(inventory[0]!!)
                        inventory.setStackInSlot(0, inventory[0]!!.consumeItem())
                        markDirty()
                    }
                }
            }
            //burns fuel
            if (burningTime > 0 && heat.heat < heat.maxHeat && heat.temperature < maxFuelTemp) {
                val burningSpeed = Math.ceil(Config.fireboxMaxProduction / 10.0).toInt()
                burningTime -= burningSpeed
                heat.applyHeat(burningSpeed.toDouble() * FUEL_TO_HEAT, false)
            }

            //sends an update to the client to start/stop the fan animation
            if (shouldTick(200)) {
                val data = IBD()
                data.setBoolean(DATA_ID_MACHINE_WORKING, heat.temperature > STANDARD_AMBIENT_TEMPERATURE + 1)
                data.setDouble(DATA_ID_MACHINE_HEAT, heat.heat)
                //sendSyncData(data, Side.CLIENT)
            }
            super.update()
        }
    }



    override fun receiveSyncData(data: IBD, side: Side) {
        super.receiveSyncData(data, side)
        if (side == Side.SERVER) {
            data.getDouble(DATA_ID_MACHINE_WORKING, { heat.heat = it })
        }
    }

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            add("inventory", inventory.serializeNBT())
            add("maxBurningTime", maxBurningTime)
            add("meltingTime", burningTime)
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        maxBurningTime = nbt.getFloat("maxBurningTime")
        burningTime = nbt.getFloat("meltingTime")
        super.load(nbt)
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return inventory as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    fun getDirection(): EnumFacing {
        val state = world.getBlockState(pos)
        if (PROPERTY_DIRECTION.isIn(state)) {
            return state[PROPERTY_DIRECTION]
        }
        return EnumFacing.NORTH
    }
}