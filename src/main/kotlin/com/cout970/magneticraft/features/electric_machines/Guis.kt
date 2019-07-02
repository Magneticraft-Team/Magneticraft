package com.cout970.magneticraft.features.electric_machines

import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.components.bars.StaticBarProvider
import com.cout970.magneticraft.systems.gui.components.bars.toPercentText
import com.cout970.magneticraft.systems.gui.render.GuiBase
import com.cout970.magneticraft.systems.gui.render.dsl

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiWindTurbine(gui: GuiBase, container: ContainerWindTurbine) = gui.dsl {
    val tile = container.tile

    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
    }

    bars {
        electricProduction(tile.windTurbineModule.production, Config.windTurbineMaxProduction)

        StaticBarProvider(0.0, 1.0, tile.windTurbineModule::openSpace).let { prov ->
            genericBar(8, 5, prov, prov.toPercentText("Wind not blocked: "))
        }

        StaticBarProvider(0.0, 1.0, tile.windTurbineModule::currentWind).let { prov ->
            genericBar(9, 7, prov, prov.toPercentText("Wind: ", "%"))
        }
    }
}

fun guiElectricHeater(gui: GuiBase, container: ContainerElectricHeater) = gui.dsl {
    val tile = container.tile

    bars {
        electricBar(tile.electricNode)
        heatBar(tile.heatNode)
        electricConsumption(tile.electricHeaterModule.consumption, Config.electricHeaterMaxProduction)
        heatProduction(tile.electricHeaterModule.production, Config.electricHeaterMaxProduction)
    }
}

fun guiRfHeater(gui: GuiBase, container: ContainerRfHeater) = gui.dsl {
    val tile = container.tile

    bars {
        rfBar(tile.storage)
        heatBar(tile.node)
        rfConsumption(tile.electricHeaterModule.consumption, Config.electricHeaterMaxProduction)
        heatProduction(tile.electricHeaterModule.production, Config.electricHeaterMaxProduction)
    }
}
