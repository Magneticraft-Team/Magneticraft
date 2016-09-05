package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.network.MessageTileUpdate
import com.cout970.magneticraft.util.misc.IBD
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side

abstract class TileBase : TileEntity() {

    private var blockState: IBlockState? = null
    private var lastTime: Long = -1

    fun getBlockState(): IBlockState {
        if (blockState == null || blockState!!.block != getBlockType() || worldObj.totalWorldTime > lastTime + 40) {
            lastTime = worldObj.totalWorldTime
            blockState = worldObj.getBlockState(pos)
        }
        return blockState!!
    }

    override fun updateContainingBlockInfo() {
        super.updateContainingBlockInfo()
        blockState = null
    }

    open fun onBreak() {
    }

    fun dropItem(last: ItemStack, pos: BlockPos) {
        if (!world.isRemote) {
            val f = 0.05f
            val d0 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val d1 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val d2 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val entityItem = EntityItem(world, pos.x.toDouble() + d0, pos.y.toDouble() + d1, pos.z.toDouble() + d2, last)
            entityItem.setDefaultPickupDelay()
            world.spawnEntityInWorld(entityItem)
        }
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("TileData")) {
            load(compound.getCompoundTag("TileData"))
        }
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        compound?.apply { setTag("TileData", save()) }
        return super.writeToNBT(compound)
    }

    override fun getMaxRenderDistanceSquared(): Double = 128.0 * 128.0

    abstract fun save(): NBTTagCompound
    abstract fun load(nbt: NBTTagCompound)

    fun sendUpdateToNearPlayers() {
        if (world.isRemote) return
        val packet = updatePacket
        worldObj.playerEntities
                .map { it as EntityPlayerMP }
                .filter { getDistanceSq(it.posX, it.posY, it.posZ) <= (64 * 64) }
                .forEach { it.connection.sendPacket(packet) }
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, 0, updateTag)
    }

    override fun getUpdateTag(): NBTTagCompound = writeToNBT(NBTTagCompound())!!

    override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity?) {
        readFromNBT(pkt!!.nbtCompound)
    }

    /**
     * Receives data sent using [sendSyncData]
     * @param side the side that sent the message
     */
    open fun receiveSyncData(data: IBD, side: Side) {
    }

    /**
     * Sends data to 'side' to be handled in [receiveSyncData]
     * @param side the place where the message will be sent
     */
    fun sendSyncData(data: IBD, side: Side) {
        val msg = MessageTileUpdate(data, pos, world.provider.dimension)
        if (side == Side.CLIENT) {
            Magneticraft.network.sendToAllAround(msg, NetworkRegistry.TargetPoint(world.provider.dimension, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 32.0))
        } else {
            Magneticraft.network.sendToServer(msg)
        }
    }
}