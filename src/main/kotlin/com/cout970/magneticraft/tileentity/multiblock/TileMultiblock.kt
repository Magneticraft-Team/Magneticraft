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
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 20/08/2016.
 */
// TilEntity to store multiblock data in every block that forms the multiblock,
// this allows to destroy it breaking any block
@TileRegister("tile_multiblock")
open class TileMultiblock : TileBase(), ITileMultiblock {

    override var multiblock: Multiblock? = null
        set(i) {
            field = i
        }
    override var centerPos: BlockPos? = null
    override var multiblockFacing: EnumFacing? = null

    override fun readCustomNBT(compound: NBTTagCompound) {
        if (compound!!.hasKey("Multiblock")) {
            val tag = compound.getCompoundTag("Multiblock")
            multiblock = MultiblockManager.getMultiblock(tag.getString("Name"))
            centerPos = tag.getBlockPos("CenterPos")
            multiblockFacing = tag.getEnumFacing("Facing")
        }
    }

    override fun writeCustomNBT(compound: NBTTagCompound, sync: Boolean) {
        multiblock?.let {
            val tag = NBTTagCompound()
            compound.setString("Name", it.name)
            compound.setBlockPos("CenterPos", centerPos!!)
            compound.setEnumFacing("Facing", multiblockFacing!!)
            compound.setTag("Multiblock", tag)
        }
    }

    override fun onLoad() {
        super.onLoad()
        if (multiblock != null) {
            sendUpdateToNearPlayers()
        }
    }


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

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (multiblock != null) {
            val tile = worldObj.getTileEntity(pos.subtract(centerPos!!))
            if (tile is IMultiblockCenter) {
                return tile.getCapability(capability, facing, centerPos!!)
            }
        }
        return super.getCapability(capability, facing)
    }
}