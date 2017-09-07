package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.Computers
import com.cout970.magneticraft.computer.DeviceRobotControl
import com.cout970.magneticraft.computer.IMiningRobot
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.inventory.canAcceptAll
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.modules.mining_robot.*
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.plus
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraftforge.items.IItemHandler

/**
 * Created by cout970 on 2017/08/22.
 */
class ModuleRobotControl(
        val ref: ITileRef,
        val inventory: IItemHandler,
        val storage: ModuleInternalStorage,
        val node: ElectricNode,
        val orientationGetter: () -> Computers.RobotOrientation,
        val orientationSetter: (Computers.RobotOrientation) -> Unit,
        override val name: String = "module_network_card"
) : IModule, IMiningRobot {

    override lateinit var container: IModuleContainer

    override val batterySize: Int = storage.capacity
    override val batteryCharge: Int = storage.energy

    var orientation
        get() = orientationGetter()
        set(i) = orientationSetter(i)

    val device = DeviceRobotControl(ref, this)

    override var requestedAction: RobotAction? = null
    override var requestStatus: RequestStatus = RequestStatus.SUCCESSFUL
    override var failReason: Int = 0

    var task: RobotTask? = null
    override val cooldown: Int get() = (task?.cooldown ?: -1) + 1

    var clientOrientation: Computers.RobotOrientation? = null
    var clientCooldown = 0

    override fun update() {
        if (clientCooldown == 0) {
            clientOrientation = null
        }
        if (clientCooldown > 0) {
            clientCooldown--
        }
        if (world.isClient) return

        requestedAction?.let {
            if (requestStatus == RequestStatus.PENDING) {
                failReason = 0
                task = it.taskFactory()
                requestStatus = RequestStatus.RUNNING
                container.sendUpdateToNearPlayers()
            }
        }
        runTask()
        if (node.voltage > ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
            node.applyPower(-Config.miningRobotPassiveConsumption, false)
        }
    }

    fun runTask() {
        task?.let {
            it.tick(this)
            it.finish?.let {
                task = null
                requestStatus = if (it == 0) RequestStatus.SUCCESSFUL else RequestStatus.FAILED
            }
        }
    }

    override fun move(front: Boolean) {
        if (requestStatus.isNotFinished) return

        val newPos = pos + if (front) orientation.facing else orientation.facing.opposite
        val state = world.getBlockState(newPos)

        if (state.block.canPlaceBlockAt(world, newPos)) {
            if (front) {
                requestedAction = RobotAction.MOVE_FRONT
                requestStatus = RequestStatus.PENDING
            } else {
                requestedAction = RobotAction.MOVE_BACK
                requestStatus = RequestStatus.PENDING
            }
        } else {
            requestStatus = RequestStatus.FAILED
            failReason = FailReason.BLOCKED
        }
    }

    override fun rotateLeft() {
        if (requestStatus.isNotFinished) return
        requestedAction = RobotAction.ROTATE_LEFT
        requestStatus = RequestStatus.PENDING
    }

    override fun rotateRight() {
        if (requestStatus.isNotFinished) return
        requestedAction = RobotAction.ROTATE_RIGHT
        requestStatus = RequestStatus.PENDING
    }

    override fun rotateUp() {
        if (requestStatus.isNotFinished) return
        if (orientation.level != Computers.OrientationLevel.UP) {
            requestedAction = RobotAction.ROTATE_UP
            requestStatus = RequestStatus.PENDING
        } else {
            requestStatus = RequestStatus.FAILED
            failReason = FailReason.LIMIT_REACHED
        }
    }

    override fun rotateDown() {
        if (requestStatus.isNotFinished) return
        if (orientation.level != Computers.OrientationLevel.DOWN) {
            requestedAction = RobotAction.ROTATE_DOWN
            requestStatus = RequestStatus.PENDING
        } else {
            requestStatus = RequestStatus.FAILED
            failReason = FailReason.LIMIT_REACHED
        }
    }

    override fun mine() {
        if (requestStatus.isNotFinished) return

        val frontPos = pos + orientation.facing
        val frontBlock = world.getBlockState(frontPos)

        if (world.isAirBlock(frontPos)) {
            requestStatus = RequestStatus.FAILED
            failReason = FailReason.AIR
            return
        }
        if (frontBlock.getBlockHardness(world, frontPos) < 0) {
            requestStatus = RequestStatus.FAILED
            failReason = FailReason.UNBREAKABLE
            return
        }
        val items = NonNullList.create<ItemStack>().also {
            frontBlock.block.getDrops(it, world, frontPos, frontBlock, 0)
        }

        if (inventory.canAcceptAll(items)) {
            requestedAction = RobotAction.MINE
            requestStatus = RequestStatus.PENDING
        } else {
            requestStatus = RequestStatus.FAILED
            failReason = FailReason.INVENTORY_FULL
        }
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("device", device.serializeNBT())
        add("request", requestedAction?.ordinal ?: -1)
        add("action", task?.action?.ordinal ?: -1)
        add("cooldown", task?.cooldown ?: -1)
        add("requestStatus", requestStatus.ordinal)
        add("failReason", failReason)
        add("clientCooldown", clientCooldown)
        add("clientOrientation", clientOrientation?.ordinal ?: -1)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        device.deserializeNBT(nbt.getCompoundTag("device"))
        requestedAction = nbt.getInteger("request").let { if (it == -1) null else RobotAction.values()[it] }

        val action = nbt.getInteger("action").let { if (it == -1) null else RobotAction.values()[it] }
        task = action?.taskFactory?.invoke()?.also { it.cooldown = nbt.getInteger("cooldown") }

        requestStatus = RequestStatus.values()[nbt.getInteger("requestStatus")]
        failReason = nbt.getInteger("failReason")
        clientCooldown = nbt.getInteger("clientCooldown")
        clientOrientation = nbt.getInteger("clientOrientation").let {
            if (it == -1) null else Computers.RobotOrientation.values()[it]
        }
    }
}