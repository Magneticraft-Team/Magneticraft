package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tilerenderers.MutableCubeCache
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

data class ConnectionSpot(
    val capability: Capability<*>,
    val pos: BlockPos,
    val side: EnumFacing?,
    val getter: () -> Any?
)

class ModuleMultiblockIO(
    val facing: () -> EnumFacing,
    val connectionSpots: List<ConnectionSpot>,
    override val name: String = "module_multiblock_io"
) : IModule {

    // Client side only, for debug purposes
    var clientCache: MutableList<MutableCubeCache>? = null

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

        fun connectionCube(capability: Capability<*>, start: BlockPos, end: BlockPos, getter: () -> Any?): List<ConnectionSpot> {
            val result = mutableListOf<ConnectionSpot>()

            for (x in start.x..end.x) {
                for (y in start.y..end.y) {
                    for (z in start.z..end.z) {
                        val pos = BlockPos(x, y, z)

                        getCubeSides(pos, start, end).forEach {
                            result += ConnectionSpot(capability, pos, it, getter)
                        }
                    }
                }
            }
            return result
        }

        private fun getCubeSides(pos: BlockPos, start: BlockPos, end: BlockPos): List<EnumFacing> {
            val sides = mutableListOf<EnumFacing>()

            if (pos.y == start.y) sides += EnumFacing.DOWN
            if (pos.y == end.y) sides += EnumFacing.UP
            if (pos.x == start.x) sides += EnumFacing.WEST
            if (pos.x == end.x) sides += EnumFacing.EAST
            if (pos.z == start.z) sides += EnumFacing.NORTH
            if (pos.z == end.z) sides += EnumFacing.SOUTH

            return sides
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
        if (connectionSpots.isEmpty()) return null

        val direction = facing()

        val validCapability = connectionSpots.filter { it.capability == cap }

        val validSide = validCapability.filter {
            if (it.side == null) {
                side == null
            } else {
                direction.getRelative(it.side) == side
            }
        }

        val validPos = validSide.filter { direction.rotatePoint(BlockPos.ORIGIN, it.pos) == relPos }

        val valid = validPos.firstOrNull() ?: return null

        return valid.getter()
    }

    fun getElectricConnectPoints(): List<Pair<BlockPos, EnumFacing>> {
        val connections = connectionSpots.filter { it.capability == ELECTRIC_NODE_HANDLER }

        if (connections.isEmpty()) return emptyList()
        val direction = facing()

        return getConnectPoints(connections, direction)
    }

    fun getHeatConnectPoints(): List<Pair<BlockPos, EnumFacing>> {
        val connections = connectionSpots.filter { it.capability == HEAT_NODE_HANDLER }

        if (connections.isEmpty()) return emptyList()
        val direction = facing()

        return getConnectPoints(connections, direction)
    }

    private fun getConnectPoints(connections: List<ConnectionSpot>, direction: EnumFacing): List<Pair<BlockPos, EnumFacing>> {
        return connections
            .filter { it.side != null }
            .filter { direction.getRelative(it.side!!).axisDirection == EnumFacing.AxisDirection.NEGATIVE }
            .map {
                val dir = direction.getRelative(it.side!!)
                direction.rotatePoint(BlockPos.ORIGIN, it.pos) + dir.toBlockPos() to dir.opposite
            }
    }

    fun canConnectAtSide(facing: EnumFacing?): Boolean {
        val connections = connectionSpots.filter { it.capability == ELECTRIC_NODE_HANDLER }

        if (connections.isEmpty()) return false

        val direction = facing()

        return connections.any {
            if (it.side == null) {
                facing == null
            } else {
                direction.getRelative(it.side) == facing
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        return getCapability(cap, facing, BlockPos.ORIGIN) as? T?
    }
}