package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.block.getOrientationCentered
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleConveyorBelt
import com.cout970.magneticraft.tileentity.modules.ModuleFeedingTrough
import com.cout970.magneticraft.tileentity.modules.ModuleInserter
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
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

    // Client data
    var rotation = 0f
    var deltaTimer = System.currentTimeMillis()

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
    val inventory = Inventory(1)
    val invModule = ModuleInventory(inventory, capabilityFilter = { null })
    val inserterModule = ModuleInserter({ facing }, inventory)

    init {
        initModules(inserterModule, invModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("water_generator")
class TileWaterGenerator : TileBase(), ITickable {

    val cache: MutableList<IFluidHandler> = mutableListOf()

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