package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.inventory.forEachIndexed
import com.cout970.magneticraft.misc.inventory.insertItem
import com.cout970.magneticraft.misc.inventory.isEmpty
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.getRelative
import com.cout970.magneticraft.misc.vector.rotatePoint
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.IItemHandler

class ModuleItemExporter(
    val facing: () -> EnumFacing,
    val inventory: IItemHandler,
    val ports: () -> List<Pair<BlockPos, EnumFacing>>, // Facing is the side of the block to fill
    override val name: String = "module_item_exporter"
) : IModule {

    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isClient) return
        if (!container.shouldTick(20)) return
        if (inventory.isEmpty()) return

        val facing = facing()
        for ((off, dir) in ports()) {
            val tile = world.getTileEntity(pos.add(facing.rotatePoint(BlockPos.ORIGIN, off))) ?: continue
            val handler = tile.getOrNull(ITEM_HANDLER, facing.getRelative(dir)) ?: continue

            inventory.forEachIndexed { i, stack ->
                val itemStack = inventory.extractItem(i, stack.count, false)
                val remaining = handler.insertItem(itemStack, false)
                inventory.insertItem(i, remaining, false)
            }
        }
    }
}