package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.features.electric_conductors.TileEnergyReceiver
import com.cout970.magneticraft.misc.ConversionTable
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.getTileEntitiesIn
import com.cout970.magneticraft.misc.vector.createAABBUsing
import com.cout970.magneticraft.misc.vector.toBlockPos
import com.cout970.magneticraft.misc.vector.toVec3d
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.entity.player.EntityPlayer

class ModuleTeslaTower(
        val node: ElectricNode,
        override val name: String = "module_tesla_tower"
) : IModule {
    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isClient) return
        if (node.voltage < ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) return
        // Item charge rate and Receiver transmission rate
        val rate = Config.teslaTowerTransmissionRate

        // Charge items in players inventory
        val start = pos.toVec3d().addVector(-32.0, -32.0, -32.0)
        val end = pos.toVec3d().addVector(33.0, 33.0, 33.0)
        val aabb = start.createAABBUsing(end)

        world.getEntitiesWithinAABB(EntityPlayer::class.java, aabb).forEach { entity ->
            val inv = entity.inventory
            for (it in 0 until inv.sizeInventory) {
                val item = inv.getStackInSlot(it)
                if (!item.isNotEmpty) continue
                val storage = FORGE_ENERGY!!.fromItem(item) ?: continue

                val rf = storage.receiveEnergy(rate.toInt(), false)
                node.applyPower(-rf * ConversionTable.FE_TO_J, false)
                if (node.voltage < ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) return
            }
        }

        // Send energy to receivers
        val receivers = world.getTileEntitiesIn(start.toBlockPos(), end.toBlockPos())
                .filterIsInstance<TileEnergyReceiver>()
                .filter { it.node.voltage < ElectricConstants.TIER_1_MAX_VOLTAGE }

        receivers.forEach { other ->
            if (node.voltage < ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) return

            other.node.applyPower(rate, false)
            node.applyPower(-rate, false)
        }
    }
}