package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.Computers
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.InventoryCapabilityFilter
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.*
import com.cout970.magneticraft.util.getList
import com.cout970.magneticraft.util.getTagCompound
import com.cout970.magneticraft.util.list
import com.cout970.magneticraft.util.newNbt
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

    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory)
    val monitorModule = ModuleMonitor(container.ref)
    val floppyDriveModule = ModuleFloppyDrive(container.ref, inventory, 0)
    val networkCardModule = ModuleNetworkCard(container.ref)

    val computerModule = ModuleComputer(
            devices = mapOf(
                    0x00 to monitorModule.monitor,
                    0x01 to floppyDriveModule.drive,
                    0x02 to networkCardModule.networkCard
            )
    )

    init {
        initModules(computerModule, invModule, monitorModule, floppyDriveModule, networkCardModule)
    }

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
        get() = getBlockState()[Computers.PROPERTY_ROBOT_ORIENTATION] ?: Computers.RobotOrientation.NORTH

    val inventory = Inventory(18)
    val node = ElectricNode(container.ref)

    val invModule = ModuleInventory(inventory)
    val energyModule = ModuleElectricity(listOf(node))

    val energyStorage = ModuleInternalStorage(
            mainNode = node,
            capacity = 10000,
            upperVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE + 5
    )

    //computer
    val monitorModule = ModuleMonitor(container.ref)
    val floppyDriveModule = ModuleFloppyDrive(ref = container.ref, inventory = inventory, slot = 16)
    val networkCardModule = ModuleNetworkCard(container.ref)

    val storageInventory = InventoryCapabilityFilter(
            inventory = inventory,
            inputSlots = (0..15).toList(),
            outputSlots = (0..15).toList()
    )

    val robotControlModule = ModuleRobotControl(
            ref = container.ref,
            inventory = storageInventory,
            storage = energyStorage,
            node = node,
            orientationGetter = { orientation },
            orientationSetter = { world.setBlockState(pos, it.getBlockState(Computers.miningRobot)) }
    )

    val computerModule = ModuleComputer(
            devices = mapOf(
                    0x00 to monitorModule.monitor,
                    0x01 to floppyDriveModule.drive,
                    0x02 to networkCardModule.networkCard,
                    0x03 to robotControlModule.device
            )
    )

    init {
        initModules(computerModule, invModule, monitorModule, floppyDriveModule, networkCardModule, robotControlModule,
                energyModule, energyStorage)
    }

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

