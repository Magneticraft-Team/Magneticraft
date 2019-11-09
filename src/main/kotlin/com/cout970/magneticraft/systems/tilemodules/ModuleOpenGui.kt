package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer

class ModuleOpenGui(
    override val name: String = "module_open_gui"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    override fun onActivated(args: OnActivatedArgs): Boolean {
        return if (!args.playerIn.isSneaking) {
            if (args.worldIn.isServer) {
                // TODO
//                args.playerIn.openGui(Magneticraft, -1, args.worldIn, pos.xi, pos.yi, pos.zi)
            }
            true
        } else {
            false
        }
    }
}