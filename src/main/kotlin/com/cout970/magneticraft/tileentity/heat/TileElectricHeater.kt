package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_WORKING
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.TraitHeat
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by cout970 on 04/07/2016.
 */

@TileRegister("electric_heater")
class TileElectricHeater : TileBase() {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)
    val traitElectricity = TraitElectricity(this, listOf(mainNode))

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT,
            conductivity = DEFAULT_CONDUCTIVITY,
            worldGetter = { this.world },
            posGetter = { this.getPos() })

    val traitHeat: TraitHeat = TraitHeat(this, listOf(heat))

    override val traits: List<ITileTrait> = listOf(traitHeat, traitElectricity)
    override fun update() {
        if (worldObj.isServer) {
            if (mainNode.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE && heat.heat < heat.maxHeat) {
                val power = -Config.electricHeaterMaxConsumption * interpolate(mainNode.voltage, 60.0, 70.0)
                val applied = mainNode.applyPower(power, false)
                val energy = applied.toFloat()
                heat.applyHeat((energy * ENERGY_TO_HEAT).toDouble(), false)
            }
        }
        super.update()
    }

    override fun receiveSyncData(data: IBD, side: Side) {
        super.receiveSyncData(data, side)
        if (side == Side.SERVER) {
            data.getDouble(DATA_ID_MACHINE_WORKING, { heat.heat = it })
        }
    }
}