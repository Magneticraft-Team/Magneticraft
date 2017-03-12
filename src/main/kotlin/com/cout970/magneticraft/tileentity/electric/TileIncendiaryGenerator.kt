package com.cout970.magneticraft.tileentity.electric


import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_WORKING
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.inventory.consumeItem
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.render.AnimationTimer
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */

@TileRegister("incendiary_generator")
class TileIncendiaryGenerator(
        val tank: Tank = object : Tank(4000) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean = fluid?.fluid?.name == "water"
        }
) : TileBase(), IFluidHandler by tank {

    companion object {
        val MAX_HEAT = 500.toKelvinFromCelsius()
        //fuel -> burning time -> heat -> electricity
        val FUEL_TO_WATTS = 10// 1 coal = 1600 burning time = 16000RF
        val FUEL_TO_HEAT = 0.5f
        val HEAT_TO_WATTS = FUEL_TO_WATTS / FUEL_TO_HEAT
    }

    val fuelCache = FuelCache()

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)

    val traitElectricity = TraitElectricity(this, listOf(mainNode),
            canConnectAtSideImpl = this::canConnectAtSide)

    override val traits: List<ITileTrait> = listOf(traitElectricity)

    val inventory = ItemStackHandler(1)
    var maxBurningTime = 0f
    var burningTime = 0f
    var maxFuelTemp = Config.defaultMaxTemp
    var ambientTemperature = STANDARD_AMBIENT_TEMPERATURE.toFloat()
    var heat = STANDARD_AMBIENT_TEMPERATURE.toFloat()
    val production = ValueAverage()
    var nanoBuckets = 0
    var fanAnimation = AnimationTimer().apply { active = false }


    override fun update() {

        if (worldObj.isServer) {
            //consumes fuel
            if (burningTime <= 0 && mainNode.voltage < 120) {
                if (inventory[0] != null) {
                    val time = TileEntityFurnace.getItemBurnTime(inventory[0])
                    if (time > 0) {
                        maxFuelTemp = fuelCache.getOrChange(inventory[0]!!)
                        maxBurningTime = time.toFloat()
                        burningTime = time.toFloat()
                        inventory.setStackInSlot(0, inventory[0]!!.consumeItem())
                        markDirty()
                    }
                }
            }
            //burns fuel
            if (burningTime > 0 && heat < MAX_HEAT && heat < maxFuelTemp) {
                val burningSpeed = Math.ceil(Config.incendiaryGeneratorMaxProduction / 10.0).toInt()
                burningTime -= burningSpeed
                heat += burningSpeed * FUEL_TO_HEAT
            }
            //makes electricity from heat
            if (heat > ambientTemperature + 75 && tank.fluidAmount > 0) {

                val speed = interpolate(heat.toDouble(), ambientTemperature.toDouble(), MAX_HEAT - 50)
                val prod = Config.incendiaryGeneratorMaxProduction * speed
                val interp = interpolate(
                        v = mainNode.voltage,
                        min = ElectricConstants.TIER_1_MAX_VOLTAGE,
                        max = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE
                )
                val applied = mainNode.applyPower((1 - interp) * prod, false)
                production += applied

                heat -= applied.toFloat() / HEAT_TO_WATTS
                // 1 coal -> 1600 ticks, 1 bucket 1000*1000 nanoBuckets,
                // we want to use 1 bucket ever 8 coal,
                // so every tick uses (1000*1000)/(1600*8) = 78.125 nanoBuckets per fuel
                nanoBuckets += ((applied / FUEL_TO_WATTS) * 78.125).toInt()
                if (nanoBuckets > 1000) {
                    nanoBuckets -= 1000
                    tank.drainInternal(1, true)
                }
            } else if (heat > ambientTemperature && tank.fluidAmount > 0) {
                heat -= 0.109f
            }
            //updates the production counter
            production.tick()

            //sends an update to the client to start/stop the fan animation
            if (shouldTick(200)) {
                val data = IBD()
                data.setBoolean(DATA_ID_MACHINE_WORKING, heat > STANDARD_AMBIENT_TEMPERATURE + 1)
                data.setFloat(DATA_ID_MACHINE_HEAT, heat)
                //sendSyncData(data, Side.CLIENT)
            }
            if (shouldTick(200)) {
                ambientTemperature = guessAmbientTemp(world, pos).toFloat()
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

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            add("inventory", inventory.serializeNBT())
            add("maxBurningTime", maxBurningTime)
            add("meltingTime", burningTime)
            add("heat", heat)
            add("fuelTemp", maxFuelTemp)
            add("tank", NBTTagCompound().apply { tank.writeToNBT(this) })
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        maxBurningTime = nbt.getFloat("maxBurningTime")
        burningTime = nbt.getFloat("meltingTime")
        heat = nbt.getFloat("heat")
        maxFuelTemp = nbt.getDouble("fuelTemo")
        tank.readFromNBT(nbt.getCompoundTag("tank"))
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

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == EnumFacing.UP
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == FLUID_HANDLER) return this as T
        if (capability == ITEM_HANDLER) return inventory as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == FLUID_HANDLER) return true
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

    @TileRegister("incendiary_generator_bottom")
    class TileIncendiaryGeneratorBottom : TileBase() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            val tile = worldObj.getTile<TileIncendiaryGenerator>(pos.up())
            if (tile != null) return tile.getCapability(capability, facing)
            return super.getCapability(capability, facing)
        }

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            val tile = worldObj.getTile<TileIncendiaryGenerator>(pos.up())
            if (tile != null) return tile.hasCapability(capability, facing)
            return super.hasCapability(capability, facing)
        }

    }
}