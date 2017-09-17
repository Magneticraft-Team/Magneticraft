package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.misc.block.getOrientationCentered
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleCrushingTable
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import com.cout970.magneticraft.tileentity.modules.ModuleSluiceBox
import com.cout970.magneticraft.util.vector.toBlockPos
import com.cout970.magneticraft.util.vector.xd
import com.cout970.magneticraft.util.vector.yd
import com.cout970.magneticraft.util.vector.zd
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("box")
class TileBox : TileBase() {

    val invModule = ModuleInventory(Inventory(27))

    init {
        initModules(invModule)
    }
}

@RegisterTileEntity("crushing_table")
class TileCrushingTable : TileBase() {

    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val crushingModule = ModuleCrushingTable(inventory)

    init {
        initModules(invModule, crushingModule)
    }
}

@RegisterTileEntity("sluice_box")
class TileSluiceBox : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientationCentered()

    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val sluiceBoxModule = ModuleSluiceBox({ facing }, inventory)

    init {
        initModules(sluiceBoxModule, invModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return pass == 0 || pass == 1
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val dir = facing.toBlockPos()
        return super.getRenderBoundingBox().expand(dir.xd, dir.yd, dir.zd)
    }
}