package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBuffer
import com.cout970.magneticraft.api.pneumatic.PneumaticBox
import com.cout970.magneticraft.api.pneumatic.PneumaticMode
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.util.EnumFacing

class ModuleFilter(
    val input: PneumaticBuffer,
    val output: PneumaticBuffer,
    val itemFilter: ModuleItemFilter,
    override val name: String = "module_filter"
) : IModule {
    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isClient || input.getItems().isEmpty()) return

        while (input.getItems().isNotEmpty()) {
            output.add(input.pop())
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun canInsert(buff: PneumaticBuffer, box: PneumaticBox, mode: PneumaticMode, side: EnumFacing): Boolean {
        if (buff == input) {
            return mode == PneumaticMode.TRAVELING && !output.blocked && itemFilter.filterAllowStack(box.item)
        }
        if (buff == output) {
            return mode == PneumaticMode.GOING_BACK
        }
        return false
    }
}