package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleConveyorBelt
import com.cout970.magneticraft.tileentity.modules.ModuleCrushingTable
import com.cout970.magneticraft.tileentity.modules.ModuleInserter
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable

/**
 * Created by cout970 on 2017/06/12.
 */

@RegisterTileEntity("box")
class TileBox : TileBase() {

    val invModule = ModuleInventory(27)

    init {
        initModules(invModule)
    }
}

@RegisterTileEntity("crushing_table")
class TileCrushingTable : TileBase() {

    val invModule = ModuleInventory(1, capabilityFilter = { null })
    val crushingModule = ModuleCrushingTable(invModule)

    init {
        initModules(invModule, crushingModule)
    }
}

@RegisterTileEntity("conveyor_belt")
class TileConveyorBelt : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val conveyorModule = ModuleConveyorBelt({ facing })

    // Client data
    var rotation = 0f
    var deltaTimer = System.currentTimeMillis()

    init {
        initModules(conveyorModule)
    }

    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("inserter")
class TileInserter : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val invModule = ModuleInventory(1, capabilityFilter = { null })
    val inserterModule = ModuleInserter({ facing }, invModule)

    init {
        initModules(inserterModule, invModule)
    }

    override fun update() {
        super.update()
    }
}

