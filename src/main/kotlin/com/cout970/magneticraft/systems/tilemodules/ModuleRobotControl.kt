package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.features.computers.Blocks
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.inventory.canAcceptAll
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.computer.DeviceRobotControl
import com.cout970.magneticraft.systems.computer.IMiningRobot
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tilemodules.mining_robot.*
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
    val orientationGetter: () -> Blocks.RobotOrientation,
    val orientationSetter: (Blocks.RobotOrientation) -> Unit,
    override val name: String = "module_network_card"
) : IModule, IMiningRobot {

    override lateinit var container: IModuleContainer

    override val batterySize: Int = storage.capacity
    override val batteryCharge: Int = storage.energy

    val device = DeviceRobotControl(ref, this)

    var orientation
        get() = orientationGetter()
        set(i) = orientationSetter(i)

    override val cooldown: Int
        get() = (task?.cooldown ?: -1) + 1

    override val orientationFlag: Int
        get() = (orientation.level.ordinal shl 2) or (orientation.direction.horizontalIndex)

    override var status = RequestStatus.SUCCESSFUL
    override var failReason: Int = 0
    override var scanResult: Int = 0
    var requestedAction: RobotAction? = null
    var task: RobotTask? = null
    var clientOrientation: Blocks.RobotOrientation? = null
    var clientCooldown = 0

    override fun update() {
        if (clientCooldown == 0) {
            clientOrientation = null
        }
        if (clientCooldown > 0) {
            clientCooldown--
        }
        if (world.isClient) return

        runTask()
        if (node.voltage > ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
            node.applyPower(-Config.miningRobotPassiveConsumption, false)
        }
    }

    fun updateTask() {
        requestedAction?.let {
            failReason = 0
            task = it.taskFactory()
            status = RequestStatus.RUNNING
            container.sendUpdateToNearPlayers()
        }
    }

    fun runTask() {
        task?.let {
            it.tick(this)
            it.finish?.let {
                task = null
                status = if (it == 0) RequestStatus.SUCCESSFUL else RequestStatus.FAILED
            }
        }
    }

    override fun move(front: Boolean) {
        if (status.isNotFinished) return

        val newPos = pos + orientation.facing.let { if (front) it else it.opposite }
        val state = world.getBlockState(newPos)

        if (world.isOutsideBuildHeight(newPos)) {
            status = RequestStatus.FAILED
            failReason = FailReason.BLOCKED
            return
        }

        if (state.block.canPlaceBlockAt(world, newPos)) {
            if (front) {
                requestedAction = RobotAction.MOVE_FRONT
                updateTask()
            } else {
                requestedAction = RobotAction.MOVE_BACK
                updateTask()
            }
        } else {
            status = RequestStatus.FAILED
            failReason = FailReason.BLOCKED
        }
    }

    override fun rotateLeft() {
        if (status.isNotFinished) return
        requestedAction = RobotAction.ROTATE_LEFT
        updateTask()
    }

    override fun rotateRight() {
        if (status.isNotFinished) return
        requestedAction = RobotAction.ROTATE_RIGHT
        updateTask()
    }

    override fun rotateUp() {
        if (status.isNotFinished) return
        if (orientation.level != Blocks.OrientationLevel.UP) {
            requestedAction = RobotAction.ROTATE_UP
            updateTask()
        } else {
            status = RequestStatus.FAILED
            failReason = FailReason.LIMIT_REACHED
        }
    }

    override fun rotateDown() {
        if (status.isNotFinished) return
        if (orientation.level != Blocks.OrientationLevel.DOWN) {
            requestedAction = RobotAction.ROTATE_DOWN
            updateTask()
        } else {
            status = RequestStatus.FAILED
            failReason = FailReason.LIMIT_REACHED
        }
    }

    override fun mine() {
        if (status.isNotFinished) return

        val frontPos = pos + orientation.facing
        val frontBlock = world.getBlockState(frontPos)

        if (world.isAirBlock(frontPos)) {
            status = RequestStatus.FAILED
            failReason = FailReason.AIR
            return
        }
        if (frontBlock.getBlockHardness(world, frontPos) < 0) {
            status = RequestStatus.FAILED
            failReason = FailReason.UNBREAKABLE
            return
        }
        val items = NonNullList.create<ItemStack>().also {
            frontBlock.block.getDrops(it, world, frontPos, frontBlock, 0)
        }

        if (inventory.canAcceptAll(items)) {
            requestedAction = RobotAction.MINE
            updateTask()
        } else {
            status = RequestStatus.FAILED
            failReason = FailReason.INVENTORY_FULL
        }
    }

    override fun scan() {
        if (status.isNotFinished) return

        val frontPos = pos + orientation.facing

        scanResult = if (world.isAirBlock(frontPos)) 0 else 1
        status = RequestStatus.SUCCESSFUL
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("device", device.serialize().toNBT())
        add("request", requestedAction?.ordinal ?: -1)
        add("action", task?.action?.ordinal ?: -1)
        add("cooldown", task?.cooldown ?: -1)
        add("finish", task?.finish ?: -1)
        add("requestStatus", status.ordinal)
        add("failReason", failReason)
        add("clientCooldown", clientCooldown)
        add("clientOrientation", clientOrientation?.ordinal ?: -1)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        device.deserialize(nbt.getCompoundTag("device").toMap())
        requestedAction = nbt.getInteger("request").let { if (it == -1) null else RobotAction.values()[it] }

        nbt.getInteger("action").let {
            if (it != -1) {
                val action = RobotAction.values()[it]

                task = action.taskFactory().also {
                    it.cooldown = nbt.getInteger("cooldown")
                    it.finish = nbt.getInteger("finish").let { if (it == -1) null else it }
                }
            } else {
                task = null
            }
        }

        status = RequestStatus.values()[nbt.getInteger("requestStatus")]
        failReason = nbt.getInteger("failReason")
        clientCooldown = nbt.getInteger("clientCooldown")
        clientOrientation = nbt.getInteger("clientOrientation").let {
            if (it == -1) null else Blocks.RobotOrientation.values()[it]
        }
    }
}