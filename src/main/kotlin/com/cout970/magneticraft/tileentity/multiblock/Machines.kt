package com.cout970.magneticraft.tileentity.multiblock

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.crafting.GrinderCraftingProcess
import com.cout970.magneticraft.misc.crafting.HydraulicPressCraftingProcess
import com.cout970.magneticraft.misc.crafting.SieveCraftingProcess
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.InventoryCapabilityFilter
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.multiblock.MultiblockGrinder
import com.cout970.magneticraft.multiblock.MultiblockHydraulicPress
import com.cout970.magneticraft.multiblock.MultiblockSieve
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.modules.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos

@RegisterTileEntity("grinder")
class TileGrinder : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockGrinder

    val node = ElectricNode(ref, capacity = 8.0)

    val inventory = Inventory(3)

    val itemExporterModule = ModuleItemExporter(
            facing = { facing },
            inventory = InventoryCapabilityFilter(inventory, listOf(1, 2), listOf(1, 2)),
            ports = { listOf(BlockPos(0, 0, -3) to EnumFacing.SOUTH, BlockPos(0, 0, -3) to EnumFacing.UP) }
    )

    val openGuiModule = ModuleOpenGui()

    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
            facing = { facing },
            connectionSpots = listOf(
                    ConnectionSpot(
                            capability = ELECTRIC_NODE_HANDLER!!,
                            pos = BlockPos(1, 1, -1),
                            side = EnumFacing.EAST,
                            getter = { energyModule }
                    ),
                    ConnectionSpot(ITEM_HANDLER!!, BlockPos(0, 0, -2), EnumFacing.NORTH,
                            getter = { InventoryCapabilityFilter(inventory, emptyList(), listOf(1, 2)) }
                    )
            ) + ModuleMultiblockIO.connectionArea(ITEM_HANDLER!!, BlockPos(-1, 3, -2), BlockPos(1, 3, 0),
                    EnumFacing.UP, getter = { InventoryCapabilityFilter(inventory, listOf(0), emptyList()) }
            )
    )

    val energyModule = ModuleElectricity(
            electricNodes = listOf(node),
            canConnectAtSide = ioModule::canConnectAtSide,
            connectableDirections = ioModule::getElectricConnectPoints
    )

    val storageModule = ModuleInternalStorage(
            mainNode = node,
            capacity = 10_000,
            lowerVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
            upperVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
    )

    val invModule = ModuleInventory(
            inventory = inventory,
            capabilityFilter = ModuleInventory.ALLOW_NONE
    )

    val processModule = ModuleElectricProcessing(
            craftingProcess = GrinderCraftingProcess(invModule, 0, 1, 2),
            storage = storageModule,
            workingRate = 1f,
            costPerTick = Config.grinderMaxConsumption.toFloat()
    )

    override val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, energyModule, storageModule, processModule, invModule, openGuiModule, ioModule,
                itemExporterModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("sieve")
