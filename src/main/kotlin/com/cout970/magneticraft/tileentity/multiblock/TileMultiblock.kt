package com.cout970.magneticraft.tileentity.multiblock

import com.cout970.magneticraft.multiblock.ITileMultiblock
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.MultiblockManager
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.getBlockPos
import com.cout970.magneticraft.util.getEnumFacing
import com.cout970.magneticraft.util.setBlockPos
import com.cout970.magneticraft.util.setEnumFacing
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 20/08/2016.
 */
// TilEntity to store multiblock data in every block that forms the multiblock,
// this allows to destroy it breaking any block
open class TileMultiblock : TileBase(), ITileMultiblock {

    override var multiblock: Multiblock? = null
    override var centerPos: BlockPos? = null
    override var multiblockFacing: EnumFacing? = null

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("Multiblock")) {
            val tag = compound.getCompoundTag("Multiblock")
            multiblock = MultiblockManager.getMultiblock(tag.getString("Name"))
            centerPos = tag.getBlockPos("CenterPos")
            multiblockFacing = tag.getEnumFacing("Facing")
        }
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        multiblock?.let {
            val tag = NBTTagCompound()
            tag.setString("Multiblock", it.name)
            tag.setBlockPos("CenterPos", centerPos!!)
            tag.setEnumFacing("Facing", multiblockFacing!!)
            compound?.apply { setTag("Multiblock", tag) }
        }
        return super.writeToNBT(compound)
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit
}