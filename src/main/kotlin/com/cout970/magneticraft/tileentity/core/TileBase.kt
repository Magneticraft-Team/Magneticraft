package com.cout970.magneticraft.tileentity.core

import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import com.cout970.magneticraft.util.getList
import com.cout970.magneticraft.util.getTagCompound
import com.cout970.magneticraft.util.list
import com.cout970.magneticraft.util.newNbt
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/06/12.
 */
abstract class TileBase(modules: List<IModule>) : TileEntity() {
    @Suppress("LeakingThis")
    val container = ModuleContainer(this, modules)

    private var blockState: IBlockState? = null
    private var lastTime: Long = -1

    init {
        container.modules.forEach { it.container = container; it.init() }
    }

    fun getBlockState(): IBlockState {
        if (blockState == null || blockState!!.block != getBlockType() || world.totalWorldTime > lastTime + 40) {
            lastTime = world.totalWorldTime
            blockState = world.getBlockState(pos)
        }
        return blockState!!
    }

    fun update(){
        container.modules.forEach(IModule::update)
    }

    override fun updateContainingBlockInfo() {
        super.updateContainingBlockInfo()
        blockState = null
    }

    fun onBreak(){
        container.modules.forEach(IModule::onBreak)
    }

    override fun onLoad() {
        container.modules.forEach(IModule::onLoad)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return container.modules.any { it.hasCapability(capability, facing) }
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val list = container.modules.filter { it.hasCapability(capability, facing) }
        return list.first().getCapability(capability, facing)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasKey("TileData")) {
            load(compound.getCompoundTag("TileData"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.apply { setTag("TileData", save()) }
        return super.writeToNBT(compound)
    }

    open fun save(): NBTTagCompound {
        val traitNbts = container.modules.mapNotNull { it.serializeNBT() }
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
            container.modules.forEachIndexed { index, trait ->
                trait.deserializeNBT(list.getTagCompound(index))
            }
        }
    }

    // Vanilla value is 64 * 64
    override fun getMaxRenderDistanceSquared(): Double = 128.0 * 128.0

    //Sends to the client the nbt of the server TileEntity
    //NOTE: never send a packet every tick, instead use shouldTick(20) to send a packet only every second
    fun sendUpdateToNearPlayers() {
        if (world.isClient) return
        val packet = updatePacket
        world.playerEntities
                .map { it as EntityPlayerMP }
                .filter { getDistanceSq(it.posX, it.posY, it.posZ) <= (64 * 64) }
                .forEach { it.connection.sendPacket(packet) }
    }


    class ModuleContainer(val tile: TileBase, override val modules: List<IModule>) : IModuleContainer {
        override val world: World get() = tile.world
        override val pos: BlockPos get() = tile.pos
        override val blockState: IBlockState get() = tile.getBlockState()

        override fun markDirty() = tile.markDirty()
        override fun sendUpdateToNearPlayers() = tile.sendUpdateToNearPlayers()
    }
}

@Suppress("unused")
class ExampleTile : TileBase(listOf(ModuleInventory(27))), ITickable


