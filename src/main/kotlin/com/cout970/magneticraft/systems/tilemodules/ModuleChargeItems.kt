package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.gui.DATA_ID_ITEM_CHARGE_RATE
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer

/**
 * Created by cout970 on 2017/07/02.
 */
class ModuleChargeItems(
    val inventory: Inventory,
    val chargeSlot: Int = -1,
    val dischargeSlot: Int = -1,
    val storage: ModuleInternalStorage,
    val transferRate: Int,
    override val name: String = "module_charge_items"
) : IModule {

    lateinit override var container: IModuleContainer
    val itemChargeRate = ValueAverage(20)

    override fun update() {
        charge()
        discharge()
        itemChargeRate.tick()
    }

    fun discharge() {
        if (dischargeSlot == -1) return
        val toDischarge = inventory[dischargeSlot]
        if (toDischarge.isEmpty) return
        val cap = FORGE_ENERGY!!.fromItem(toDischarge) ?: return

        val space = storage.capacity - storage.energy
        val amount = Math.min(space, transferRate)
        //simulated
        val taken = cap.extractEnergy(amount, true)
        if (taken > 0) {
            cap.extractEnergy(taken, false)
            storage.energy += taken
            itemChargeRate += taken
        }
    }

    fun charge() {
        if (chargeSlot == -1) return
        val toCharge = inventory[chargeSlot]
        if (toCharge.isEmpty) return
        val cap = FORGE_ENERGY!!.fromItem(toCharge) ?: return

        val amount = Math.min(storage.energy, transferRate)
        //simulated
        val given = cap.receiveEnergy(amount, true)
        if (given > 0) {
            cap.receiveEnergy(given, false)
            storage.energy -= given
            itemChargeRate -= given
        }
    }

    override fun getGuiSyncVariables(): List<SyncVariable> =
        listOf(itemChargeRate.toSyncVariable(DATA_ID_ITEM_CHARGE_RATE))
}