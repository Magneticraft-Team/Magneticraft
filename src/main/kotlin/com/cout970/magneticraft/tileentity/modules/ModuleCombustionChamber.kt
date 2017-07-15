package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.gui.common.core.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.core.DATA_ID_MAX_BURNING_TIME
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.toKelvinFromCelsius
import net.minecraft.init.Items
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace

/**
 * Created by cout970 on 2017/07/13.
 */
class ModuleCombustionChamber(
        val invModuleInventory: ModuleInventory,
        override val name: String = "module_combustion_chamber"
) : IModule {

    override lateinit var container: IModuleContainer
    var burningTime = 0
    var maxBurningTime = 0
    var heat = 24.toKelvinFromCelsius().toFloat()

    companion object {
        @JvmStatic val HEAT_RISING_SPEED = 1f
        @JvmStatic val HEAT_FALLING_SPEED = 0.25f
        @JvmStatic val HEAT_PER_BURNING_TICK = 0.5f
    }

    override fun update() {
        if (world.isClient) return
        if (maxBurningTime > 0) {
            if (burningTime > maxBurningTime) {
                maxBurningTime = 0
                burningTime = 0
            } else {
                if (heat >= 99.toKelvinFromCelsius()) {
                    burningTime++
                    getBoiler()?.applyHeat(HEAT_PER_BURNING_TICK)
                } else {
                    heat += HEAT_RISING_SPEED
                }
            }
        }
        if (maxBurningTime <= 0) {
            val consumed = consumeFuel()
            if (!consumed && heat > 24.toKelvinFromCelsius()) {
                heat -= HEAT_FALLING_SPEED
            }
        }
    }

    fun getBoiler(): ModuleSteamBoiler? {
        val tile = world.getTile<TileBase>(pos.up()) ?: return null
        return tile.getModule<ModuleSteamBoiler>()
    }

    fun consumeFuel(): Boolean {
        maxBurningTime = 0
        val stack = invModuleInventory.inventory[0]
        if (stack.isEmpty || stack.item != Items.COAL) return false
        val time = TileEntityFurnace.getItemBurnTime(stack)
        if (time > 0) {
            stack.shrink(1)
            maxBurningTime = time
        }
        return true
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("burningTime", burningTime)
        add("maxBurningTime", maxBurningTime)
        add("heat", heat)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        burningTime = nbt.getInteger("burningTime")
        maxBurningTime = nbt.getInteger("maxBurningTime")
        heat = nbt.getFloat("heat")
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
                IntSyncVariable(DATA_ID_BURNING_TIME, { burningTime }, { burningTime = it }),
                IntSyncVariable(DATA_ID_MAX_BURNING_TIME, { maxBurningTime }, { maxBurningTime = it }),
                FloatSyncVariable(DATA_ID_MACHINE_HEAT, { heat }, { heat = it })
        )
    }
}