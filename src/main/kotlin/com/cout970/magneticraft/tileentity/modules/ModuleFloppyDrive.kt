package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.computer.DeviceFloppyDrive
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand

/**
 * Created by cout970 on 2017/08/10.
 */
class ModuleFloppyDrive(
        val ref: ITileRef,
        val invModule: ModuleInventory,
        val slot: Int,
        override val name: String = "module_monitor"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer

    val drive: DeviceFloppyDrive = DeviceFloppyDrive(ref, this::getDisk)

    fun getDisk(): IFloppyDisk? {
        return ITEM_FLOPPY_DISK!!.fromItem(invModule.inventory[slot])
    }

    override fun update() {
        drive.iterate()
    }

    override fun serializeNBT(): NBTTagCompound {
        return drive.serializeNBT()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        drive.deserializeNBT(nbt)
    }

    override fun onActivated(args: OnActivatedArgs): Boolean = args.run {
        if (!playerIn.isSneaking) {
            if (worldIn.isServer) {
                var block = true
                if (heldItem.isNotEmpty) {
                    val cap = ITEM_FLOPPY_DISK!!.fromItem(heldItem)
                    if (cap != null) {
                        if (invModule.inventory[slot].isEmpty) {
                            val index = playerIn.inventory.currentItem
                            if (index in 0..8) {
                                invModule.inventory[slot] = playerIn.inventory.removeStackFromSlot(index)
                                container.sendUpdateToNearPlayers()
                            }
                        } else {
                            block = false
                        }
                    } else {
                        block = false
                    }
                } else {
                    block = false
                }

                if (!block) {
                    playerIn.openGui(Magneticraft, -1, worldIn, pos.x, pos.y, pos.z)
                }
            }
            return true
        } else {
            if (worldIn.isServer && hand == EnumHand.MAIN_HAND && heldItem.isEmpty) {
                if (invModule.inventory[slot].isNotEmpty) {
                    playerIn.inventory.addItemStackToInventory(
                            invModule.inventory.extractItem(slot, 64, false)
                    )
                    container.sendUpdateToNearPlayers()
                }
            }
        }
        false
    }
}