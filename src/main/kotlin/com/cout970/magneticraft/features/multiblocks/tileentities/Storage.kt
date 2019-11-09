package com.cout970.magneticraft.features.multiblocks.tileentities

import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockContainer
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockShelvingUnit
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.tileentity.ITickableTileEntity

@RegisterTileEntity("shelving_unit")
class TileShelvingUnit(type: TileType) : TileMultiblock(type), ITickableTileEntity {

    override fun getMultiblock(): Multiblock = MultiblockShelvingUnit

    val inventory = Inventory(ModuleShelvingUnitMb.MAX_CHESTS * 27)
    val invModule = ModuleInventory(inventory, capabilityFilter = ModuleInventory.ALLOW_NONE)

    val shelvingUnitModule = ModuleShelvingUnitMb(inventory)

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing },
        capabilityGetter = shelvingUnitModule::getCapability
    )

    init {
        initModules(multiblockModule, shelvingUnitModule, invModule)
    }

        override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("container")
class TileContainer(type: TileType) : TileMultiblock(type), ITickableTileEntity {

    override fun getMultiblock(): Multiblock = MultiblockContainer

    val stackInventoryModule = ModuleStackInventory(Config.containerMaxItemStorage)
    val openGui = ModuleOpenGui()

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing } //,
//        capabilityGetter = { cap, side, _ -> stackInventoryModule.getCapability(cap, side) }
    )

    init {
        initModules(multiblockModule, stackInventoryModule, openGui)
    }

        override fun tick() {
        super.update()
    }
}
