package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleComputer
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import com.cout970.magneticraft.tileentity.modules.ModuleMonitor
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable

/**
 * Created by cout970 on 2017/07/07.
 */

@RegisterTileEntity("computer")
class TileComputer : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()

    val invModule = ModuleInventory(1)
    val monitorModule = ModuleMonitor(container.ref)
    val computerModule = ModuleComputer(
            devices = mapOf(0xFF to monitorModule.monitor)
    )

    init {
        initModules(computerModule, invModule, monitorModule )
        computerModule.motherboard.start()
    }

    override fun update() {
        super.update()
    }
}