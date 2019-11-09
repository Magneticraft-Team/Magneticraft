package com.cout970.magneticraft.features.electric_machines

import com.cout970.magneticraft.*
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
import com.cout970.magneticraft.misc.ElectricConstants.TIER_1_MAX_VOLTAGE
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.block.getOrientationActive
import com.cout970.magneticraft.misc.crafting.FurnaceCraftingProcess
import com.cout970.magneticraft.misc.energy.RfStorage
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.InventoryCapabilityFilter
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.createAABBUsing
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.blocks.CommonMethods
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.Direction
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.LogicalSide

/**
 * Created by cout970 on 2017/06/29.
 */

@RegisterTileEntity("battery")
class TileBattery(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val node = ElectricNode(ref)

    val electricModule = ModuleElectricity(
        electricNodes = listOf(node),
        canConnectAtSide = this::canConnectAtSide
    )
    val storageModule = ModuleInternalStorage(
        capacity = Config.blockBatteryCapacity,
        mainNode = node,
        maxChargeSpeed = 640.0,
        upperVoltageLimit = ElectricConstants.TIER_1_BATTERY_CHARGE_VOLTAGE,
        lowerVoltageLimit = ElectricConstants.TIER_1_BATTERY_DISCHARGE_VOLTAGE
    )

    val inventory = Inventory(2)
    val invModule = ModuleInventory(inventory)

    val itemChargeModule = ModuleChargeItems(
        inventory = inventory,
        storage = storageModule,
        chargeSlot = 0,
        dischargeSlot = 1,
        transferRate = Config.blockBatteryTransferRate
    )

    init {
        initModules(electricModule, storageModule, invModule, itemChargeModule)
    }

    override fun tick() {
        super.update()
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing?.axis == Direction.Axis.Y || facing == this.facing.opposite
    }
}

