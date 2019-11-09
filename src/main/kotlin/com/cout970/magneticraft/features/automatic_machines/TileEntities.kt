package com.cout970.magneticraft.features.automatic_machines

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBoxStorage
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBuffer
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.block.FULL_BLOCK_AABB
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.block.getOrientationCentered
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.fluid.fillExecute
import com.cout970.magneticraft.misc.fluid.fillSimulate
import com.cout970.magneticraft.misc.fluid.stack
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.TimeCache
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.fluid.Fluids
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fluids.capability.IFluidHandler

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("feeding_trough")
class TileFeedingTrough(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getOrientationCentered()

    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val moduleFeedingTrough = ModuleFeedingTrough(inventory)

    init {
        initModules(moduleFeedingTrough, invModule)
    }

    override fun tick() {
        super.update()
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val dir = facing.toBlockPos()
        return super.getRenderBoundingBox().expand(dir.xd, dir.yd, dir.zd)
    }
}


@RegisterTileEntity("conveyor_belt")
class TileConveyorBelt(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getOrientation()
    val conveyorModule = ModuleConveyorBelt({ facing })

    init {
        initModules(conveyorModule)
    }

    override fun tick() {
        super.update()
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return pos createAABBUsing pos.add(1, 2, 1)
    }
}

@RegisterTileEntity("inserter")
class TileInserter(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getOrientation()
    val filters = Inventory(9)
    val inventory: Inventory = Inventory(3) { _, _ -> inserterModule.updateUpgrades() }
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val openGui = ModuleOpenGui()
    val inserterModule = ModuleInserter({ facing }, inventory, filters)

    init {
        initModules(inserterModule, invModule, openGui)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("water_generator")
class TileWaterGenerator(type: TileType) : TileBase(type), ITickableTileEntity {

    val cache: MutableList<IFluidHandler> = mutableListOf()
    val tank = object : Tank(32_000) {
        init {
            onContentsChanged()
        }

        override fun onContentsChanged() {
            fluid = Fluids.WATER.stack(32_000)
        }
    }

    val fluidModule = ModuleFluidHandler(tank)
    val bucketIoModule = ModuleBucketIO(tank)

    init {
        initModules(bucketIoModule, fluidModule)
    }

    override fun tick() {
        super.update()
        if (container.shouldTick(20)) {
            cache.clear()
            enumValues<EnumFacing>().forEach { dir ->
                val tile = theWorld.getTileEntity(pos.offset(dir))
                val handler = tile?.getOrNull(FLUID_HANDLER, dir.opposite) ?: return@forEach
                cache.add(handler)
            }
        }
        cache.forEach { handler ->
            val water = Fluids.WATER.stack(Config.waterGeneratorPerTickWater)
            val amount = handler.fillSimulate(water)
            if (amount > 0) {
                handler.fillExecute(water.stack(amount))
            }
        }
    }
}

abstract class AbstractTileTube(type: TileType) : TileBase(type) {

    val flow = PneumaticBoxStorage()

    @Suppress("LeakingThis")
    val tubeModule = ModulePneumaticTube(flow, getWeight())
    val connections = TimeCache(this.ref, 10) { tubeModule.getConnections() }

    val down = { connections()[0] }
    val up = { connections()[1] }
    val north = { connections()[2] }
    val south = { connections()[3] }
    val west = { connections()[4] }
    val east = { connections()[5] }

    abstract fun getWeight(): Int

    init {
        initModules(tubeModule)
    }


    override fun getRenderBoundingBox(): AxisAlignedBB {
        return FULL_BLOCK_AABB.offset(pos)
    }
}

@RegisterTileEntity("pneumatic_tube")
class TilePneumaticTube(type: TileType) : AbstractTileTube(type), ITickableTileEntity {

    override fun getWeight(): Int = 0

    override fun tick() {
        super.update()
    }

}

@RegisterTileEntity("pneumatic_restriction_tube")
class TilePneumaticRestrictionTube(type: TileType) : AbstractTileTube(type), ITickableTileEntity {

    override fun getWeight(): Int = 100

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("relay")
class TileRelay(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getFacing()
    val inventory = Inventory(9)
    val buffer = PneumaticBuffer()

    val invModule = ModuleInventory(inventory, sideFilter = { it != facing })
    val endpointModule = ModulePneumaticEndpoint(buffers = listOf(buffer), getInfo = { BufferInfo(false, facing) })
    val relayModule = ModuleRelay(inventory, buffer)

    init {
        initModules(endpointModule, invModule, relayModule)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("filter")
class TileFilter(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getFacing()
    val inventory = Inventory(9)
    val inputBuffer = PneumaticBuffer()
    val outputBuffer = PneumaticBuffer()

    val itemFilter = ModuleItemFilter(inventory)
    val filterModule = ModuleFilter(inputBuffer, outputBuffer, itemFilter)

    val endpointModule = ModulePneumaticEndpoint(
        buffers = listOf(inputBuffer, outputBuffer),
        getInfo = { buff ->
            if (buff == inputBuffer) {
                BufferInfo(true, facing.opposite, filterModule::canInsert)
            } else {
                BufferInfo(false, facing)
            }
        }
    )

    init {
        initModules(endpointModule, filterModule, itemFilter)
    }

    override fun tick() {
        super.update()
    }
}


// Nice to have: filter to only extract certain items
@RegisterTileEntity("transposer")
class TileTransposer(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getFacing()
    val buffer = PneumaticBuffer()
    val inventory = Inventory(9)

    val itemFilter = ModuleItemFilter(inventory)
    val endpointModule = ModulePneumaticEndpoint(buffers = listOf(buffer), getInfo = { BufferInfo(false, facing) })
    val transposerModule = ModuleTransposer(buffer, itemFilter, { facing })

    init {
        initModules(endpointModule, transposerModule, itemFilter)
    }

    override fun tick() {
        super.update()
    }
}