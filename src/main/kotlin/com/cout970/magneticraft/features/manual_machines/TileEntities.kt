package com.cout970.magneticraft.features.manual_machines

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.block.getOrientationCentered
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.misc.vector.xd
import com.cout970.magneticraft.misc.vector.yd
import com.cout970.magneticraft.misc.vector.zd
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.ModuleCrushingTable
import com.cout970.magneticraft.systems.tilemodules.ModuleFabricator
import com.cout970.magneticraft.systems.tilemodules.ModuleInventory
import com.cout970.magneticraft.systems.tilemodules.ModuleSluiceBox
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("box")
class TileBox(type: TileType) : TileBase(type) {

    val inventory = Inventory(27)
    val invModule = ModuleInventory(inventory)

    init {
        initModules(invModule)
    }
}

@RegisterTileEntity("crushing_table")
class TileCrushingTable(type: TileType) : TileBase(type) {

    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val crushingModule = ModuleCrushingTable(inventory)

    init {
        initModules(invModule, crushingModule)
    }
}

@RegisterTileEntity("sluice_box")
class TileSluiceBox(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = getBlockState().getOrientationCentered()

    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val sluiceBoxModule = ModuleSluiceBox({ facing }, inventory)

    init {
        initModules(sluiceBoxModule, invModule)
    }

        override fun tick() {
        super.update()
    }

    // TODO
//    override fun shouldRenderInPass(pass: Int): Boolean {
//        return pass == 0 || pass == 1
//    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val dir = facing.toBlockPos()
        return super.getRenderBoundingBox().expand(dir.xd, dir.yd, dir.zd)
    }
}

@RegisterTileEntity("fabricator")
class TileFabricator(type: TileType) : TileBase(type), ITickableTileEntity {
    val inventory = Inventory(9)

    val invModule = ModuleInventory(inventory)
    val fabricatorModule = ModuleFabricator(inventory)

    init {
        initModules(invModule, fabricatorModule)
    }

        override fun tick() {
        super.update()
    }
}