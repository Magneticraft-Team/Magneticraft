package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.energy.WireConnectorWrapper
import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModuleElectricity
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by cout970 on 2017/06/29.
 */

class TileConnector : TileBase(), ITickable {

    val node = ElectricNode(container.ref)
    val wrapper = WireConnectorWrapper(node, listOf(vec3Of(0.5)))

    val electricModule = ModuleElectricity(
            electricNodes = listOf(wrapper),
            onWireChange = { if (world.isClient) wireRender.reset() },
            maxWireDistance = 10.0,
            canConnectAtSide = this::canConnectAtSide
    )

    val facing: EnumFacing get() = getBlockState()[ElectricMachines.PROPERTY_FACING]?.facing ?: EnumFacing.DOWN

    // client
    val wireRender = RenderCache()
    var hasBase: Boolean = false

    init {
        initModules(electricModule)
    }

    override fun update() {
        super.update()
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == this.facing
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

}