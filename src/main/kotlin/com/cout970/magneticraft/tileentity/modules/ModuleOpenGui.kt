package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.vector.xi
import com.cout970.magneticraft.util.vector.yi
import com.cout970.magneticraft.util.vector.zi

class ModuleOpenGui(
        override val name: String = "module_open_gui"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    override fun onActivated(args: OnActivatedArgs): Boolean {
        return if (!args.playerIn.isSneaking) {
            if (args.worldIn.isServer) {
                args.playerIn.openGui(Magneticraft, -1, args.worldIn, pos.xi, pos.yi, pos.zi)
            }
            true
        } else {
            false
        }
    }
}