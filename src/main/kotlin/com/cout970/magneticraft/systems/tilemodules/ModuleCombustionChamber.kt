package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.withSize
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.blocks.*
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.systems.gui.DATA_ID_MAX_BURNING_TIME
import com.cout970.magneticraft.systems.integration.ItemHolder
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumParticleTypes

/**
 * Created by cout970 on 2017/07/13.
 */
class ModuleCombustionChamber(
    val node: IHeatNode,
    val inventory: Inventory,
    override val name: String = "module_combustion_chamber"
) : IModule, IOnActivated {

    override lateinit var container: IModuleContainer
    var burningTime = 0
    var maxBurningTime = 0
    var doorOpen = false

    companion object {
        @JvmStatic
        val MAX_HEAT = 600.fromCelsiusToKelvin()
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {
        val block = container.blockState.block

        val boxes = (block as? BlockBase)
            ?.aabb
            ?.invoke(BoundingBoxArgs(container.blockState, world, pos))
            ?: emptyList()

        val index = boxes.indexOfFirst { it.isHitBy(args.hit) }
        if (index != 2) {
            return if (Config.allowCombustionChamberGui) {
                CommonMethods.openGui(args)
            } else {
                false
            }
        } else {
            if (doorOpen && isValidFuel(args.heldItem)) {
                val space = 64 - inventory[0].count
                val toMove = Math.min(args.heldItem.count, space)
                if (toMove > 0) {
                    val notMoved = inventory.insertItem(0, args.heldItem.withSize(toMove), false)
                    args.heldItem.shrink(toMove - notMoved.count)
                }
            } else {
                doorOpen = !doorOpen
                container.sendUpdateToNearPlayers()
            }
            return true
        }
    }

    fun spawnParticles() {
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
    }

    override fun update() {
        if (world.isClient) {
            spawnParticles()
            return
        }
        if (maxBurningTime > 0) {
            if (burningTime > maxBurningTime) {
                maxBurningTime = 0
                burningTime = 0

            } else {
                if (node.temperature < MAX_HEAT) {
                    val speed = ((if (doorOpen) 0.5f else 1f) * Config.combustionChamberMaxSpeed.toFloat()).toInt()
                    burningTime += speed
                    node.applyHeat(ConversionTable.FUEL_TO_J * speed)
                }
            }
        }
        if (maxBurningTime <= 0) {
            val consumed = consumeFuel()
            if (!consumed && node.temperature > STANDARD_AMBIENT_TEMPERATURE) {
                node.applyHeat(-10.0)
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

    fun isValidFuel(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        // vanilla
        if (stack.item == Items.COAL) return true
        if (stack.item == Item.getItemFromBlock(Blocks.COAL_BLOCK)) return true
        // other mods
        ItemHolder.coalCoke?.let { if (it.isItemEqual(stack)) return true }
        ItemHolder.coalCokeBlock?.let { if (it.isItemEqual(stack)) return true }
        return false
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("burningTime", burningTime)
        add("maxBurningTime", maxBurningTime)
        add("doorOpen", doorOpen)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        burningTime = nbt.getInteger("burningTime")
        maxBurningTime = nbt.getInteger("maxBurningTime")
        doorOpen = nbt.getBoolean("doorOpen")
    }

    override fun getGuiSyncVariables(): List<SyncVariable> {
        return listOf(
            IntSyncVariable(DATA_ID_BURNING_TIME, { burningTime }, { burningTime = it }),
            IntSyncVariable(DATA_ID_MAX_BURNING_TIME, { maxBurningTime }, { maxBurningTime = it })
        )
    }
}