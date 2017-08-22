package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.MultiblockShelvingUnit
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.multiblock.MultiblockSteamEngine
import com.cout970.magneticraft.multiblock.core.Multiblock
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.*
import com.cout970.magneticraft.util.interpolate
import com.cout970.magneticraft.util.iterateArea
import com.cout970.magneticraft.util.vector.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/07/03.
 */

@RegisterTileEntity("multiblock_gap")
class TileMultiblockGap : TileBase() {

    val multiblockModule = ModuleMultiblockGap()

    init {
        initModules(multiblockModule)
    }
}

abstract class TileMultiblock : TileBase() {

    val facing: EnumFacing
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.facing ?: EnumFacing.NORTH
    val active: Boolean
        get() = getBlockState()[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: false

    abstract fun getMultiblock(): Multiblock

    @Suppress("LeakingThis")
    abstract val multiblockModule: ModuleMultiblockCenter

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

    val node = ElectricNode(container.ref, capacity = 8.0)

    val electricModule = ModuleElectricity(
            electricNodes = listOf(node),
            canConnectAtSide = this::canConnectAtSide,
            connectableDirections = this::getConnectableDirections
    )

    override val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = { _, _, _ -> null }
    )

    //client animation
    var currentAngle = 0f
    var deltaTime = System.currentTimeMillis()

    init {
        initModules(multiblockModule, electricModule)
    }

    fun getConnectableDirections(): List<Pair<BlockPos, EnumFacing>> {
        val base = ModuleElectricity.NEGATIVE_DIRECTIONS.map { it.toBlockPos() to it.opposite }
        if (world.getTile<TileSolarPanel>(pos.offset(EnumFacing.NORTH, 5)) != null) {
            return base + EnumFacing.NORTH.let { BlockPos.ORIGIN.offset(it, 5) to it.opposite }
        }
        if (world.getTile<TileSolarPanel>(pos.offset(EnumFacing.WEST, 5)) != null) {
            return base + EnumFacing.WEST.let { BlockPos.ORIGIN.offset(it, 5) to it.opposite }
        }
        return base
    }

    override fun update() {
        super.update()
        if (world.isServer) {
            if (active && world.isDaytime && world.provider.hasSkyLight()) {
                var count = 0
                iterateArea(0..2, 0..2) { i, j ->
                    val offset = facing.rotatePoint(BlockPos.ORIGIN, BlockPos(i - 1, 0, j))
                    if (world.canBlockSeeSky(pos + offset)) {
                        count++
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

    val inventory = Inventory(ModuleShelvingUnitMb.MAX_CHESTS * 27)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })

    val shelvingUnitModule = ModuleShelvingUnitMb(inventory)

    override val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = shelvingUnitModule::getCapability
    )

    init {
        initModules(multiblockModule, shelvingUnitModule, invModule)
    }

    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("steam_engine")
class TileSteamEngine : TileMultiblock(), ITickable {
    override fun getMultiblock(): Multiblock = MultiblockSteamEngine

    val tank = Tank(16000)
    val node = ElectricNode(container.ref, capacity = 8.0)

    val fluidModule = ModuleFluidHandler(tank)

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

    val steamGeneratorModule = ModuleSteamGenerator(
            steamTank = tank,
            storage = storageModule
    )

    val steamEngineMbModule = ModuleSteamEngineMb(
            facingGetter = { facing },
            energyModule = energyModule
    )

    override val multiblockModule = ModuleMultiblockCenter(
            multiblockStructure = getMultiblock(),
            facingGetter = { facing },
            capabilityGetter = steamEngineMbModule::getCapability,
            dynamicCollisionBoxes = steamEngineMbModule::getDynamicCollisionBoxes
    )

    init {
        initModules(multiblockModule, fluidModule, energyModule, storageModule, steamGeneratorModule,
                steamEngineMbModule)
    }

    override fun update() {
        super.update()
    }
}
