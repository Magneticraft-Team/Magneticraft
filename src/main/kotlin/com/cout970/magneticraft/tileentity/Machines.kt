package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleCrushingTable
import com.cout970.magneticraft.tileentity.modules.ModuleInventory

/**
 * Created by cout970 on 2017/06/12.
 */

class TileBox : TileBase(){

    val invModule = ModuleInventory(27)

    init {
        initModules(listOf(invModule))
    }
}
class TileCrushingTable : TileBase(){

    val invModule = ModuleInventory(1, capabilityFilter = { null })
    val crushingModule = ModuleCrushingTable(invModule)

    init {
        initModules(listOf(invModule, crushingModule))
    }
}

