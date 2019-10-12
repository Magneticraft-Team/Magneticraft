package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockShelvingUnit
import com.cout970.magneticraft.features.multiblocks.tileentities.TileMultiblockGap
import com.cout970.magneticraft.features.multiblocks.tileentities.TileShelvingUnit
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.inventory.*
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import net.minecraft.block.BlockChest
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashSet

/**
 * Created by cout970 on 2017/07/05.
 */
class ModuleShelvingUnitMb(
        val inventory: Inventory,
        override val name: String = "module_shelving_unit"
) : IModule, IOnActivated {

    companion object {
        const val MAX_CHESTS = 24
        const val SLOTS_PER_CHEST = 27
        const val CHESTS_PER_LEVEL = 8
    }

    override lateinit var container: IModuleContainer
    var chestCount = intArrayOf(0, 0, 0)
    val chests = Inventory(MAX_CHESTS)
    var guiLevel = Level.BOTTOM

    override fun onActivated(args: OnActivatedArgs): Boolean {
        val stack = args.playerIn.getHeldItem(args.hand)
        if (stack.isNotEmpty) {
            val item = stack.item
            if (item is ItemBlock && item.block is BlockChest) {
                if (world.isServer) {
                    placeChest(args.pos, args.hit, stack)
                }
                return true
            }
        }
        if (!args.playerIn.isSneaking) {
            if (args.worldIn.isServer) {
                var level = getLevel(args.pos.toVec3d() + args.hit)
                var pos = this.pos
                if (level == Level.OTHER_SHELF) {
                    val other = getOtherShelf()
                    if (other != null) {
                        pos = other.pos
                        level = Level.TOP
                    } else {
                        level = Level.BOTTOM
                    }
                }

                guiLevel = level
                container.sendUpdateToNearPlayers()
                args.playerIn.openGui(Magneticraft, 0, args.worldIn, pos.xi, pos.yi, pos.zi)
            }
            return true
        } else {
            return false
        }
    }

    fun getOtherShelf(): TileShelvingUnit? {
        val tile = world.getTile<TileMultiblockGap>(pos.add(0, -1, 0)) ?: return null
        val centerPos = tile.multiblockModule.centerPos ?: return null
        return world.getTile(pos.add(0, -1, 0).subtract(centerPos))
    }

    fun placeChest(pos: BlockPos, hit: IVector3, stack: ItemStack): Boolean {
        val level = getLevel(pos.toVec3d() + hit)
        val (module, levelIndex) = findWhereToPlace(level)

        if (module.chestCount[levelIndex] < CHESTS_PER_LEVEL) {
            module.chestCount[levelIndex]++
            module.chests.insertItem(stack.withSize(1), false)
            stack.shrink(1)
            module.container.markDirty()
            module.container.sendUpdateToNearPlayers()
            return true
        }
        return false
    }

    fun findWhereToPlace(level: Level): Pair<ModuleShelvingUnitMb, Int> {
        if (level == Level.OTHER_SHELF) {
            val other = getOtherShelf()
            if (other != null) {
                return other.shelvingUnitModule to 2
            }
            return this to 0
        } else {
            return this to level.levelIndex
        }
    }

    fun sortStacks() {
        val items = mutableListOf<ItemStack>()
        repeat(inventory.size) { index ->
            if (inventory[index].isNotEmpty) {
                items += inventory[index]
            }
            inventory[index] = ItemStack.EMPTY
        }

        items.sortWith(Comparator { a, b ->
            val aId = Item.getIdFromItem(a.item)
            val bId = Item.getIdFromItem(b.item)
            val order = when {
                aId != bId -> aId - bId
                a.itemDamage != b.itemDamage -> a.itemDamage - b.itemDamage
                else -> 0
            }

            order
        })

        val queue = ArrayDeque(items)
        var count = 0

        while (queue.isNotEmpty() && count < inventory.slots) {
            val stack = queue.pollFirst()!!
            val remaining = inventory.insertItem(stack, false)

            if (remaining.isNotEmpty) {
                queue.addFirst(remaining)
            }
            count++
        }

        // If after sorting the items use more slots, the remaining items are dropped
        if (queue.isNotEmpty()) {
            queue.forEach { world.dropItem(it, pos, true) }
        }
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("level", guiLevel.ordinal)
        add("chestCount0", chestCount[0])
        add("chestCount1", chestCount[1])
        add("chestCount2", chestCount[2])
        add("chests", chests.serializeNBT())
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        guiLevel = Level.values()[nbt.getInteger("level")]
        chestCount[0] = nbt.getInteger("chestCount0")
        chestCount[1] = nbt.getInteger("chestCount1")
        chestCount[2] = nbt.getInteger("chestCount2")
        chests.deserializeNBT(nbt.getCompoundTag("chests"))
    }

    // other pos relative to this block
    fun getLevel(otherPos: IVector3): Level {
        return when (otherPos.y - pos.y) {
            in 0.0..(11 * PIXEL) -> {
                // if there are shelving units stacked one on top of another,
                // and you click the bottom part, this will act as the top of the shelving unit below
                val tile = world.getTile<TileMultiblockGap>(pos.add(0, -1, 0))
                val multiblock = tile?.multiblockModule?.multiblock

                if (multiblock == MultiblockShelvingUnit) Level.OTHER_SHELF else Level.BOTTOM
            }
            in (11 * PIXEL)..(27 * PIXEL) -> Level.BOTTOM
            in (27 * PIXEL)..(43 * PIXEL) -> Level.MIDDLE
            in (43 * PIXEL)..(48 * PIXEL) -> Level.TOP
            else -> Level.TOP
        }
    }

    fun getAvailableSlots(level: Level): IntRange {
        if (level == Level.OTHER_SHELF) return IntRange.EMPTY
        val levelNum = level.levelIndex
        if (chestCount[levelNum] == 0) return IntRange.EMPTY
        val levelStart = CHESTS_PER_LEVEL * levelNum
        val start = SLOTS_PER_CHEST * levelStart
        val chestsInThisLevel = chestCount[levelNum]
        val end = start + chestsInThisLevel * SLOTS_PER_CHEST
        return start until end
    }

    override fun onBreak() {
        chests.forEach { item ->
            world.dropItem(item, pos)
        }
    }

    fun getCapability(cap: Capability<*>, side: EnumFacing?, relPos: BlockPos): Any? {
        if (cap == ITEM_HANDLER) {
            val level: Level = when (side) {
                EnumFacing.UP -> Level.TOP
                EnumFacing.DOWN -> Level.BOTTOM
                else -> when (relPos.yi) {
                    0 -> Level.BOTTOM
                    1 -> Level.MIDDLE
                    else -> Level.TOP
                }
            }
            val slots = getAvailableSlots(level)
            return InventoryCapabilityFilter(inventory, slots.toList(), slots.toList())
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        return getCapability(cap, facing, BlockPos.ORIGIN) as? T
    }

    enum class Level(val levelIndex: Int) {
        TOP(2),
        MIDDLE(1),
        BOTTOM(0),
        OTHER_SHELF(-1)
    }
}