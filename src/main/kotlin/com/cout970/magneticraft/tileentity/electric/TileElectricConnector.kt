package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.block.states.PROPERTY_FACING
import com.cout970.magneticraft.tileentity.electric.connectors.ElectricConnector
import com.cout970.magneticraft.util.get
import com.cout970.magneticraft.util.isIn
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3i

/**
 * Created by cout970 on 29/06/2016.
 */
class TileElectricConnector : TileElectricBase() {

    var mainNode = ElectricConnector(ElectricNode(worldGetter = { world }, posGetter = { pos }), this)
    override val electricNodes: List<IElectricNode>
        get() = listOf(mainNode)
    var hasBase = true
    var tickToNextUpdate = 0

    override fun update() {
        super.update()
        if (worldObj.isRemote)
            if (tickToNextUpdate > 0)
                tickToNextUpdate--
    }

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) = Unit

    override fun getRenderBoundingBox(): AxisAlignedBB = INFINITE_EXTENT_AABB

    override fun updateWiredConnections() {
        val dir = getFacing()
        if (dir.axisDirection == EnumFacing.AxisDirection.NEGATIVE) {
            val tile = worldObj.getTileEntity(pos.offset(dir, 2))
            if (tile is TileElectricConnector) {
                if (canConnect(mainNode, tile, tile.mainNode, dir) && tile.canConnect(tile.mainNode, this, mainNode, dir.opposite)) {
                    val connection = ElectricConnection(mainNode, tile.mainNode)
                    addConnection(connection, dir, true)
                    tile.addConnection(connection, dir.opposite, false)
                }
            }
        }
        if (autoConnectWires) {
            autoConnectWires(this, world, pos.subtract(Vec3i(16, 5, 16)), pos.add(Vec3i(16, 5, 16)), mainNode)
        }
        super.updateWiredConnections()
    }

    fun getFacing(): EnumFacing {
        val state = world.getBlockState(pos)
        if(PROPERTY_FACING.isIn(state)){
            return PROPERTY_FACING[state]
        }
        return EnumFacing.DOWN
    }

    override fun canConnectAtSide(facing: EnumFacing?): Boolean {
        return facing == null || facing == getFacing()
    }

    override fun getMaxWireDistance(): Double = MAX_WIRE_DISTANCE

    override fun connectWire(handler: INodeHandler, side: EnumFacing): Boolean {
        var result = false
        if (handler == this || handler !is IElectricNodeHandler) return result
        result = connectHandlers(this, handler)
        wireRender.reset()
        return result
    }

    companion object {
        val MAX_WIRE_DISTANCE = 8.0
    }
}