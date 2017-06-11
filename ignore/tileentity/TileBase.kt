package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.util.getList
import com.cout970.magneticraft.util.getTagCompound
import com.cout970.magneticraft.util.list
import com.cout970.magneticraft.util.newNbt
import com.teamwizardry.librarianlib.common.base.block.TileMod
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side

/**
 * Base class for all the TileEntities in the mod
 */
abstract class TileBase : TileMod(), ITickable {

    open val traits = listOf<ITileTrait>()

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

    override fun update() {
        traits.forEach(ITileTrait::update)
    }

    /**
     * Called when the block is mined
     * This is called in the server and the client
     */
    open fun onBreak() {
        traits.forEach(ITileTrait::onBreak)
    }

    override fun onLoad() {
        try {

            traits.forEach(ITileTrait::onLoad)
        } catch (e: Exception) {
            println(this::class)
            e.printStackTrace()
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return traits.any { it.hasCapability(capability, facing) }
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val list = traits.filter { it.hasCapability(capability, facing) }
        return list.first().getCapability(capability, facing)
    }

    /**
     * Drop an item into the world, used to drop the inventory content when a block is mined
     */
    //TODO make extension for world with this
    fun dropItem(last: ItemStack, pos: BlockPos) {
        if (world.isServer) {
            val f = 0.05f
            val d0 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val d1 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val d2 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val entityItem = EntityItem(world, pos.x.toDouble() + d0, pos.y.toDouble() + d1, pos.z.toDouble() + d2,
                    last)
            entityItem.setDefaultPickupDelay()
            world.spawnEntityInWorld(entityItem)
        }
    }

    override fun readCustomNBT(compound: NBTTagCompound) {
        if (compound.hasKey("TileData")) {
            load(compound.getCompoundTag("TileData"))
        }
    }

    override fun writeCustomNBT(compound: NBTTagCompound, sync: Boolean) {
        compound.apply { setTag("TileData", save()) }
    }

    //The vanilla values is 64 * 64
    override fun getMaxRenderDistanceSquared(): Double = 128.0 * 128.0

    open fun save(): NBTTagCompound {
        val traitNbts = traits.mapNotNull { it.serialize() }
        if (traitNbts.isNotEmpty()) {
            return newNbt {
                list("_traits") {
                    traitNbts.forEach { appendTag(it) }
                }
            }
        }
        return NBTTagCompound()
    }

    open fun load(nbt: NBTTagCompound) {
        if (nbt.hasKey("_traits")) {
            val list = nbt.getList("_traits")
            traits.forEachIndexed { index, trait ->
                trait.deserialize(list.getTagCompound(index))
            }
        }
    }

    //Sends to the client the nbt of the server TileEntity
    //NOTE: never send a packet every tick, instead use shouldTick(20) to send a packet only every second
    fun sendUpdateToNearPlayers() {
        if (world.isClient) return
        val packet = updatePacket
        worldObj.playerEntities
                .map { it as EntityPlayerMP }
                .filter { getDistanceSq(it.posX, it.posY, it.posZ) <= (64 * 64) }
                .forEach { it.connection.sendPacket(packet) }
    }


    /**
     * Receives data sent using [sendSyncData]
     * @param side the side that sent the message
     */
    open fun receiveSyncData(data: IBD, side: Side) = Unit

}