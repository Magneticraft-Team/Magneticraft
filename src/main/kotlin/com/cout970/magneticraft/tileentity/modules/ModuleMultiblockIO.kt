package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.vector.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

data class ConnectionSpot(
        val capability: Capability<*>,
        val pos: BlockPos,
        val side: EnumFacing,
        val getter: () -> Any?
)

class ModuleMultiblockIO(
        val facing: () -> EnumFacing,
        val connectionSpots: List<ConnectionSpot>,
        override val name: String = "module_multiblock_io"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        fun connectionArea(capability: Capability<*>, start: BlockPos, end: BlockPos, side: EnumFacing,
                           getter: () -> Any?): List<ConnectionSpot> {
            val result = mutableListOf<ConnectionSpot>()

            for (x in start.x..end.x) {
                for (y in start.y..end.y) {
                    for (z in start.z..end.z) {
                        result += ConnectionSpot(capability, BlockPos(x, y, z), side, getter)
                    }
                }
            }
            return result
        }

        fun connectionCross(capability: Capability<*>, start: BlockPos, dist: Int = 1, getter: () -> Any?): List<ConnectionSpot> {
            val result = mutableListOf<ConnectionSpot>()

            EnumFacing.HORIZONTALS.forEach {
                result += ConnectionSpot(capability, start + it.toBlockPos() * dist, it, getter)
            }
            return result
        }
    }

    fun getCapability(cap: Capability<*>, side: EnumFacing?, relPos: BlockPos): Any? {
        if (connectionSpots.isEmpty() || side == null) return null

        val direction = facing()

        val validCapability = connectionSpots.filter { it.capability == cap }

        val valid = validCapability.find {
            direction.rotatePoint(BlockPos.ORIGIN, it.pos) == relPos &&
                    direction.getRelative(it.side) == side
        } ?: return null


        return valid.getter()
    }

    fun getConnectableDirections(): List<Pair<BlockPos, EnumFacing>> {
        val connections = connectionSpots.filter { it.capability == ELECTRIC_NODE_HANDLER }

        if (connections.isEmpty()) return emptyList()
        val direction = facing()

        return connections
                .filter { direction.getRelative(it.side).axisDirection == EnumFacing.AxisDirection.NEGATIVE }
                .map {
                    val dir = direction.getRelative(it.side)
                    direction.rotatePoint(BlockPos.ORIGIN, it.pos) + dir.toBlockPos() to dir.opposite
                }
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        val connections = connectionSpots.filter { it.capability == ELECTRIC_NODE_HANDLER }

        if (connections.isEmpty()) return false

        val direction = facing()

        return connections.any { direction.getRelative(it.side) == facing }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        return getCapability(cap, facing, BlockPos.ORIGIN) as? T?
    }
}