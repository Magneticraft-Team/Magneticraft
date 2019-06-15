package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBoxStorage
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticUtils
import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry
import com.cout970.magneticraft.api.pneumatic.ITube
import com.cout970.magneticraft.api.pneumatic.PneumaticBox
import com.cout970.magneticraft.api.pneumatic.PneumaticMode
import com.cout970.magneticraft.features.automatic_machines.Blocks
import com.cout970.magneticraft.misc.getList
import com.cout970.magneticraft.misc.getNullableEnumFacing
import com.cout970.magneticraft.misc.inventory.insertItem
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.set
import com.cout970.magneticraft.misc.tileentity.getCap
import com.cout970.magneticraft.misc.vector.containsPoint
import com.cout970.magneticraft.misc.vector.toVec3d
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.TUBE_CONNECTABLE
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.network.MessageTileUpdate
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.vector.extensions.distanceSq
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import java.io.ByteArrayOutputStream

class ModulePneumaticTube(
    val flow: PneumaticBoxStorage,
    val weight: Int = 0,
    override val name: String = "module_pneumatic_tube"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer
    private var preferredNextSide: EnumFacing? = null
    var changed = false
    val enabledSides = booleanArrayOf(true, true, true, true, true, true)

    override fun update() {
        val toEject = mutableListOf<PneumaticBox>()
        val removed = mutableListOf<PneumaticBox>()

        for (box in flow.getItems()) {
            if (box.progress < PneumaticBox.MAX_PROGRESS) {
                box.progress = box.progress + 16
            }

            if (box.progress < PneumaticBox.MAX_PROGRESS) continue
            changed = true

            if (box.isOutput) {
                toEject.add(box)
                continue
            }

            val previousSide = box.side
            var route = PneumaticUtils.findRoute(world, pos, box.side, box, PneumaticMode.TRAVELING, preferredNextSide)

            if (route == null) {
                route = PneumaticUtils.findRoute(world, pos, box.side, box, PneumaticMode.GOING_BACK, preferredNextSide)

                if (route == null) {
                    box.setInRoute(false)
                }
            }

            if (route != null) {
                box.setInRoute(true)
                box.side = route
                box.isOutput = true
                box.progress = 0

                preferredNextSide = EnumFacing.values()
                    .filter { it != route && it != previousSide && PneumaticUtils.canConnectToTube(world, pos, it) }
                    .shuffled(world.rand)
                    .firstOrNull()
            } else {
                toEject.add(box)
            }
        }

        for (box in toEject) {
            if (ejectItem(box, world.isClient)) {
                removed += box
            }
        }

        if (removed.isNotEmpty() && world.isServer) {
            changed = true
            removed.forEach { flow.remove(it) }
        }

        if (changed) {
            changed = false
            sendItemUpdate()
        }
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {
        if (args.heldItem.isEmpty || !WrenchRegistry.isWrench(args.heldItem)) return false
        if (world.isClient) return true

        val hitboxes = Blocks.pneumaticTubeSides(world, pos)

        hitboxes.find { (_, aabb) -> aabb.containsPoint(args.hit) }?.let { (side, _) ->
            enabledSides[side.ordinal] = !enabledSides[side.ordinal]
            container.sendUpdateToNearPlayers()
        }

        return true
    }

    fun ejectItem(box: PneumaticBox, simulate: Boolean): Boolean {
        val tube = world.getCap(TUBE_CONNECTABLE, pos.offset(box.side), box.side.opposite)

        // Add to next tube
        if (tube != null && box.hasRoute()) {
            if (!simulate) {
                tube.insert(box, PneumaticMode.TRAVELING)
            }
            return true
        }

        val inv = world.getCap(ITEM_HANDLER, pos.offset(box.side), box.side.opposite)

        // Add to inventory
        if (inv != null && box.hasRoute()) {
            if (!simulate) {
                box.item = inv.insertItem(box.item, false)
            }
            return box.item.isEmpty
        }

        // If everything else fails drop the item
        if (!simulate) {
            world.dropItem(box.item, pos)
        }
        return true
    }

    override fun receiveSyncData(ibd: IBD, otherSide: Side) {
        if (otherSide != Side.SERVER) return
        // id 0 is the packet type, for now there is only one type
        val size = ibd.getInteger(1)
        flow.clear()

        repeat(size) { index ->
            val array = ibd.getByteArray(index + 2)
            val input = CompressedStreamTools.readCompressed(array.inputStream())
            flow.insert(PneumaticBox(input))
        }
    }

    fun sendItemUpdate() {
        if (world.isClient) return

        val closePlayers = world.playerEntities
            .map { it as EntityPlayerMP }
            .filter { vec3Of(it.posX, it.posY, it.posZ).distanceSq(pos.toVec3d()) <= (32 * 32) }

        if (closePlayers.isEmpty()) return

        val data = IBD()
        val items = flow.getItems()

        // id 0 is reserved for future packet types
        data.setInteger(1, items.size)
        items.forEachIndexed { index, box ->
            val output = ByteArrayOutputStream()
            CompressedStreamTools.writeCompressed(box.serializeNBT(), output)
            data.setByteArray(index + 2, output.toByteArray())
        }

        // Custom packets are used because they are a bit smaller than regular tile entity update packets
        val packet = MessageTileUpdate(data, pos, world.provider.dimension)

        closePlayers.forEach { Magneticraft.network.sendTo(packet, it) }
    }

    override fun onBreak() {
        flow.getItems().forEach { box ->
            container.world.dropItem(box.item, container.pos)
        }
    }

    fun add(box: PneumaticBox, facing: EnumFacing) {
        box.side = facing
        box.isOutput = false
        box.progress = 0
        box.setInRoute(true)
        flow.insert(box)
        sendItemUpdate()
    }

    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        return if (cap == TUBE_CONNECTABLE && facing != null && enabledSides[facing.ordinal]) {
            object : ITube {
                override fun insert(box: PneumaticBox, mode: PneumaticMode): Boolean {
                    add(box, facing)
                    return true
                }

                override fun canInsert(box: PneumaticBox, mode: PneumaticMode): Boolean = true

                override fun canRouteItemsTo(side: EnumFacing): Boolean = enabledSides[side.ordinal]

                override fun getWeight(): Int = this@ModulePneumaticTube.weight
            } as T
        } else {
            super.getCapability(cap, facing)
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            this["flow"] = flow.serializeNBT()
            this["preferredNextSide"] = preferredNextSide
            this["enabledSides"] = enabledSides.map { if (it) 1 else 0 }.toIntArray()
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        flow.deserializeNBT(nbt.getList("flow"))
        preferredNextSide = nbt.getNullableEnumFacing("preferredNextSide")
        nbt.getIntArray("enabledSides").forEachIndexed { index, i ->
            enabledSides[index] = i != 0
        }
    }
}