package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBuffer
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticUtils
import com.cout970.magneticraft.api.pneumatic.ITubeConnectable
import com.cout970.magneticraft.api.pneumatic.PneumaticBox
import com.cout970.magneticraft.api.pneumatic.PneumaticMode
import com.cout970.magneticraft.misc.getList
import com.cout970.magneticraft.misc.list
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.TUBE_CONNECTABLE
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class BufferInfo(
    val input: Boolean,
    val side: EnumFacing,
    val canInsert: (PneumaticBuffer, PneumaticBox, PneumaticMode, EnumFacing) -> Boolean = { _, _, mode, _ ->
        mode == PneumaticMode.GOING_BACK
    }
)

class ModulePneumaticEndpoint(
    val buffers: List<PneumaticBuffer>,
    val getInfo: (PneumaticBuffer) -> BufferInfo,
    override val name: String = "module_pneumatic_network"
) : IModule {

    override lateinit var container: IModuleContainer

    override fun onBreak() {
        buffers.forEach { buff ->
            buff.getItems().forEach { box ->
                container.world.dropItem(box.item, container.pos)
            }
        }
    }

    override fun update() {
        if (world.isClient || !container.shouldTick(5)) return

        buffers.forEach { buff ->
            val info = getInfo(buff)
            if (!info.input){
                buff.ejectItems { box ->
                    PneumaticUtils.handleItemEjection(container.ref, box, info.side)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        return if (cap == TUBE_CONNECTABLE && facing != null) {

            val buffs = buffers.filter { getInfo(it).side == facing }
            if (buffs.isEmpty()) return null

            object : ITubeConnectable {
                override fun insert(box: PneumaticBox, mode: PneumaticMode): Boolean {
                    return buffs.filter { getInfo(it).canInsert(it, box, mode, facing) }.any { it.add(box); true }
                }

                override fun canInsert(box: PneumaticBox, mode: PneumaticMode): Boolean {
                    return buffs.any { getInfo(it).canInsert(it, box, mode, facing) }
                }

                override fun getWeight(): Int = buffs.sumBy { it.getItems().size }
            } as T
        } else super.getCapability(cap, facing)
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            list("buffers") {
                buffers.forEach { appendTag(it.serializeNBT()) }
            }
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        nbt.getList("buffers").forEachIndexed { index, nbtBase ->
            buffers.getOrNull(index)?.deserializeNBT(nbtBase as NBTTagList)
        }
    }
}