package com.cout970.magneticraft.tileentity.multiblock

import com.cout970.magneticraft.multiblock.IMultiblockCenter
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
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 20/08/2016.
 */
// TilEntity to store multiblock data in every block that forms the multiblock,
// this allows to destroy it breaking any block
open class TileMultiblock : TileBase(), ITileMultiblock {

    override var multiblock: Multiblock? = null
        set(i) {
            field = i
        }
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
            tag.setString("Name", it.name)
            tag.setBlockPos("CenterPos", centerPos!!)
            tag.setEnumFacing("Facing", multiblockFacing!!)
            compound!!.setTag("Multiblock", tag)
        }
        return super.writeToNBT(compound)
    }

    override fun onLoad() {
        super.onLoad()
        if (multiblock != null) {
            sendUpdateToNearPlayers()
        }
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun onActivate() {}

    override fun onDeactivate() {
        worldObj.removeTileEntity(pos)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (multiblock != null) {
            val tile = worldObj.getTileEntity(pos.subtract(centerPos!!))
            if (tile is IMultiblockCenter) {
                return tile.hasCapability(capability, facing, centerPos!!)
            }
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (multiblock != null) {
            val tile = worldObj.getTileEntity(pos.subtract(centerPos!!))
            if (tile is IMultiblockCenter) {
                return tile.getCapability(capability, facing, centerPos!!)
            }
        }
        return super.getCapability(capability, facing)
    }
}