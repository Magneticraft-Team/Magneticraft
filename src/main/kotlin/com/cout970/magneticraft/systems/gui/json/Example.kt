package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.features.heat_machines.TileElectricHeater
import com.cout970.magneticraft.features.manual_machines.TileBox

fun GuiBuilder.boxGui(tile: TileBox) {
    repeat(27) {
        slot(tile.inventory, it)
    }

    region(0, 27)
}

fun GuiBuilder.electricHeaterGui(tile: TileElectricHeater){
    verticalBar("voltage", tile.electricNode)
}

//fun guiElectricHeater(gui: GuiBase, container: ContainerElectricHeater) = gui.dsl {
//    val tile = container.tile
//
//    bars {
//        electricBar(tile.electricNode)
//        heatBar(tile.heatNode)
//        electricConsumption(tile.electricHeaterModule.consumption, Config.electricHeaterMaxProduction)
//        heatProduction(tile.electricHeaterModule.production, Config.electricHeaterMaxProduction)
//    }
//}