package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.energy.WireConnectorWrapper
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleElectricity
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
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
            maxWireDistance = 10.0,
            canConnectAtSide = this::canConnectAtSide
    )
    val storageModule = ModuleInternalStorage(
            electricModule = electricModule,
            mainNode = node
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