package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.energy.WireConnectorWrapper
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.crafting.FurnaceCraftingProcess
import com.cout970.magneticraft.misc.inventory.InventoryCapabilityFilter
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleElectricProcessing
import com.cout970.magneticraft.tileentity.modules.ModuleElectricity
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
import com.cout970.magneticraft.tileentity.modules.ModuleInventory
import com.cout970.magneticraft.tilerenderer.TileRendererConnector
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.times
import com.cout970.magneticraft.util.vector.toVec3d
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3i

/**
 * Created by cout970 on 2017/06/29.
 */

class TileConnector : TileBase(), ITickable {

    val node = ElectricNode(container.ref)
    val wrapper = WireConnectorWrapper(node, this::getConnectors)

    val electricModule = ModuleElectricity(
            electricNodes = listOf(wrapper),
            onWireChange = { if (world.isClient) wireRender.reset() },
            maxWireDistance = 10.0,
            canConnectAtSide = this::canConnectAtSide,
            connectableDirections = this::getConnectableDirections
    )

    val facing: EnumFacing get() = getBlockState().getFacing()

    // client
    val wireRender = RenderCache()
    var hasBase: Boolean = false

    init {
        initModules(electricModule)
    }

    override fun update() {
        super.update()
    }

    override fun onBlockStateUpdates() {
        if (world.isClient) {
            hasBase = TileRendererConnector.shouldHaveBase(this)
        }
    }

    fun getConnectableDirections(): List<Vec3i> {
        return if (facing.opposite.axisDirection == EnumFacing.AxisDirection.NEGATIVE) {
            listOf(facing.opposite.directionVec, facing.opposite.directionVec * 2)
        } else emptyList()
    }

    fun getConnectors(): List<IVector3> {
        val offset = facing.opposite.directionVec.toVec3d() * PIXEL * 3.0
        return listOf(vec3Of(0.5) + offset)
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == this.facing.opposite
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB
}


class TileBattery : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val node = ElectricNode(container.ref)

    val electricModule = ModuleElectricity(
            electricNodes = listOf(node),
            canConnectAtSide = this::canConnectAtSide
    )
    val storageModule = ModuleInternalStorage(
            capacity = Config.blockBatteryCapacity,
            mainNode = node,
            maxChargeSpeed = 400.0,
            upperVoltageLimit = 100.0,
            lowerVoltageLimit = 90.0
    )

    init {
        initModules(electricModule, storageModule)
    }

    override fun update() {
        super.update()
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == this.facing.opposite
    }
}

class TileElectricFurnace : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val node = ElectricNode(container.ref)

    val electricModule = ModuleElectricity(
            electricNodes = listOf(node)
    )
    val storageModule = ModuleInternalStorage(
            capacity = 10000,
            mainNode = node
    )
    val invModule = ModuleInventory(2, capabilityFilter = {
        InventoryCapabilityFilter(it, inputSlots = listOf(0), outputSlots = listOf(1))
    })
    val processModule = ModuleElectricProcessing(
            craftingProcess = FurnaceCraftingProcess(invModule, 0, 1),
            storage = storageModule,
            workingRate = 1f,
            costPerTick = Config.electricFurnaceMaxConsumption.toFloat()
    )

    init {
        initModules(electricModule, storageModule, invModule, processModule)
    }

    override fun update() {
        super.update()
    }
}