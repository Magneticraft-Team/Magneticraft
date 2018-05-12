package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.InventoryCapabilityFilter
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.MultiblockShelvingUnit
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.multiblock.TileMultiblockGap
import com.cout970.magneticraft.tileentity.multiblock.TileShelvingUnit
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.*
import net.minecraft.block.BlockChest
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/07/05.
 */
class ModuleShelvingUnitMb(
        val inventory: Inventory,
        override val name: String = "module_shelving_unit"
) : IModule, IOnActivated {

    companion object {
        val MAX_CHESTS = 24
        val SLOTS_PER_CHEST = 27
        val CHESTS_PER_LEVEL = 8
    }

    lateinit override var container: IModuleContainer
    var chestCount = intArrayOf(0, 0, 0)

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
                args.playerIn.openGui(Magneticraft, level.ordinal, args.worldIn, pos.xi, pos.yi, pos.zi)
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
            module.container.sendUpdateToNearPlayers()
            stack.shrink(1)
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

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("chestCount0", chestCount[0])
        add("chestCount1", chestCount[1])
        add("chestCount2", chestCount[2])
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        chestCount[0] = nbt.getInteger("chestCount0")
        chestCount[1] = nbt.getInteger("chestCount1")
        chestCount[2] = nbt.getInteger("chestCount2")
    }

    // other pos relative to this block
    fun getLevel(otherPos: IVector3): Level {
        val y = otherPos.y - pos.y
        return when (y) {
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
        chestCount.forEach { count ->
            repeat(count) {
                world.dropItem(Blocks.CHEST.stack(1), pos)
            }
        }
    }

    fun getCapability(cap: Capability<*>, side: EnumFacing?, relPos: BlockPos): Any? {
        if (cap == ITEM_HANDLER) {
            val level: ModuleShelvingUnitMb.Level = when (side) {
                EnumFacing.UP -> ModuleShelvingUnitMb.Level.TOP
                EnumFacing.DOWN -> ModuleShelvingUnitMb.Level.BOTTOM
                else -> when (relPos.yi) {
                    0 -> ModuleShelvingUnitMb.Level.BOTTOM
                    1 -> ModuleShelvingUnitMb.Level.MIDDLE
                    else -> ModuleShelvingUnitMb.Level.TOP
                }
            }
            val slots = getAvailableSlots(level)
            return InventoryCapabilityFilter(inventory, slots.toList(), slots.toList())
        }
        return null
    }

    enum class Level(val levelIndex: Int) {
        TOP(2),
        MIDDLE(1),
        BOTTOM(0),
        OTHER_SHELF(-1)
    }
}