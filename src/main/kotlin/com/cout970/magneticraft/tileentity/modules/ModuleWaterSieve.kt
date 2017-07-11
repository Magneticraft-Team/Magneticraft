package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer

/**
 * Created by cout970 on 2017/07/11.
 */
class ModuleWaterSieve(
        val invModuleInventory: ModuleInventory,
        override val name: String = "module_water_sieve"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    companion object {
        @JvmStatic
        val MAX_ITEMS = 10
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {

        val heldItem = args.heldItem
        if(heldItem.isEmpty) return false
        val stack = invModuleInventory.inventory[0]
        if(stack.isEmpty){
            //TODO
        }
        return false
    }
}