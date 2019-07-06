package com.cout970.magneticraft.features.automatic_machines

import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBoxStorage
import com.cout970.magneticraft.api.internal.pneumatic.PneumaticBuffer
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.block.getOrientationCentered
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.TimeCache
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.block.Block
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("feeding_trough")
class TileFeedingTrough : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientationCentered()

    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val moduleFeedingTrough = ModuleFeedingTrough(inventory)

    init {
        initModules(moduleFeedingTrough, invModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val dir = facing.toBlockPos()
        return super.getRenderBoundingBox().expand(dir.xd, dir.yd, dir.zd)
    }
}


@RegisterTileEntity("conveyor_belt")
class TileConveyorBelt : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val conveyorModule = ModuleConveyorBelt({ facing })

    init {
        initModules(conveyorModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return pos createAABBUsing pos.add(1, 2, 1)
    }
}

@RegisterTileEntity("inserter")
class TileInserter : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val filters = Inventory(9)
    val inventory: Inventory = Inventory(3) { _, _ -> inserterModule.updateUpgrades() }
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val openGui = ModuleOpenGui()
    val inserterModule = ModuleInserter({ facing }, inventory, filters)

    init {
        initModules(inserterModule, invModule, openGui)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("water_generator")
class TileWaterGenerator : TileBase(), ITickable {

    val cache: MutableList<IFluidHandler> = mutableListOf()
    val tank = object : Tank(32_000) {
        init {
            onContentsChanged()
        }

        override fun onContentsChanged() {
            fluid = FluidRegistry.getFluidStack("water", 32_000)
        }
    }

    val fluidModule = ModuleFluidHandler(tank)
    val bucketIoModule = ModuleBucketIO(tank)

    init {
        initModules(bucketIoModule, fluidModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
        if (container.shouldTick(20)) {
            cache.clear()
            enumValues<EnumFacing>().forEach { dir ->
                val tile = world.getTileEntity(pos.offset(dir))
                val handler = tile?.getOrNull(FLUID_HANDLER, dir.opposite) ?: return@forEach
                cache.add(handler)
            }
        }
        cache.forEach { handler ->
            val water = FluidStack(FluidRegistry.WATER, Config.waterGeneratorPerTickWater)
            val amount = handler.fill(water, false)
            if (amount > 0) {
                handler.fill(FluidStack(water, amount), true)
            }
        }
    }
}

abstract class AbstractTileTube : TileBase() {

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
        return Block.FULL_BLOCK_AABB.offset(pos)
    }
}

@RegisterTileEntity("pneumatic_tube")
class TilePneumaticTube : AbstractTileTube(), ITickable {

    override fun getWeight(): Int = 0

    @DoNotRemove
    override fun update() {
        super.update()
    }

}

@RegisterTileEntity("pneumatic_restriction_tube")
class TilePneumaticRestrictionTube : AbstractTileTube(), ITickable {

    override fun getWeight(): Int = 100

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("relay")
class TileRelay : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getFacing()
    val inventory = Inventory(9)
    val buffer = PneumaticBuffer()

    val invModule = ModuleInventory(inventory, sideFilter = { it != facing })
    val endpointModule = ModulePneumaticEndpoint(buffers = listOf(buffer), getInfo = { BufferInfo(false, facing) })
    val relayModule = ModuleRelay(inventory, buffer)

    init {
        initModules(endpointModule, invModule, relayModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("filter")
class TileFilter : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getFacing()
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

    @DoNotRemove
    override fun update() {
        super.update()
    }
}


// Nice to have: filter to only extract certain items
@RegisterTileEntity("transposer")
class TileTransposer : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getFacing()
    val buffer = PneumaticBuffer()
    val inventory = Inventory(9)

    val itemFilter = ModuleItemFilter(inventory)
    val endpointModule = ModulePneumaticEndpoint(buffers = listOf(buffer), getInfo = { BufferInfo(false, facing) })
    val transposerModule = ModuleTransposer(buffer, itemFilter, { facing })

    init {
        initModules(endpointModule, transposerModule, itemFilter)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}