package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_FLUID_HANDLER
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraftforge.fluids.FluidUtil

/**
 * Created by cout970 on 2017/08/29.
 */
class ModuleBucketIO(
    val tank: Tank,
    val input: Boolean = true,
    val output: Boolean = true,
    override val name: String = "module_bucket_io"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    override fun onActivated(args: OnActivatedArgs): Boolean = args.run {
        val handler = ITEM_FLUID_HANDLER!!.fromItem(playerIn.getHeldItem(hand)) ?: return false

        if (worldIn.isServer) {
            handler.tankProperties.forEach { prop ->
                if (output) {
                    val result0 = FluidUtil.tryFillContainer(heldItem, tank, prop.capacity, playerIn, true)
                    if (result0.isSuccess) {

                        heldItem.shrink(1)
                        if (!playerIn.inventory.addItemStackToInventory(result0.result)) {
                            worldIn.dropItem(result0.result, playerIn.position, false)
                        }

                        container.sendUpdateToNearPlayers()
                        return@forEach
                    }
                }
                if (input) {
                    val result1 = FluidUtil.tryEmptyContainer(heldItem, tank, prop.capacity, playerIn, true)
                    if (result1.isSuccess) {
                        if (!playerIn.capabilities.isCreativeMode) {
                            heldItem.shrink(1)
                            if (!playerIn.inventory.addItemStackToInventory(result1.result)) {
                                worldIn.dropItem(result1.result, playerIn.position, false)
                            }
                        }
                        container.sendUpdateToNearPlayers()
                        return@forEach
                    }
                }
            }
        }
        return true
    }
}