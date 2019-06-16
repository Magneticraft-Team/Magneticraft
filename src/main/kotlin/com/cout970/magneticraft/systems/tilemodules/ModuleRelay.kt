package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBuffer
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer

class ModuleRelay(
    val inventory: Inventory,
    val buffer: PneumaticBuffer,
    override val name: String = "module_relay"
) : IModule {

    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isClient || !container.shouldTick(5) || buffer.blocked) return

        for (index in 0 until inventory.slots) {
            val stack = inventory.getStackInSlot(index)
            if (stack.isNotEmpty) {
                buffer.add(inventory.extractItem(index, 64, false))
                return
            }
        }
    }
}