package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeManager
import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipe
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.core.DATA_ID_THERMOPILE_DRAIN
import com.cout970.magneticraft.gui.common.core.DATA_ID_THERMOPILE_PRODUCTION
import com.cout970.magneticraft.gui.common.core.DATA_ID_THERMOPILE_SOURCE
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.AverageSyncVariable
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.plus
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/08/28.
 */
class ModuleThermopile(
        val node: IElectricNode,
        override val name: String = "module_steam_generator"
) : IModule {

    override lateinit var container: IModuleContainer
    var counter = 0
    var heatSource = 0
    var heatDrain = 0
    val production = ValueAverage()

    override fun update() {
        if (world.isClient) return

        counter++
        if (counter > 20) {
            counter = 0
            updateTemps()
        }

        if (node.voltage < ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE) {
            val energy = getGeneratedPower()
            node.applyPower(energy, false)
            production += energy
        }
        production.tick()
    }

    fun updateTemps() {
        heatSource = 0
        heatDrain = 0
        val checks = mutableListOf<Triple<BlockPos, IBlockState, IThermopileRecipe>>()

        EnumFacing.values().forEach { dir ->
            val state = world.getBlockState(pos + dir)
            val recipe = ThermopileRecipeManager.findRecipe(state.block) ?: return@forEach

            checks += Triple(pos + dir, state, recipe)
            val heat = recipe.getHeat(world, pos + dir, state)

            if (heat > 0) {
                heatSource += heat
            } else if (heat < 0) {
                heatDrain -= heat
            }
        }

        checks.forEach { (pos, state, recipe) ->
            recipe.applyDecay(world, pos, state, heatSource, heatDrain)
        }
    }

    fun getGeneratedPower(): Double {
        val diff = Math.min(heatSource, heatDrain)
        return (diff.toDouble() / 200.0) * Config.thermopileProduction
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("heatSource", heatSource)
        add("heatDrain", heatDrain)
        add("counter", counter)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        heatSource = nbt.getInteger("heatSource")
        heatDrain = nbt.getInteger("heatDrain")
        counter = nbt.getInteger("counter")
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = listOf(
            IntSyncVariable(id = DATA_ID_THERMOPILE_SOURCE, getter = { heatSource }, setter = { heatSource = it }),
            IntSyncVariable(id = DATA_ID_THERMOPILE_DRAIN, getter = { heatDrain }, setter = { heatDrain = it }),
            AverageSyncVariable(DATA_ID_THERMOPILE_PRODUCTION, production)
    )
}