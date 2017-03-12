package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.misc.render.RenderCache
import com.cout970.magneticraft.misc.tileentity.IManualWireConnect
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.TraitElectricity.Companion.connectHandlers
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricPoleConnector
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by cout970 on 03/07/2016.
 */
@TileRegister("electric_pole")
class TileElectricPole : TileBase(), IManualWireConnect {

    var mainNode = ElectricPoleConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }))

    val traitElectricity = TraitElectricity(this, listOf(mainNode),
            canConnectAtSideImpl = this::canConnectAtSide,
            onWireChangeImpl = { if(world.isClient && it == null) wireRender.reset() })

    override val traits: List<ITileTrait> = listOf(traitElectricity)
    val wireRender = RenderCache()

    fun canConnectAtSide(facing: EnumFacing?): Boolean = facing == null

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        var result = false
        if (handler == traitElectricity || handler !is IElectricNodeHandler) return result
        result = connectHandlers(traitElectricity, handler)
        wireRender.reset()
        return result
    }
}