package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.block.core.*
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.common.core.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.core.DATA_ID_MAX_BURNING_TIME
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.toKelvinFromCelsius
import com.cout970.magneticraft.util.vector.*
import net.minecraft.init.Items
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumParticleTypes

/**
 * Created by cout970 on 2017/07/13.
 */
class ModuleCombustionChamber(
        val inventory: Inventory,
        override val name: String = "module_combustion_chamber"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer
    var burningTime = 0
    var maxBurningTime = 0
    var heat = 24.toKelvinFromCelsius().toFloat()
    var doorOpen = false

    companion object {
        @JvmStatic val HEAT_RISING_SPEED = 1f
        @JvmStatic val HEAT_FALLING_SPEED = 0.25f
        @JvmStatic val HEAT_PER_BURNING_TICK = 0.5f
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {
        val block = container.blockState.block

        val boxes = (block as? BlockBase)
                            ?.aabb
                            ?.invoke(BoundingBoxArgs(container.blockState, world, pos))
                    ?: emptyList()

        val index = boxes.indexOfFirst { it.isHitBy(args.hit) }
        if (index != 2) {
            if (Config.allowCombustionChamberGui) {
                return CommonMethods.openGui(args)
            } else {
                return false
            }
        } else {
            if (doorOpen && args.heldItem.item == Items.COAL) {
                val space = 64 - inventory[0].count
                val toMove = Math.min(args.heldItem.count, space)
                if (toMove > 0) {
                    val notMoved = inventory.insertItem(0, Items.COAL.stack(toMove), false)
                    args.heldItem.shrink(toMove - notMoved.count)
                }
            } else {
                doorOpen = !doorOpen
                container.sendUpdateToNearPlayers()
            }
            return true
        }
    }

    override fun update() {
        if (world.isClient) {
            if (doorOpen && inventory[0].isNotEmpty) {
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
            return
        }
        if (maxBurningTime > 0) {
            if (burningTime > maxBurningTime) {
                maxBurningTime = 0
                burningTime = 0
            } else {
                if (heat >= 99.toKelvinFromCelsius()) {
                    val speed = if(doorOpen) 2 else 4
                    burningTime += speed
                    getBoiler()?.applyHeat(HEAT_PER_BURNING_TICK * speed)
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
        return world.getModule<ModuleSteamBoiler>(pos.up())
    }

    fun consumeFuel(): Boolean {
        maxBurningTime = 0
        val stack = inventory[0]
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
        add("doorOpen", doorOpen)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        burningTime = nbt.getInteger("burningTime")
        maxBurningTime = nbt.getInteger("maxBurningTime")
        heat = nbt.getFloat("heat")
        doorOpen = nbt.getBoolean("doorOpen")
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
                IntSyncVariable(DATA_ID_BURNING_TIME, { burningTime }, { burningTime = it }),
                IntSyncVariable(DATA_ID_MAX_BURNING_TIME, { maxBurningTime }, { maxBurningTime = it }),
                FloatSyncVariable(DATA_ID_MACHINE_HEAT, { heat }, { heat = it })
        )
    }
}