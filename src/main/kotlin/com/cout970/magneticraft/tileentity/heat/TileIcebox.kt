package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.api.internal.registries.machines.tablesieve.IceboxRecipeManager
import com.cout970.magneticraft.api.registries.machines.heatexchanger.IIceboxRecipe
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_WORKING
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.fluid.Tank
import com.cout970.magneticraft.util.misc.IBD
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */

class TileIcebox(
        val tank: Tank = object : Tank(4000) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean = fluid?.fluid?.name == "water"
        }
) : TileHeatBase() {

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = (COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT).toLong(),
            conductivity = 0.05,
            tile = this)
    val inventory = ItemStackHandler(1)
    var maxBurningTime = 0f
    var burningTime = 0f
    private var lastInput: ItemStack? = null

    override val heatNodes: List<IHeatNode>
        get() = listOf(heat)

    override fun update() {
        if (!worldObj.isRemote) {
            //consumes fuel
            if (burningTime <= 0) {
                if (inventory[0] != null) {
                    lastInput = getRecipe()?.input
                    if (lastInput != null) {
                        val time = lastRecipe()?.getTotalHeat(heat.ambientTemperatureCache) ?: 0

                        if (time > 0) {
                            maxBurningTime = time.toFloat()
                            burningTime = time.toFloat()
                            inventory[0] = inventory[0]!!.consumeItem()
                            markDirty()
                        }
                    }
                }
            }
            //burns fuel
            if (canMelt()) {
                //Melting rate depends on difference between current temperature and recipe's temperature range
                val burningSpeed = Math.ceil((Config.iceboxMaxConsumption * interpolate(heat.temperature, lastRecipe()!!.minTemp, lastRecipe()!!.maxTemp)) / 10.0).toInt()
                if (tank.fillInternal(FluidStack(lastRecipe()!!.output.fluid, (lastRecipe()!!.output.amount * burningSpeed / maxBurningTime).toInt()), false) != 0) {  //This is lossy, but the fluid is considered a byproduct, so whatever
                    burningTime -= burningSpeed
                    tank.fillInternal(lastRecipe()!!.output, true)
                    heat.pullHeat((burningSpeed).toLong(), false)
                }
            }

            //sends an update to the client to start/stop the fan animation
            if (shouldTick(200)) {
                val data = IBD()
                data.setBoolean(DATA_ID_MACHINE_WORKING, heat.temperature > heat.ambientTemperatureCache + 1)
                data.setLong(DATA_ID_MACHINE_HEAT, heat.heat)
                sendSyncData(data, Side.CLIENT)
            }
            super.update()
        }
    }

    private var recipeCache: IIceboxRecipe? = null
    private var inputCache: ItemStack? = null

    fun canMelt(): Boolean
            = lastRecipe() != null &&
            burningTime > 0
            && heat.temperature > lastRecipe()!!.minTemp
            && heat.heat < heat.maxHeat
            && heat.temperature < lastRecipe()!!.maxTemp

    fun lastRecipe(): IIceboxRecipe? {
        if (lastInput == null) return null
        return getRecipe(lastInput!!)
    }

    fun getRecipe(input: ItemStack): IIceboxRecipe? {
        if (input === inputCache) return recipeCache
        val recipe = IceboxRecipeManager.findRecipe(input)
        if (recipe != null) {
            recipeCache = recipe
            inputCache = input
        }
        return recipe
    }

    fun getRecipe(): IIceboxRecipe? = if (inventory[0] != null) getRecipe(inventory[0]!!) else null

    override fun receiveSyncData(data: IBD, side: Side) {
        super.receiveSyncData(data, side)
        if (side == Side.SERVER) {
            data.getLong(DATA_ID_MACHINE_WORKING, { heat.heat = it })
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setTag("inventory", inventory.serializeNBT())
        setTag("recipe_cache", lastInput?.serializeNBT())
        setFloat("maxBurningTime", maxBurningTime)
        setFloat("burningTime", burningTime)
        setTag("tank", NBTTagCompound().apply { tank.writeToNBT(this) })
        super.save()
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        lastInput!!.deserializeNBT(nbt.getCompoundTag("recipe_cache"))
        maxBurningTime = nbt.getFloat("maxBurningTime")
        burningTime = nbt.getFloat("burningTime")
        tank.readFromNBT(nbt.getCompoundTag("tank"))
        super.load(nbt)
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
}