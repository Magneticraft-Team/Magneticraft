package com.cout970.magneticraft.features.multiblocks.tileentities

import com.cout970.magneticraft.api.internal.heat.HeatNode
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockSolarMirror
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockSolarTower
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos

@RegisterTileEntity("solar_tower")
class TileSolarTower : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockSolarTower

    val node = HeatNode(ref)

    val openGuiModule = ModuleOpenGui()

    val solarTowerModule = ModuleSolarTower(
        facingGetter = { facing },
        active = { active },
        node = node
    )

    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
        facing = { facing },
        connectionSpots = listOf(
            ConnectionSpot(HEAT_NODE_HANDLER!!, BlockPos(0, 0, -1), EnumFacing.DOWN) {
                if (active) heatModule else null
            }
        )
    )

    val heatModule = ModuleHeat(node,
        capabilityFilter = { false },
        connectableDirections = ioModule::getHeatConnectPoints
    )

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing },
        capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, ioModule, solarTowerModule, openGuiModule, heatModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("solar_mirror")
class TileSolarMirror : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockSolarMirror

    val solarMirrorModule = ModuleSolarMirror(
        facingGetter = { facing },
        active = { active }
    )

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing },
        capabilityGetter = ModuleMultiblockCenter.emptyCapabilityGetter
    )

    init {
        initModules(multiblockModule, solarMirrorModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}