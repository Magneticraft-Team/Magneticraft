package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.block.getOrientationCentered
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
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

/**
 * Created by cout970 on 2017/06/12.
 */

@RegisterTileEntity("box")
class TileBox : TileBase() {

    val invModule = ModuleInventory(27)

    init {
        initModules(invModule)
    }
}

@RegisterTileEntity("crushing_table")
class TileCrushingTable : TileBase() {

    val invModule = ModuleInventory(1, capabilityFilter = { null })
    val crushingModule = ModuleCrushingTable(invModule)

    init {
        initModules(invModule, crushingModule)
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

    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("inserter")
class TileInserter : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val invModule = ModuleInventory(1, capabilityFilter = { null })
    val inserterModule = ModuleInserter({ facing }, invModule)

    init {
        initModules(inserterModule, invModule)
    }

    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("tube_light")
class TileTubeLight : TileBase() {

    val facing: EnumFacing get() = getBlockState().getOrientation()
}

@RegisterTileEntity("water_generator")
class TileWaterGenerator : TileBase(), ITickable {

    override fun update() {
        super.update()
        enumValues<EnumFacing>().forEach { dir ->
            val tile = world.getTileEntity(pos.offset(dir))
            val handler = tile?.getOrNull(FLUID_HANDLER) ?: return@forEach

            val water = FluidStack(FluidRegistry.WATER, Config.waterGeneratorPerTickWater)
            val amount = handler.fill(water, false)
            if(amount > 0){
                handler.fill(FluidStack(water, amount), true)
            }
        }
    }
}

@RegisterTileEntity("infinite_energy")
class TileInfiniteEnergy : TileBase(), ITickable {

    val node = ElectricNode(container.ref)

    val electricModule = ModuleElectricity(
            listOf(node)
    )

    init {
        initModules(electricModule)
    }

    override fun update() {
        node.voltage = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE
        super.update()
        node.voltage = ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE
    }
}

@RegisterTileEntity("sluice_box")
class TileSluiceBox : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientationCentered()

    val invModule = ModuleInventory(1, capabilityFilter = { null })
    val sluiceBoxModule = ModuleSluiceBox({facing}, invModule)

    init {
        initModules(sluiceBoxModule)
    }

    override fun update() {
        super.update()
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return pass == 0 || pass == 1
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val dir = facing.toBlockPos()
        return super.getRenderBoundingBox().expand(dir.xd, dir.yd, dir.zd)
    }
}

