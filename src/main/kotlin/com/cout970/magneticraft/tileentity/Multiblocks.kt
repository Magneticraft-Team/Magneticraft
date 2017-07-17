package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.MultiblockShelvingUnit
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.multiblock.MultiblockSteamEngine
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.*
import com.cout970.magneticraft.util.interpolate
import com.cout970.magneticraft.util.vector.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 2017/07/03.
 */

@RegisterTileEntity("multiblock_gap")
class TileMultiblockGap : TileBase() {

    val multiblockModule = ModuleMultiblock()

    init {
        initModules(multiblockModule)
    }
}

abstract class TileMultiblock : TileBase() {

    val facing: EnumFacing
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.facing ?: EnumFacing.NORTH
    val active: Boolean
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: false

    @Suppress("LeakingThis")
    val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = this::getCapability
    )

    open fun getCapability(cap: Capability<*>, side: EnumFacing?, relPos: BlockPos): Any? = null

    abstract fun getMultiblock(): Multiblock

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val size = getMultiblock().size.toVec3d()
        val center = getMultiblock().center.toVec3d()
        val box = Vec3d.ZERO toAABBWith size
        val boxWithOffset = box.offset(-center)
        val normalizedBox = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), boxWithOffset)
        val alignedBox = facing.rotateBox(vec3Of(0.5), normalizedBox)
        return alignedBox.offset(pos)
    }
}


@RegisterTileEntity("solar_panel")
class TileSolarPanel : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockSolarPanel

    val node = ElectricNode(container.ref)

    val electricModule = ModuleElectricity(
            electricNodes = listOf(node),
            canConnectAtSide = this::canConnectAtSide
    )

    init {
        initModules(multiblockModule, electricModule)
    }

    override fun update() {
        super.update()
        if (world.isServer) {
            if (active && world.isDaytime && world.provider.hasSkyLight()) {
                var count = 0
                for (i in 0..2) {
                    for (j in 0..2) {
                        val offset = facing.rotatePoint(BlockPos.ORIGIN, BlockPos(i - 1, 0, j))
                        if (world.canBlockSeeSky(pos + offset)) {
                            count++
                        }
                    }
                }
                if (count > 0) {
                    val limit = interpolate(node.voltage, ElectricConstants.TIER_1_MAX_VOLTAGE,
                            ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE)
                    node.applyPower((1 - limit) * Config.solarPanelMaxProduction * (count / 9f), false)
                }
            }
        }
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean = facing?.axis != EnumFacing.Axis.Y
}

@RegisterTileEntity("shelving_unit")
class TileShelvingUnit : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockShelvingUnit

    val invModule = ModuleInventory(ModuleShelvingUnit.MAX_CHESTS * 27)

    val shelvingUnitModule = ModuleShelvingUnit()

    init {
        initModules(multiblockModule, shelvingUnitModule, invModule)
    }
}

@RegisterTileEntity("steam_engine")
class TileSteamEngine : TileMultiblock(), ITickable {
    override fun getMultiblock(): Multiblock = MultiblockSteamEngine

    val tank = Tank(16000)
    val fluidModule = ModuleFluidHandler(tank)
    val node = ElectricNode(container.ref)
    val energyModule = ModuleElectricity(
            electricNodes = listOf(node),
            connectableDirections = { emptyList() },
            capabilityFilter = { false }
    )
    val storageModule = ModuleInternalStorage(
            mainNode = node,
            capacity = 10_000,
            lowerVoltageLimit = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE,
            upperVoltageLimit = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE
    )
    val steamEngineModule = ModuleSteamEngine(tank, storageModule)

    init {
        initModules(multiblockModule, fluidModule, energyModule, storageModule, steamEngineModule)
    }

    override fun update() {
        super.update()
    }

    override fun getCapability(cap: Capability<*>, side: EnumFacing?, relPos: BlockPos): Any? {
        if (cap == ELECTRIC_NODE_HANDLER && side == EnumFacing.UP) {
            val rel = facing.opposite.rotatePoint(BlockPos.ORIGIN, BlockPos(2, 0, 2))
            if (relPos == rel) {
                return energyModule
            }
        }
        return null
    }
}
