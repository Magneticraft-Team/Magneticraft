package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeManager
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.AverageSyncVariable
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.DATA_ID_THERMOPILE_HEAT_FLUX
import com.cout970.magneticraft.systems.gui.DATA_ID_THERMOPILE_PRODUCTION
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.math.BlockPos
import kotlin.math.absoluteValue

/**
 * Created by cout970 on 2017/08/28.
 */
class ModuleThermopile(
    val node: IElectricNode,
    override val name: String = "module_thermopile"
) : IModule {

    override lateinit var container: IModuleContainer
    var totalFlux = 0f
    val production = ValueAverage()

    override fun update() {
        if (world.isClient) return

        if (container.shouldTick(20)) {
            updateFlux()
        }

        if (node.voltage < ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE) {
            val energy = getGeneratedPower()
            node.applyPower(energy, false)
            production += energy
        }
        production.tick()
    }

    fun updateFlux() {
        val sources = EnumFacing.VALUES.map { it to getSource(pos + it, it) }.toMap()

        totalFlux = listOf(
            getFlux(sources[DOWN]!!, sources[UP]!!),
            getFlux(sources[DOWN]!!, sources[NORTH]!!),
            getFlux(sources[DOWN]!!, sources[SOUTH]!!),
            getFlux(sources[DOWN]!!, sources[WEST]!!),
            getFlux(sources[DOWN]!!, sources[EAST]!!),
            getFlux(sources[UP]!!, sources[NORTH]!!),
            getFlux(sources[UP]!!, sources[SOUTH]!!),
            getFlux(sources[UP]!!, sources[WEST]!!),
            getFlux(sources[UP]!!, sources[EAST]!!),
            getFlux(sources[NORTH]!!, sources[SOUTH]!!),
            getFlux(sources[NORTH]!!, sources[WEST]!!),
            getFlux(sources[NORTH]!!, sources[EAST]!!),
            getFlux(sources[SOUTH]!!, sources[EAST]!!),
            getFlux(sources[SOUTH]!!, sources[WEST]!!),
            getFlux(sources[WEST]!!, sources[EAST]!!)
        ).sum()
    }

    fun getGeneratedPower(): Double {
        return totalFlux / 10_000 * Config.thermopileProduction
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("totalFlux", totalFlux)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        totalFlux = nbt.getFloat("totalFlux")
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = listOf(
        FloatSyncVariable(id = DATA_ID_THERMOPILE_HEAT_FLUX, getter = { totalFlux }, setter = { totalFlux = it }),
        AverageSyncVariable(DATA_ID_THERMOPILE_PRODUCTION, production)
    )

    fun getSource(pos: BlockPos, side: EnumFacing): HeatSource {
        val state = world.getBlockState(pos)

        val recipe = ThermopileRecipeManager.findRecipe(state)
        if (recipe != null) {
            return HeatSource(recipe.temperature, recipe.conductivity)
        }

        val handler = world.getTileEntity(pos)?.getOrNull(HEAT_NODE_HANDLER, side.opposite)
        if (handler != null) {
            val node = handler.nodes.filterIsInstance<IHeatNode>().firstOrNull()
            if (node != null) {
                return HeatSource(node.temperature.toFloat(), node.conductivity.toFloat() * 0.01f)
            }
        }

        return DefaultHeatSource
    }

    fun getFlux(a: HeatSource, b: HeatSource): Float {
        val conductivity = 1 / (1 / a.conductivity + 1 / b.conductivity)
        val tempDiff = a.temperature - b.temperature
        return (conductivity * tempDiff).absoluteValue
    }

    open class HeatSource(val temperature: Float, val conductivity: Float)

    object DefaultHeatSource : HeatSource(STANDARD_AMBIENT_TEMPERATURE.toFloat(), 1.0f)
}