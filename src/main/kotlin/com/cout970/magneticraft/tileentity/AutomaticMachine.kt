package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.block.getOrientationCentered
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.*
import com.cout970.magneticraft.util.vector.toBlockPos
import com.cout970.magneticraft.util.vector.xd
import com.cout970.magneticraft.util.vector.yd
import com.cout970.magneticraft.util.vector.zd
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