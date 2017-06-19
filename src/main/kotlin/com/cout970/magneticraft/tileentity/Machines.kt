package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.block.Machines
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ConveyorBeltModule
import com.cout970.magneticraft.tileentity.modules.ModuleCrushingTable
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable

/**
 * Created by cout970 on 2017/06/12.
 */

class TileBox : TileBase() {

    val invModule = ModuleInventory(27)

    init {
        initModules(invModule)
    }
}

class TileCrushingTable : TileBase() {

    val invModule = ModuleInventory(1, capabilityFilter = { null })
    val crushingModule = ModuleCrushingTable(invModule)

    init {
        initModules(invModule, crushingModule)
    }
}

class TileConveyorBelt : TileBase(), ITickable {

    var rotation = 0f
    val facing: EnumFacing
        get() = getBlockState()[Machines.PROPERTY_CONVEYOR_ORIENTATION].facing

    val conveyorModule = ConveyorBeltModule({ facing })

    init {
        initModules(conveyorModule)
    }

    override fun update() {
        super.update()
    }
}

