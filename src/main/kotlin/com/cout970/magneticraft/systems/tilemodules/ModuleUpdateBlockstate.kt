package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.block.state.IBlockState

class ModuleUpdateBlockstate(
    val checkInterval: Int = 20,
    override val name: String = "module_update_blockstate",
    val getNewState: (IBlockState) -> IBlockState
) : IModule {

    override lateinit var container: IModuleContainer

    override fun update() {
        if (container.shouldTick(checkInterval)) {
            val state = container.blockState
            val newState = getNewState(state)

            if (state != newState) {
                world.setBlockState(pos, newState)
            }
        }
    }
}