@RegisterTileEntity("electric_furnace")
class TileElectricFurnace(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = getBlockState().getOrientationActive()
    val node = ElectricNode(ref)

    val electricModule = ModuleElectricity(
        electricNodes = listOf(node)
    )
    val storageModule = ModuleInternalStorage(
        capacity = 10000,
        mainNode = node
    )
    val invModule = ModuleInventory(Inventory(2), capabilityFilter = {
        InventoryCapabilityFilter(it, inputSlots = listOf(0), outputSlots = listOf(1))
    })

    val updateBlockModule = ModuleUpdateBlockstate { oldState ->
        val state = CommonMethods.OrientationActive.of(facing, processModule.working)
        oldState.withProperty(CommonMethods.PROPERTY_ORIENTATION_ACTIVE, state)
    }

    val processModule = ModuleElectricProcessing(
        craftingProcess = FurnaceCraftingProcess(invModule, 0, 1),
        storage = storageModule,
        workingRate = 1f,
        costPerTick = Config.electricFurnaceMaxConsumption.toFloat()
    )

    init {
        initModules(electricModule, storageModule, invModule, processModule, updateBlockModule)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("infinite_energy")
class TileInfiniteEnergy(type: TileType) : TileBase(type), ITickableTileEntity {

    val node = ElectricNode(ref)

    val electricModule = ModuleElectricity(
        listOf(node)
    )

    init {
        initModules(electricModule)
    }

    override fun tick() {
        node.voltage = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE
        super.update()
        node.voltage = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE
    }
}


@RegisterTileEntity("airlock")
class TileAirLock(type: TileType) : TileBase(type), ITickableTileEntity {

    val node = ElectricNode(ref)

    val electricModule = ModuleElectricity(
        electricNodes = listOf(node)
    )

    val airlockModule = ModuleAirlock(node)

    init {
        initModules(airlockModule, electricModule)
    }

    override fun tick() {
        super.update()
    }
}


@RegisterTileEntity("thermopile")
class TileThermopile(type: TileType) : TileBase(type), ITickableTileEntity {

    val node = ElectricNode(ref)

    val electricModule = ModuleElectricity(
        electricNodes = listOf(node)
    )

    val storage = ModuleInternalStorage(
        mainNode = node,
        capacity = 80_000,
        lowerVoltageLimit = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE - 5.0,
        upperVoltageLimit = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE - 5.0
    )

    val thermopileModule = ModuleThermopile(node)

    init {
        initModules(electricModule, thermopileModule, storage)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("wind_turbine")
class TileWindTurbine(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val node = ElectricNode(ref)

    val electricModule = ModuleElectricity(
        electricNodes = listOf(node)
    )
    val storageModule = ModuleInternalStorage(
        capacity = 80_000,
        mainNode = node,
        upperVoltageLimit = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE - 5,
        lowerVoltageLimit = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE - 10
    )

    val windTurbineModule = ModuleWindTurbine(
        electricNode = node,
        facingGetter = { facing }
    )

    init {
        initModules(electricModule, storageModule, windTurbineModule)
    }

    override fun tick() {
        super.update()
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return (Vec3d(-6.0, -6.0, -6.0) createAABBUsing Vec3d(6.0, 6.0, 6.0)).offset(pos)
    }
}

@RegisterTileEntity("wind_turbine_gap")
class TileWindTurbineGap(type: TileType) : TileBase(type) {

    var centerPos: BlockPos? = null

    override fun save(): NBTTagCompound = newNbt {
        if (centerPos != null) {
            add("center", centerPos!!)
        }
    }

    override fun load(nbt: NBTTagCompound) {
        if (nbt.hasKey("center")) {
            centerPos = nbt.getBlockPos("center")
        }
    }
}

@RegisterTileEntity("rf_transformer")
class TileRfTransformer(type: TileType) : TileBase(type), ITickableTileEntity {
    val storage = RfStorage(80_000)
    val node = ElectricNode(ref)

    val rfModule = ModuleRf(storage)
    val electricModule = ModuleElectricity(listOf(node))

    init {
        initModules(rfModule, electricModule)
    }

    override fun tick() {
        super.update()

        if (theWorld.isClient || storage.energyStored == 0 || node.voltage >= TIER_1_MAX_VOLTAGE) return
        val rf = Math.min(storage.energyStored, Config.rfConversionSpeed)
        node.applyPower(rf * Config.wattsToFE, false)
        storage.energyStored -= rf
    }
}

@RegisterTileEntity("electric_engine")
class TileElectricEngine(type: TileType) : TileBase(type), ITickableTileEntity {
    val facing: EnumFacing get() = getBlockState().getFacing()

    val storage = RfStorage(80_000)
    val node = ElectricNode(ref)

    val rfModule = ModuleRf(storage)
    val electricModule = ModuleElectricity(listOf(node))

    var lastWorkingTick = 0L
    var animationStep = 0.0
    var animationSpeed = 0.0
    var animationLastTime = 0.0

    init {
        initModules(rfModule, electricModule)
    }

    override fun tick() {
        super.update()

        if (theWorld.isClient) return
        storage.exportTo(theWorld, pos, facing.opposite)

        if (node.voltage < TIER_1_MACHINES_MIN_VOLTAGE) return

        val space = storage.maxEnergyStored - storage.energyStored
        if (space == 0) return

        val rf = Math.min(space, Config.electricEngineSpeed)
        node.applyPower(-rf * Config.wattsToFE, false)
        storage.energyStored += rf

        // Animation
        lastWorkingTick = theWorld.totalWorldTime
        if (container.shouldTick(10)) {
            // Packet to client
            container.sendSyncDataToNearPlayers(IBD().apply {
                setInteger(0, 0)
                setLong(1, lastWorkingTick)
            })
        }
    }

    override fun receiveSyncData(ibd: IBD, otherSide: LogicalSide) {
        val id = ibd.getInteger(0)
        if (id == 0) {
            ibd.getLong(1) {
                lastWorkingTick = it
            }
        }
        super.receiveSyncData(ibd, otherSide)
    }
}