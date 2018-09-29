package com.cout970.magneticraft.features.multiblocks.tileentities

import com.cout970.magneticraft.features.multiblocks.structures.MultiblockContainer
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockShelvingUnit
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.util.ITickable

@RegisterTileEntity("shelving_unit")
class TileShelvingUnit : TileMultiblock(), ITickable {

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

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("container")
class TileContainer : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockContainer

    val stackInventoryModule = ModuleStackInventory(Config.containerMaxItemStorage)
    val openGui = ModuleOpenGui()

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing },
        capabilityGetter = { cap, side, _ -> stackInventoryModule.getCapability(cap, side) }
    )

    init {
        initModules(multiblockModule, stackInventoryModule, openGui)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}
