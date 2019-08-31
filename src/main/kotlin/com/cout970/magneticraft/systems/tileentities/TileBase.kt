package com.cout970.magneticraft.systems.tileentities

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.toVec3d
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.network.MessageTileUpdate
import com.cout970.vector.extensions.distanceSq
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by cout970 on 2017/06/12.
 */
abstract class TileBase : TileEntity() {

    private val modules = mutableListOf<IModule>()
    @Suppress("LeakingThis")
    val container = ModuleContainer(this, modules)

    private var blockState: IBlockState? = null
    private var lastTime: Long = -1

    val ref: ITileRef get() = DynamicTileRef(this)

    fun initModules(vararg list: IModule) {
        if (modules.isEmpty()) {
            modules += list
            container.modules.forEach { it.container = container; it.init() }
        }
    }

    fun getBlockState(): IBlockState {
        if (world == null) return Blocks.AIR.defaultState

        if (blockState == null || blockState!!.block != getBlockType() || world.totalWorldTime > lastTime + 40) {
            lastTime = world.totalWorldTime
            blockState = world.getBlockState(pos)
            onBlockStateUpdates()
        }
        return blockState!!
    }

    open fun onBlockStateUpdates() = Unit

    /**
     * In order to this method to work the subclass must implement ITickeable
     *
     */
    open fun update() {
        container.modules.forEach(IModule::update)
    }

    override fun updateContainingBlockInfo() {
        super.updateContainingBlockInfo()
        blockState = null
    }

    open fun onBreak() {
        container.modules.forEach(IModule::onBreak)
    }

    override fun onLoad() {
        container.modules.forEach(IModule::onLoad)
    }

    override fun invalidate() {
        super.invalidate()
        if (world.isClient) {
            onBreak()
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return container.modules.any { it.hasCapability(capability, facing) }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val list = container.modules.filter { it.hasCapability(capability, facing) }
        return list.firstOrNull()?.getCapability(capability, facing)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasKey("TileData")) {
            load(compound.getCompoundTag("TileData"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.apply {
            setTag("TileData", save())
        }
        return super.writeToNBT(compound)
    }

    open fun save(): NBTTagCompound {
        val moduleNbts = container.modules.map { it.serializeNBT() }
        if (moduleNbts.isNotEmpty()) {
            return newNbt {
                list("_modules") {
                    moduleNbts.forEach { appendTag(it) }
                }
            }
        }
        return NBTTagCompound()
    }

    open fun load(nbt: NBTTagCompound) {
        if (nbt.hasKey("_modules")) {
            val list = nbt.getList("_modules")
            container.modules.forEachIndexed { index, module ->
                module.deserializeNBT(list.getTagCompound(index))
            }
        }
    }

    // Vanilla value is 64 * 64
    override fun getMaxRenderDistanceSquared(): Double = 128.0 * 128.0

    //Sends to the client the nbt of the server TileEntity
    //NOTE: never send a packet every tick, instead use shouldTick(20) to send a packet only every second
    fun sendUpdateToNearPlayers() {
        if (world.isClient) return
        val packet = updatePacket ?: return
        world.playerEntities
                .map { it as EntityPlayerMP }
                .filter { getDistanceSq(it.posX, it.posY, it.posZ) <= (64 * 64) }
                .forEach { it.connection.sendPacket(packet) }
    }

    fun sendSyncDataToNearPlayers(ibd: IBD, distance: Double) {
        if (world.isClient) return
        val squaredDistance = distance * distance
        val closePlayers = world.playerEntities
                .map { it as EntityPlayerMP }
                .filter { getDistanceSq(it.posX, it.posY, it.posZ) <= squaredDistance }

        if (closePlayers.isEmpty()) return
        val packet = MessageTileUpdate(ibd, pos, world.provider.dimension)

        closePlayers.forEach { Magneticraft.network.sendTo(packet, it) }
    }

    open fun saveToPacket() = save()
    open fun loadFromPacket(nbt: NBTTagCompound) = load(nbt)

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val nbt = super.writeToNBT(newNbt {
            add("custom", saveToPacket())
        })
        return SPacketUpdateTileEntity(pos, 1, nbt)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity) {
        if (pkt.nbtCompound.hasKey("custom")) {
            val nbt = pkt.nbtCompound.getCompoundTag("custom")
            loadFromPacket(nbt)
        } else {
            readFromNBT(pkt.nbtCompound)
        }
    }

    override fun setWorldCreate(worldIn: World) {
        this.world = worldIn
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(NBTTagCompound())
    }

    /**
     * Receives data sent using [sendSyncData] in ContainerBase
     * @param otherSide the side that sent the message
     */
    open fun receiveSyncData(ibd: IBD, otherSide: Side) {
        container.modules.forEach { it.receiveSyncData(ibd, otherSide) }
    }

    override fun toString(): String {
        return this::class.qualifiedName ?: "MagneticraftTileEntity"
    }

    class ModuleContainer(override val tile: TileBase, override val modules: List<IModule>) : IModuleContainer {
        override val world: World get() = tile.world
        override val pos: BlockPos get() = tile.pos
        override val blockState: IBlockState get() = tile.getBlockState()

        override fun markDirty() = tile.markDirty()
        override fun sendUpdateToNearPlayers() = tile.sendUpdateToNearPlayers()
        override fun sendSyncDataToNearPlayers(ibd: IBD, distance: Double) = tile.sendSyncDataToNearPlayers(ibd, distance)
    }
}