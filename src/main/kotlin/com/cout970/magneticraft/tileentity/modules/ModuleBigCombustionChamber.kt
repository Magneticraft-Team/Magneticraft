package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.core.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.gui.common.core.DATA_ID_MAX_BURNING_TIME
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.tileentity.WorkingIndicator
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.ConversionTable
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumParticleTypes

class ModuleBigCombustionChamber(
    val heatNode: IHeatNode,
    val inventory: Inventory,
    val maxHeat: Double,
    override val name: String = "module_big_combustion_chamber"
) : IModule {

    override lateinit var container: IModuleContainer
    var working = WorkingIndicator(this)
    var burningTime = 0
    var maxBurningTime = 0

    override fun update() {
        if (world.isClient) {
            spawnParticles()
            return
        }
        if (maxBurningTime > 0) {
            if (burningTime > maxBurningTime) {
                burningTime -= maxBurningTime
                maxBurningTime = 0
            } else {
                if (heatNode.temperature < maxHeat) {
                    val speed = Config.bigCombustionChamberMaxSpeed.toInt()
                    burningTime += speed
                    heatNode.applyHeat(ConversionTable.FUEL_TO_J * speed)
                    working.onWork()
                }
            }
        }
        while (maxBurningTime <= 0) {
            val consumed = consumeFuel()
            if (!consumed && heatNode.temperature > STANDARD_AMBIENT_TEMPERATURE) {
                heatNode.applyHeat(-10.0)
            } else {
                break
            }
        }
        working.tick()
    }

    fun spawnParticles() {
        if (inventory[0].isNotEmpty) {
            repeat(2) {
                val rand = world.rand
                val offset = (vec3Of(rand.nextFloat(), 0, rand.nextFloat()) * 2 - vec3Of(1, 0, 1)) * 0.25
                val pos = pos.toVec3d() + vec3Of(0.5, 0.2, 0.5) + offset

                val randDir = vec3Of(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
                val randDirAllDirections = randDir * vec3Of(2, 1, 2) - vec3Of(1, 0, 1)
                val dir = randDirAllDirections * 0.001 + (-offset + vec3Of(0, 1, 0)) * 0.025
                world.spawnParticle(EnumParticleTypes.FLAME, pos.x, pos.y, pos.z, dir.x, dir.y, dir.z)
            }
        }
    }

    fun consumeFuel(): Boolean {
        maxBurningTime = 0
        val stack = inventory[0]
        if (stack.isEmpty || !isValidFuel(stack)) return false
        val time = TileEntityFurnace.getItemBurnTime(stack)
        if (time > 0) {
            stack.shrink(1)
            maxBurningTime = time
        }
        return true
    }

    fun isValidFuel(stack: ItemStack) = TileEntityFurnace.getItemBurnTime(stack) > 0

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("burningTime", burningTime)
        add("maxBurningTime", maxBurningTime)
        add("working", working.serializeNBT())
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        burningTime = nbt.getInteger("burningTime")
        maxBurningTime = nbt.getInteger("maxBurningTime")
        working.deserializeNBT(nbt.getCompoundTag("working"))
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
            IntSyncVariable(DATA_ID_BURNING_TIME, { burningTime }, { burningTime = it }),
            IntSyncVariable(DATA_ID_MAX_BURNING_TIME, { maxBurningTime }, { maxBurningTime = it })
        )
    }
}