class TileSieve : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockSieve

    val node = ElectricNode(ref, capacity = 8.0)

    val inventory = Inventory(4)

    val itemExporterModule0 = ModuleItemExporter(
            facing = { facing },
            inventory = InventoryCapabilityFilter(inventory, listOf(1), listOf(1)),
            ports = { listOf(BlockPos(0, -1, -1) to EnumFacing.UP) }
    )

    val itemExporterModule1 = ModuleItemExporter(
            facing = { facing },
            inventory = InventoryCapabilityFilter(inventory, listOf(2), listOf(2)),
            ports = { listOf(BlockPos(0, -1, -2) to EnumFacing.UP) }
    )

    val itemExporterModule2 = ModuleItemExporter(
            facing = { facing },
            inventory = InventoryCapabilityFilter(inventory, listOf(3), listOf(3)),
            ports = { listOf(BlockPos(0, -1, -3) to EnumFacing.UP) }
    )

    val openGuiModule = ModuleOpenGui()

    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
            facing = { facing },
            connectionSpots = listOf(
                    ConnectionSpot(
                            capability = ELECTRIC_NODE_HANDLER!!,
                            pos = BlockPos(-1, 1, 0),
                            side = EnumFacing.SOUTH,
                            getter = { energyModule }
                    ),
                    ConnectionSpot(
                            capability = ELECTRIC_NODE_HANDLER!!,
                            pos = BlockPos(1, 1, 0),
                            side = EnumFacing.SOUTH,
                            getter = { energyModule }
                    ),
                    ConnectionSpot(ITEM_HANDLER!!, BlockPos(0, 1, 0), EnumFacing.UP,
                            getter = { InventoryCapabilityFilter(inventory, listOf(0), emptyList()) }
                    ),
                    ConnectionSpot(ITEM_HANDLER!!, BlockPos(0, 0, -1), EnumFacing.DOWN,
                            getter = { InventoryCapabilityFilter(inventory, emptyList(), listOf(1)) }
                    ),
                    ConnectionSpot(ITEM_HANDLER!!, BlockPos(0, 0, -2), EnumFacing.DOWN,
                            getter = { InventoryCapabilityFilter(inventory, emptyList(), listOf(2)) }
                    ),
                    ConnectionSpot(ITEM_HANDLER!!, BlockPos(0, 0, -3), EnumFacing.DOWN,
                            getter = { InventoryCapabilityFilter(inventory, emptyList(), listOf(3)) }
                    )
            )
    )

    val energyModule = ModuleElectricity(
            electricNodes = listOf(node),
            canConnectAtSide = ioModule::canConnectAtSide,
            connectableDirections = ioModule::getElectricConnectPoints
    )

    val storageModule = ModuleInternalStorage(
            mainNode = node,
            capacity = 10_000,
            lowerVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
            upperVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
    )

    val invModule = ModuleInventory(
            inventory = inventory,
            capabilityFilter = ModuleInventory.ALLOW_NONE
    )

    val processModule = ModuleElectricProcessing(
            craftingProcess = SieveCraftingProcess(invModule, 0, 1, 2, 3),
            storage = storageModule,
            workingRate = 1f,
            costPerTick = Config.sieveMaxConsumption.toFloat()
    )

    override val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, energyModule, storageModule, processModule, invModule, ioModule, openGuiModule,
                itemExporterModule0, itemExporterModule1, itemExporterModule2)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("hydraulic_press")
class TileHydraulicPress : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockHydraulicPress

    val inventory = Inventory(2)
    val node = ElectricNode(ref)

    val invModule = ModuleInventory(inventory, capabilityFilter = ModuleInventory.ALLOW_NONE)

    val storageModule = ModuleInternalStorage(
            mainNode = node,
            capacity = 10_000,
            lowerVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
            upperVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
    )

    val openGuiModule = ModuleOpenGui()

    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
            facing = { facing },
            connectionSpots = listOf(ConnectionSpot(
                    capability = ELECTRIC_NODE_HANDLER!!,
                    pos = BlockPos(-1, 1, -1),
                    side = EnumFacing.WEST,
                    getter = { if (active) energyModule else null }
            ), ConnectionSpot(
                    capability = ELECTRIC_NODE_HANDLER!!,
                    pos = BlockPos(1, 1, -1),
                    side = EnumFacing.EAST,
                    getter = { if (active) energyModule else null }
            ), ConnectionSpot(
                    capability = ITEM_HANDLER!!,
                    pos = BlockPos(0, 0, 0),
                    side = EnumFacing.SOUTH,
                    getter = { if (active) InventoryCapabilityFilter(inventory, listOf(0), listOf()) else null }
            ), ConnectionSpot(
                    capability = ITEM_HANDLER!!,
                    pos = BlockPos(0, 0, -2),
                    side = EnumFacing.NORTH,
                    getter = { if (active) InventoryCapabilityFilter(inventory, listOf(), listOf(1)) else null }
            ))
    )

    val energyModule = ModuleElectricity(
            electricNodes = listOf(node),
            canConnectAtSide = ioModule::canConnectAtSide,
            connectableDirections = ioModule::getElectricConnectPoints
    )

    val hydraulicPressModule = ModuleHydraulicPress()

    val processModule = ModuleElectricProcessing(
            costPerTick = Config.hydraulicPressMaxConsumption.toFloat(),
            workingRate = 1f,
            storage = storageModule,
            craftingProcess = HydraulicPressCraftingProcess(
                    inventory = inventory,
                    inputSlot = 0,
                    outputSlot = 1,
                    mode = hydraulicPressModule::mode
            )
    )

    val itemExporterModule = ModuleItemExporter(
            facing = { facing },
            inventory = InventoryCapabilityFilter(inventory, listOf(1), listOf(1)),
            ports = { listOf(BlockPos(0, 0, -3) to EnumFacing.SOUTH, BlockPos(0, 0, -3) to EnumFacing.UP) }
    )

    override val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, invModule, energyModule, storageModule, ioModule,
                openGuiModule, processModule, hydraulicPressModule, itemExporterModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}