package com.cout970.magneticraft.systems.tileentities

import com.cout970.magneticraft.*
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.getList
import com.cout970.magneticraft.misc.list
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.network.MagneticraftNetwork
import com.cout970.magneticraft.systems.network.MessageTileUpdate
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SUpdateTileEntityPacket
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.LogicalSide

/**
 * Created by cout970 on 2017/06/12.
 */
abstract class TileBase(type: TileType) : TileEntity(type) {

    private val modules = mutableListOf<IModule>()
    @Suppress("LeakingThis")
    val container = ModuleContainer(this, modules)

    private var lastTime: Long = -1

    val ref: ITileRef get() = DynamicTileRef(this)

    fun initModules(vararg list: IModule) {
        if (modules.isEmpty()) {
            modules += list
            container.modules.forEach { it.container = container; it.init() }
        }
    }

    val theWorld: World get() = world!!

    open fun onBlockStateUpdates() = Unit

    /**
     * In order to this method to work the subclass must implement ITickeable
     *
     */
    open fun update() {
        container.modules.forEach(IModule::update)
    }

    open fun onBreak() {
        container.modules.forEach(IModule::onBreak)
    }

    override fun onLoad() {
        container.modules.forEach(IModule::onLoad)
    }

    override fun remove() {
        super.remove()
        if (world!!.isClient) {
            onBreak()
        }
    }

//    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
//        val list = container.modules.filter { it.hasCapability(capability, facing) }
//        return list.firstOrNull()?.getCapability(capability, facing)
//    }

    override fun read(compound: CompoundNBT) {
        super.read(compound)
        if (compound.hasKey("TileData")) {
            load(compound.getCompoundTag("TileData"))
        }
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        compound.put("TileData", save())
        return super.write(compound)
    }

    open fun save(): NBTTagCompound {
        val moduleNbts = container.modules.map { it.serializeNBT() }
        if (moduleNbts.isNotEmpty()) {
            return newNbt {
                list("_modules") {
                    moduleNbts.forEach { add(it) }
                }
            }
        }
        return NBTTagCompound()
    }

    open fun load(nbt: NBTTagCompound) {
        if (nbt.hasKey("_modules")) {
            val list = nbt.getList("_modules")
            container.modules.forEachIndexed { index, module ->
                module.deserializeNBT(list.getCompound(index))
            }
        }
    }

    // Vanilla value is 64 * 64
    override fun getMaxRenderDistanceSquared(): Double = 128.0 * 128.0

    //Sends to the client the nbt of the server TileEntity
    //NOTE: never send a packet every tick, instead use shouldTick(20) to send a packet only every second
    fun sendUpdateToNearPlayers() {
        if (world!!.isClient) return
        val packet = updatePacket ?: return
        world!!.players
            .map { it as ServerPlayerEntity }
            .filter { getDistanceSq(it.posX, it.posY, it.posZ) <= (64 * 64) }
            .forEach { it.connection.sendPacket(packet) }
    }

    fun sendSyncDataToNearPlayers(ibd: IBD, distance: Double) {
        if (world!!.isClient) return
        val squaredDistance = distance * distance
        val closePlayers = world!!.players
            .map { it as ServerPlayerEntity }
            .filter { getDistanceSq(it.posX, it.posY, it.posZ) <= squaredDistance }

        if (closePlayers.isEmpty()) return
        val packet = MessageTileUpdate(ibd, pos, world!!.dimension.type.id)

        closePlayers.forEach { MagneticraftNetwork.sendTo(packet, it) }
    }

    open fun saveToPacket() = save()
    open fun loadFromPacket(nbt: NBTTagCompound) = load(nbt)

    override fun getUpdatePacket(): SUpdateTileEntityPacket? {
        val nbt = newNbt {
            add("custom", saveToPacket())
        }

        super.write(nbt)
        return SUpdateTileEntityPacket(pos, 1, nbt)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SUpdateTileEntityPacket) {
        if (pkt.nbtCompound.hasKey("custom")) {
            val nbt = pkt.nbtCompound.getCompoundTag("custom")
            loadFromPacket(nbt)
        } else {
            read(pkt.nbtCompound)
        }
    }

    override fun getUpdateTag(): NBTTagCompound {
        return write(NBTTagCompound())
    }

    /**
     * Receives data sent using [sendSyncData] in ContainerBase
     * @param otherSide the side that sent the message
     */
    open fun receiveSyncData(ibd: IBD, otherSide: LogicalSide) {
        container.modules.forEach { it.receiveSyncData(ibd, otherSide) }
    }

    override fun toString(): String {
        return this::class.qualifiedName ?: "MagneticraftTileEntity"
    }

    class ModuleContainer(override val tile: TileBase, override val modules: List<IModule>) : IModuleContainer {
        override val world: World get() = tile.world!!
        override val pos: BlockPos get() = tile.pos
        override val blockState: IBlockState get() = tile.getBlockState()

        override fun markDirty() = tile.markDirty()
        override fun sendUpdateToNearPlayers() = tile.sendUpdateToNearPlayers()
        override fun sendSyncDataToNearPlayers(ibd: IBD, distance: Double) = tile.sendSyncDataToNearPlayers(ibd, distance)
    }
}