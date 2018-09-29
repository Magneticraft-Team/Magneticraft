package com.cout970.magneticraft.systems.tilemodules.mining_robot

import com.cout970.magneticraft.features.computers.Blocks
import com.cout970.magneticraft.misc.inventory.canAcceptAll
import com.cout970.magneticraft.misc.inventory.insertAll
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.systems.tilemodules.ModuleRobotControl
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
        if (cooldown == action.cooldown) {
            startTick(mod)
        }
        if (cooldown == -1) {
            finish = FailReason.NO_FAIL
            endTick(mod)
        }
        if (cooldown >= 0) cooldown--
    }

    open fun startTick(mod: ModuleRobotControl) = Unit
    open fun endTick(mod: ModuleRobotControl) = Unit
}

class RotateRightTask : EndTickRobotTask(RobotAction.ROTATE_RIGHT) {

    override fun startTick(mod: ModuleRobotControl) {
        val newOr = mod.orientation.rotateY()
        mod.orientation = newOr
        mod.clientOrientation = newOr
        mod.clientCooldown = action.cooldown
        mod.container.sendUpdateToNearPlayers()
    }

    override fun endTick(mod: ModuleRobotControl) {
        mod.clientOrientation = null
    }
}

class RotateLeftTask : EndTickRobotTask(RobotAction.ROTATE_LEFT) {

    override fun startTick(mod: ModuleRobotControl) {
        val newOr = mod.orientation.rotateYCCW()
        mod.orientation = newOr
        mod.clientOrientation = newOr
        mod.clientCooldown = action.cooldown
        mod.container.sendUpdateToNearPlayers()
    }

    override fun endTick(mod: ModuleRobotControl) {
        mod.clientOrientation = null
    }
}

class RotateUpTask : EndTickRobotTask(RobotAction.ROTATE_UP) {

    override fun startTick(mod: ModuleRobotControl) {
        val newOr = Blocks.RobotOrientation.get(mod.orientation.level.up(), mod.orientation.direction)
        mod.orientation = newOr
        mod.clientOrientation = newOr
        mod.clientCooldown = action.cooldown
        mod.container.sendUpdateToNearPlayers()
    }

    override fun endTick(mod: ModuleRobotControl) {
        mod.clientOrientation = null
    }
}

class RotateDownTask : EndTickRobotTask(RobotAction.ROTATE_DOWN) {

    override fun startTick(mod: ModuleRobotControl) {
        val newOr = Blocks.RobotOrientation.get(mod.orientation.level.down(), mod.orientation.direction)
        mod.orientation = newOr
        mod.clientOrientation = newOr
        mod.clientCooldown = action.cooldown
        mod.container.sendUpdateToNearPlayers()
    }

    override fun endTick(mod: ModuleRobotControl) {
        mod.clientOrientation = null
    }
}

class MineBlockTask : EndTickRobotTask(RobotAction.MINE) {

    override fun startTick(mod: ModuleRobotControl) {
        mod.clientCooldown = action.cooldown
        mod.container.sendUpdateToNearPlayers()
    }

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

class MoveRobotTask(val front: Boolean) : EndTickRobotTask(
    if (front) RobotAction.MOVE_FRONT else RobotAction.MOVE_BACK
) {

    override fun startTick(mod: ModuleRobotControl) = mod.run {
        val newPos = pos + orientation.facing.let { if (front) it else it.opposite }

        world.setBlockState(newPos, Blocks.movingRobot.defaultState)
        mod.clientCooldown = action.cooldown
        mod.container.sendUpdateToNearPlayers()
    }

    override fun endTick(mod: ModuleRobotControl) = mod.run {
        val oldPos = pos
        val newPos = oldPos + if (front) mod.orientation.facing else mod.orientation.facing.opposite
        val tile = container.tile
        val state = container.blockState

        world.removeTileEntity(oldPos)
        world.setBlockToAir(oldPos)

        tile.validate()
        tile.pos = newPos
        world.setBlockState(newPos, state)
        world.removeTileEntity(newPos)
        world.setTileEntity(newPos, tile)

        container.markDirty()
        finish = FailReason.NO_FAIL
    }
}