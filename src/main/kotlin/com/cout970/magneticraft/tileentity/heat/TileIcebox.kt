package com.cout970.magneticraft.tileentity.heat

import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.api.internal.registries.machines.tablesieve.IceboxRecipeManager
import com.cout970.magneticraft.api.registries.machines.heatexchanger.IIceboxRecipe
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

class TileIcebox : TileHeatBase() {

    val tank: Tank = object : Tank(4000) {
        override fun canFillFluidType(fluid: FluidStack?): Boolean = fluid?.fluid?.name == "water"
    }

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = IRON_HEAT_CAPACITY * 7,
            maxHeat = (IRON_HEAT_CAPACITY * 3 * IRON_MELTING_POINT).toLong(),
            conductivity = DEFAULT_CONDUCTIVITY,
            worldGetter = { this.world },
            posGetter = { this.getPos() })

    val inventory = ItemStackHandler(1)
    var maxMeltingTime = 0f
    var meltingTime = 0f
    var maxFreezingTime = 0f
    var freezingTime = 0f
    private var lastInput: ItemStack? = null
    private var lastOutput: FluidStack? = null


    override val heatNodes: List<IHeatNode>
        get() = listOf(heat)

    override fun update() {
        if (!worldObj.isRemote) {
            //consumes fuel
            if (meltingTime <= 0) {
                if (inventory[0] != null) {
                    lastInput = getRecipe()?.input
                    if (lastInput != null) {
                        val time = lastRecipe()?.getTotalHeat(heat.ambientTemperature) ?: 0

                        if (time > 0) {
                            maxMeltingTime = time.toFloat()
                            meltingTime = time.toFloat()
                            inventory[0] = inventory[0]!!.consumeItem()
                            markDirty()
                        }
                    }
                } else {
                    lastInput = null
                }
            }
            if (freezingTime <= 0) {
                if (tank.fluid != null) {
                    lastOutput = getRecipeReverse()?.output
                    if (lastOutput != null) {
                        val time = lastRecipe()?.getTotalHeat(heat.ambientTemperature) ?: 0

                        if (time > 0) {
                            maxFreezingTime = time.toFloat()
                            freezingTime = time.toFloat()
                            markDirty()
                        }
                    }
                }
            }
            //burns fuel
            if (canMelt()) {
                //Melting rate depends on difference between current temperature and recipe's temperature range
                val meltingSpeed = Math.ceil((Config.iceboxMaxConsumption * interpolate(heat.temperature, lastRecipe()!!.minTemp, lastRecipe()!!.maxTemp)) / 10.0).toInt()
                val outFluidInc = FluidStack(lastRecipe()!!.output.fluid, (lastRecipe()!!.output.amount * meltingSpeed / maxMeltingTime).toInt())
                if (tank.fillInternal(outFluidInc, false) != 0) {  //This is lossy, but the fluid is considered a byproduct, so whatever
                    meltingTime -= meltingSpeed
                    tank.fillInternal(outFluidInc, true)
                    heat.pullHeat((meltingSpeed).toLong(), false)
                    markDirty()
                }
            } else if (canFreeze()) {
                val freezingSpeed = Math.ceil(Config.iceboxMaxConsumption / 10.0).toInt()
                val outFluidInc = FluidStack(lastRecipeReverse()!!.output.fluid, (lastRecipe()!!.output.amount * freezingSpeed / maxMeltingTime).toInt())
                if (tank.drainInternal(outFluidInc, false) != null) {  //This is lossy, but the fluid is considered a byproduct, so whatever
                    if (freezingTime - freezingSpeed > 0 || inventory.insertItem(0, lastRecipeReverse()!!.input, true) == null) //If we would need to insert output, check to see if possible
                        freezingTime -= freezingSpeed
                    if (freezingTime <= 0) {
                        inventory.insertItem(0, lastRecipeReverse()!!.input, false)
                        if (tank.fluid == null) {
                            lastOutput = null
                        }
                    }
                    tank.drainInternal(outFluidInc, true)
                    heat.pushHeat((freezingSpeed).toLong(), false)
                    markDirty()
                }
            }

            //sends an update to the client to start/stop the fan animation
            if (shouldTick(200)) {
                val data = IBD()
                data.setBoolean(DATA_ID_MACHINE_WORKING, heat.temperature > heat.ambientTemperature + 1)
                data.setLong(DATA_ID_MACHINE_HEAT, heat.heat)
                sendSyncData(data, Side.CLIENT)
            }
            super.update()
        }
    }

    private var recipeCache: IIceboxRecipe? = null
    private var inputCache: ItemStack? = null
    private var recipeCacheOut: IIceboxRecipe? = null
    private var outputCache: FluidStack? = null

    fun canMelt(): Boolean
            = lastRecipe() != null
            && meltingTime > 0
            && heat.temperature > Math.ceil(lastRecipe()!!.minTemp) //Ensure min melting temp is strictly greater than max freezing temp
            && heat.heat < heat.maxHeat
            && heat.temperature < lastRecipe()!!.maxTemp

    fun canFreeze(): Boolean {
        return lastRecipeReverse() != null
                && freezingTime > 0
                && lastRecipeReverse()!!.reverse
                && heat.temperature < Math.floor(lastRecipeReverse()!!.minTemp) //Ensure min melting temp is strictly greater than max freezing temp
                && heat.heat < heat.maxHeat
    }

    fun lastRecipe(): IIceboxRecipe? {
        if (lastInput == null) return null
        return getRecipe(lastInput!!)
    }

    fun lastRecipeReverse(): IIceboxRecipe? {
        if (lastOutput == null) return null
        return getRecipe(lastOutput!!)
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

    fun getRecipe(output: FluidStack): IIceboxRecipe? {
        if (output.isFluidEqual(outputCache)) return recipeCacheOut
        val recipe = IceboxRecipeManager.findRecipeReverse(output)
        if (recipe != null) {
            recipeCacheOut = recipe
            outputCache = output
        }
        return recipe
    }

    fun getRecipe(): IIceboxRecipe? = if (inventory[0] != null) getRecipe(inventory[0]!!) else null

    fun getRecipeReverse(): IIceboxRecipe? = if (tank.fluid != null) getRecipe(tank.fluid!!) else null

    override fun receiveSyncData(data: IBD, side: Side) {
        super.receiveSyncData(data, side)
        if (side == Side.SERVER) {
            data.getLong(DATA_ID_MACHINE_WORKING, { heat.heat = it })
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setTag("inventory", inventory.serializeNBT())
        if (lastInput != null)
            setTag("recipe_cache", lastInput!!.serializeNBT())
        setFloat("maxMeltingTime", maxMeltingTime)
        setFloat("meltingTime", meltingTime)
        setTag("tank", NBTTagCompound().apply { tank.writeToNBT(this) })
        if (lastOutput != null)
            setTag("reverse_cache", NBTTagCompound().apply { lastOutput!!.writeToNBT(this) })
        super.save()
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        if (nbt.hasKey("recipe_cache")) lastInput!!.deserializeNBT(nbt.getCompoundTag("recipe_cache"))
        else lastInput = null
        maxMeltingTime = nbt.getFloat("maxMeltingTime")
        meltingTime = nbt.getFloat("meltingTime")
        tank.readFromNBT(nbt.getCompoundTag("tank"))
        if (nbt.hasKey("reverse_cache"))
            lastOutput = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("reverse_cache"))
        else lastOutput = null
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
}