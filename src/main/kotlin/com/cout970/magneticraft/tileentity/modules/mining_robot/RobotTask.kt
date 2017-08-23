package com.cout970.magneticraft.tileentity.modules.mining_robot

import com.cout970.magneticraft.block.Computers
import com.cout970.magneticraft.misc.inventory.canAcceptAll
import com.cout970.magneticraft.misc.inventory.insertAll
import com.cout970.magneticraft.tileentity.modules.ModuleRobotControl
import com.cout970.magneticraft.util.vector.plus
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * Created by cout970 on 2017/08/23.
 */
abstract class RobotTask(val action: RobotAction) {
    var cooldown: Int = action.cooldown
    var finish: Int? = null

    abstract fun tick(mod: ModuleRobotControl)
}

abstract class EndTickRobotTask(action: RobotAction) : RobotTask(action) {

    override fun tick(mod: ModuleRobotControl) {
        if (cooldown >= 0) cooldown--
        if (cooldown != -1) return
        finish = FailReason.NO_FAIL
        endTick(mod)
    }

    abstract fun endTick(mod: ModuleRobotControl)
}

class RotateRightTask : EndTickRobotTask(RobotAction.ROTATE_RIGHT) {

    override fun endTick(mod: ModuleRobotControl) {
        mod.orientation = mod.orientation.rotateY()
    }
}

class RotateLeftTask : EndTickRobotTask(RobotAction.ROTATE_LEFT) {

    override fun endTick(mod: ModuleRobotControl) {
        mod.orientation = mod.orientation.rotateYCCW()
    }
}

class RotateUpTask : EndTickRobotTask(RobotAction.ROTATE_UP) {

    override fun endTick(mod: ModuleRobotControl) {
        mod.orientation = Computers.RobotOrientation.get(mod.orientation.level.up(), mod.orientation.direction)
    }
}

class RotateDownTask : EndTickRobotTask(RobotAction.ROTATE_DOWN) {

    override fun endTick(mod: ModuleRobotControl) {
        mod.orientation = Computers.RobotOrientation.get(mod.orientation.level.down(), mod.orientation.direction)
    }
}

class MineBlockTask : EndTickRobotTask(RobotAction.MINE) {

    override fun endTick(mod: ModuleRobotControl) = mod.run {
        val frontPos = pos + orientation.facing
        val frontBlock = world.getBlockState(frontPos)
        if (!world.isAirBlock(frontPos) && frontBlock.getBlockHardness(world, frontPos) >= 0) {

            val items = NonNullList.create<ItemStack>().also {
                frontBlock.block.getDrops(it, world, frontPos, frontBlock, 0)
            }

            if (inventory.canAcceptAll(items)) {
                inventory.insertAll(items)
                world.destroyBlock(frontPos, false)
                finish = FailReason.NO_FAIL
                return
            } else {
                finish = FailReason.INVENTORY_FULL
                return
            }
        }
        finish = FailReason.UNBREAKABLE
    }
}

class MoveRobotTask(val front: Boolean) : RobotTask(if (front) RobotAction.MOVE_FRONT else RobotAction.MOVE_BACK) {

    override fun tick(mod: ModuleRobotControl) {
        if (cooldown == action.cooldown) {
            moveStart(mod)
        } else if (cooldown == -1) {
            moveEnd(mod)
            finish = FailReason.NO_FAIL
        }
        if (cooldown >= 0) cooldown--
    }

    fun moveStart(mod: ModuleRobotControl) = mod.apply {
        val newPos = pos + if (front) mod.orientation.facing else mod.orientation.facing.opposite

        world.setBlockState(newPos, container.blockState)
        world.removeTileEntity(newPos)
    }

    fun moveEnd(mod: ModuleRobotControl) = mod.apply {
        val oldPos = pos
        val newPos = oldPos + if (front) mod.orientation.facing else mod.orientation.facing.opposite
        val tile = container.tile

        world.removeTileEntity(oldPos)
        world.setBlockToAir(oldPos)

        tile.validate()
        tile.pos = newPos
        world.setTileEntity(newPos, tile)

        container.sendUpdateToNearPlayers()
        container.markDirty()
    }
}