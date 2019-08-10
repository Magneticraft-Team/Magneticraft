package com.cout970.magneticraft.features.computers

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.InventoryCapabilityFilter
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.systems.computer.*
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/07/07.
 */

@RegisterTileEntity("computer")
class TileComputer : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()

    val inventory = Inventory(2)
    val invModule = ModuleInventory(inventory)
    val monitor = DeviceMonitor()
    val keyboard = DeviceKeyboard()
    val floppyDrive1Module = ModuleFloppyDrive(ref, inventory, 0)
    val floppyDrive2Module = ModuleFloppyDrive(ref, inventory, 1)
    val networkCard = DeviceNetworkCard(ref)
    val redstoneSensor = DeviceRedstoneSensor(ref)
    val computerParts = ModuleComputerDevices(monitor, keyboard, networkCard, redstoneSensor)

    val computerModule = ModuleComputer(
        internalDevices = mutableMapOf(
            0x00 to monitor,
            0x01 to floppyDrive1Module.drive,
            0x02 to keyboard,
            0x03 to networkCard,
            0x04 to redstoneSensor,
            0x05 to floppyDrive2Module.drive
        )
    )

    init {
        initModules(computerModule, invModule, computerParts, floppyDrive1Module, floppyDrive2Module)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }

    override fun saveToPacket(): NBTTagCompound {
        val moduleNbts = container.modules.filter { it !is ModuleComputer }.map { it.serializeNBT() }
        if (moduleNbts.isNotEmpty()) {
            return newNbt {
                list("_modules") {
                    moduleNbts.forEach { appendTag(it) }
                }
            }
        }
        return NBTTagCompound()
    }

    override fun loadFromPacket(nbt: NBTTagCompound) {
        if (nbt.hasKey("_modules")) {
            val list = nbt.getList("_modules")
            container.modules.filter { it !is ModuleComputer }.forEachIndexed { index, module ->
                module.deserializeNBT(list.getTagCompound(index))
            }
        }
    }
}

@RegisterTileEntity("mining_robot")
class TileMiningRobot : TileBase(), ITickable {

    val orientation
        get() = getBlockState()[Blocks.PROPERTY_ROBOT_ORIENTATION] ?: Blocks.RobotOrientation.NORTH

    val inventory = Inventory(18)
    val node = ElectricNode(ref, capacity = 0.5)

    val storageInventory = InventoryCapabilityFilter(
        inventory = inventory,
        inputSlots = (0..15).toList(),
        outputSlots = (0..15).toList()
    )

    val invModule = ModuleInventory(
        inventory = inventory,
        capabilityFilter = { storageInventory })

    val energyModule = ModuleElectricity(listOf(node))

    val energyStorage = ModuleInternalStorage(
        mainNode = node,
        capacity = 50000,
        maxChargeSpeed = 50.0,
        upperVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE + 5
    )

    val itemChargeModule = ModuleChargeItems(
        inventory = inventory,
        storage = energyStorage,
        chargeSlot = -1,
        dischargeSlot = 17,
        transferRate = Config.blockBatteryTransferRate
    )

    val floppyDriveModule = ModuleFloppyDrive(ref = ref, inventory = inventory, slot = 16)
    val keyboard = DeviceKeyboard()
    val monitor = DeviceMonitor()
    val networkCard = DeviceNetworkCard(ref)
    val redstoneSensor = DeviceRedstoneSensor(ref)
    val inventorySensor = DeviceInventorySensor(ref, inventory)
    val computerParts = ModuleComputerDevices(monitor, keyboard, networkCard, redstoneSensor, inventorySensor)

    val robotControlModule = ModuleRobotControl(
        ref = ref,
        inventory = storageInventory,
        storage = energyStorage,
        node = node,
        orientationGetter = { orientation },
        orientationSetter = { world.setBlockState(pos, it.getBlockState(Blocks.miningRobot)) }
    )

    val computerModule = ModuleComputer(
        internalDevices = mutableMapOf(
            0x00 to monitor,
            0x01 to floppyDriveModule.drive,
            0x02 to keyboard,
            0x03 to networkCard,
            0x04 to robotControlModule.device,
            0x05 to redstoneSensor,
            0x06 to inventorySensor
        )
    )

    init {
        initModules(computerModule, invModule, computerParts, floppyDriveModule, robotControlModule,
            energyModule, energyStorage, itemChargeModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }

    override fun saveToPacket(): NBTTagCompound {
        val moduleNbts = container.modules.filter { it !is ModuleComputer }.map { it.serializeNBT() }
        if (moduleNbts.isNotEmpty()) {
            return newNbt {
                list("_modules") {
                    moduleNbts.forEach { appendTag(it) }
                }
            }
        }
        return NBTTagCompound()
    }

    override fun loadFromPacket(nbt: NBTTagCompound) {
        if (nbt.hasKey("_modules")) {
            val list = nbt.getList("_modules")
            container.modules.filter { it !is ModuleComputer }.forEachIndexed { index, module ->
                module.deserializeNBT(list.getTagCompound(index))
            }
        }
    }
}

