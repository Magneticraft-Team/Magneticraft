package com.cout970.magneticraft.api.internal.pneumatic

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.pneumatic.ITube
import com.cout970.magneticraft.api.pneumatic.PneumaticBox
import com.cout970.magneticraft.api.pneumatic.PneumaticMode
import com.cout970.magneticraft.misc.inventory.insertItem
import com.cout970.magneticraft.misc.tileentity.getCap
import com.cout970.magneticraft.misc.vector.toVec3d
import com.cout970.magneticraft.misc.vector.xd
import com.cout970.magneticraft.misc.vector.yd
import com.cout970.magneticraft.misc.vector.zd
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.TUBE_CONNECTABLE
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.systems.tilerenderers.px
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

object PneumaticUtils {

    fun canConnectToTube(world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        val here = pos.offset(side)

        return world.getCap(TUBE_CONNECTABLE, here, side.opposite) != null
            || world.getCap(ITEM_HANDLER, here, side.opposite) != null
    }

    fun handleItemEjection(ref: ITileRef, box: PneumaticBox, facing: EnumFacing): Boolean {
        if (box.item.isEmpty) {
            return true
        }

        if (addToRoute(ref, facing, box)) {
            return true
        }

        if (addToInventory(ref, facing, box)) {
            return true
        }

        val neighbour = ref.pos.offset(facing)
        val hasTile = ref.world.getTileEntity(neighbour) != null
        val isSolid = ref.world.getBlockState(neighbour).isSideSolid(ref.world, neighbour, facing.opposite)

        if (!hasTile && !isSolid) {
            dropItemToGround(ref.world, neighbour, box, facing)
            return true
        }

        return false
    }

    fun dropItemToGround(world: World, pos: BlockPos, box: PneumaticBox, facing: EnumFacing) {
        world.dropItem(box.item, pos, false) { entity ->
            val dir = facing.directionVec.toVec3d()

            entity.setPositionAndRotation(
                pos.xd + (0.5 - dir.x * 5.px),
                pos.yd + (0.5 - dir.y * 5.px) - 2.px,
                pos.zd + (0.5 - dir.z * 5.px),
                0.0f, 0.0f
            )

            entity.motionX = dir.x * 0.2
            entity.motionY = dir.y * 0.2
            entity.motionZ = dir.z * 0.2
        }
    }

    fun addToInventory(ref: ITileRef, facing: EnumFacing, box: PneumaticBox): Boolean {
        val neighbour = ref.pos.offset(facing)
        val handler = ref.world.getCap(ITEM_HANDLER, neighbour, facing.opposite) ?: return false

        return if (handler.insertItem(box.item, true).isEmpty) {
            handler.insertItem(box.item, false)
            true
        } else {
            false
        }
    }

    fun addToRoute(ref: ITileRef, facing: EnumFacing, box: PneumaticBox): Boolean {
        val tube = ref.world.getCap(TUBE_CONNECTABLE, ref.pos.offset(facing), facing.opposite) ?: return false
        val route = findRoute(ref.world, ref.pos.offset(facing), facing.opposite, box, PneumaticMode.TRAVELING)

        return if (route != null) tube.insert(box, PneumaticMode.TRAVELING) else false
    }

    fun findRoute(world: World, start: BlockPos, originSide: EnumFacing, box: PneumaticBox, mode: PneumaticMode, priorityDir: EnumFacing? = null): EnumFacing? {
        val queue = PriorityQueue<RouteNode>()
        val scanned = mutableSetOf<BlockPos>()

        fun addPoint(point: BlockPos, initialSide: EnumFacing, fromSide: EnumFacing, weight: Int) {

            val tile = world.getTileEntity(point) ?: return
            val inventory = ITEM_HANDLER!!.fromTile(tile, fromSide.opposite)

            if (inventory != null && inventory.insertItem(box.item, true).isEmpty) {
                queue.add(RouteNode(point, initialSide, fromSide, weight, true))
                return
            }

            val tube = TUBE_CONNECTABLE!!.fromTile(tile, fromSide.opposite) ?: return

            if (tube is ITube) {
                if (point !in scanned) {
                    scanned.add(point)
                    queue.add(RouteNode(point, initialSide, fromSide, weight + tube.weight))
                }
                return
            }

            if (tube.canInsert(box, mode)) {
                queue.add(RouteNode(point, initialSide, fromSide, weight + tube.weight, true))
            }
        }

        val thisTube = world.getCap(TUBE_CONNECTABLE, start, originSide) as? ITube

        for (dir in EnumFacing.VALUES) {
            if(thisTube != null && !thisTube.canRouteItemsTo(dir)) continue
            if (dir != originSide || mode == PneumaticMode.GOING_BACK) {
                addPoint(start.offset(dir), dir, dir, if (dir == priorityDir) 0 else 1)
            }
        }

        while (queue.isNotEmpty()) {
            val route = queue.poll()

            if (route.solved) {
                return route.initialSide
            }

            val handler = world.getCap(TUBE_CONNECTABLE, route.pos, route.fromSide.opposite) as? ITube
                ?: continue

            for (dir in EnumFacing.VALUES) {
                if (handler.canRouteItemsTo(dir) && dir != route.fromSide.opposite) {
                    addPoint(route.pos.offset(dir), route.initialSide, dir, route.weight + 2)
                }
            }
        }

        return null
    }

    private data class RouteNode(
        val pos: BlockPos,
        val initialSide: EnumFacing,
        val fromSide: EnumFacing,
        val weight: Int,
        val solved: Boolean = false
    ) : Comparable<RouteNode> {
        override fun compareTo(other: RouteNode): Int = this.weight - other.weight
    }
}