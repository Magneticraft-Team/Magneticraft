package com.cout970.magneticraft.tileentity.multiblock

import com.cout970.magneticraft.api.internal.heat.HeatNode
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.fluid.TankCapabilityFilter
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.multiblock.MultiblockSolarMirror
import com.cout970.magneticraft.multiblock.MultiblockSolarTower
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.tileentity.modules.*
import com.cout970.magneticraft.util.vector.rotatePoint
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos

@RegisterTileEntity("solar_tower")
class TileSolarTower : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockSolarTower

    val node = HeatNode(ref)

    val waterTank = Tank(8000).apply { clientFluidName = "water" }
    val steamTank = Tank(32000).apply { clientFluidName = "steam" }

    val heatModule = ModuleHeat(listOf(node))
    val fluidModule = ModuleFluidHandler(waterTank, steamTank, capabilityFilter = ModuleFluidHandler.ALLOW_NONE)

    val openGuiModule = ModuleOpenGui()

    val steamBoilerModule = ModuleSteamBoiler(node, waterTank, steamTank, 1200f, 1200)

    val solarTowerModule = ModuleSolarTower(
            facingGetter = { facing },
            node = node
    )

    val fluidExportModule = ModuleFluidExporter(steamTank, {
        listOf(facing.rotatePoint(BlockPos.ORIGIN, BlockPos(1, -1, -1)) to EnumFacing.UP)
    })


    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
            facing = { facing },
            connectionSpots = listOf(
                    ConnectionSpot(FLUID_HANDLER!!, BlockPos(-1, 0, -1), EnumFacing.DOWN) {
                        TankCapabilityFilter(waterTank, true, true)
                    },
                    ConnectionSpot(FLUID_HANDLER!!, BlockPos(1, 0, -1), EnumFacing.DOWN) {
                        TankCapabilityFilter(steamTank, false, true)
                    }
            )
    )

    override val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, fluidModule, ioModule, solarTowerModule, steamBoilerModule, openGuiModule,
                fluidExportModule, heatModule)
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
            facingGetter = { facing }
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