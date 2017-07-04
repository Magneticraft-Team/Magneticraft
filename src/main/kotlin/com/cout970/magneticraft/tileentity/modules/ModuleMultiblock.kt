package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.multiblock.core.IMultiblockModule
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.multiblock.core.MultiblockManager
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.getEnumFacing
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/07/03.
 */
class ModuleMultiblock(
        override val name: String = "module_multiblock"
) : IModule, IMultiblockModule {

    lateinit override var container: IModuleContainer

    //current multiblock
    override var multiblock: Multiblock? = null
    //relative position from the multiblock center to this block
    override var centerPos: BlockPos? = null
    //orientation of the multiblock
    override var multiblockFacing: EnumFacing? = null

    override fun serializeNBT(): NBTTagCompound = newNbt {
        if (multiblock != null) {
            add("multiblock", multiblock!!.name)
            add("centerPos", centerPos!!)
            add("multiblockFacing", multiblockFacing!!)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("multiblock")) {
            multiblock = MultiblockManager.getMultiblock(nbt.getString("multiblock"))
            centerPos = nbt.getBlockPos("centerPos")
            multiblockFacing = nbt.getEnumFacing("multiblockFacing")
        }
    }
}

