package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBuffer
import com.cout970.magneticraft.misc.tileentity.getCap
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.createAABBUsing
import com.cout970.magneticraft.misc.vector.toVec3d
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.entity.item.ItemEntity


class ModuleTransposer(
    val buffer: PneumaticBuffer,
    val itemFilter: ModuleItemFilter,
    val facing: () -> EnumFacing,
    override val name: String = "module_transposer"
) : IModule {
    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isClient || buffer.blocked || !container.shouldTick(5)) return

        val frontPos = pos.offset(facing().opposite)
        val inventory = world.getCap(ITEM_HANDLER, frontPos, facing())

        if (inventory != null) {
            for (slot in 0 until inventory.slots) {
                val stack = inventory.extractItem(slot, 64, true)
                if (stack.isEmpty) continue
                if (!itemFilter.filterAllowStack(stack)) continue

                buffer.add(inventory.extractItem(slot, 64, false))
                return
            }
            return
        }

        val start = frontPos.toVec3d()
        val end = start.add(1.0, 1.0, 1.0)
        val aabb = start.createAABBUsing(end)

        val items = world.getEntitiesWithinAABB(ItemEntity::class.java, aabb)
            .filter { it.isAlive }
            .toMutableSet()

        while (items.isNotEmpty()) {
            val target = items.first()

            if (itemFilter.filterAllowStack(target.item)) {
                buffer.add(target.item)
                target.remove()
                break
            }

            items.remove(target)
        }
    }
}