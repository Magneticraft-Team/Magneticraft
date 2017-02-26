package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 18/07/2016.
 */
data class UnloadedElectricConnection(
        val first: BlockPos,
        val firstIndex: Int,
        val second: BlockPos,
        val secondIndex: Int
) {

    var isValid: Boolean = true
        private set

    fun create(world: World, handler: IElectricNodeHandler): Boolean {
        if (!world.isAreaLoaded(first, second)) {
            return false
        }
        val tile0 = world.getTileEntity(first)
        val tile1 = world.getTileEntity(second)
        if (tile0 == null || tile1 == null) {
            return true
        }
        val handler0 = ELECTRIC_NODE_HANDLER!!.fromTile(tile0)
        val handler1 = ELECTRIC_NODE_HANDLER!!.fromTile(tile1)
        if (handler0 !is IElectricNodeHandler || handler1 !is IElectricNodeHandler) {
            return true
        }
        val nodes0 = handler0.nodes
        val nodes1 = handler1.nodes
        if (firstIndex < 0 || secondIndex < 0) {
            return true
        }
        if (nodes0.size <= firstIndex || nodes1.size <= secondIndex) {
            return true
        }
        val node0 = nodes0[firstIndex]
        val node1 = nodes1[secondIndex]
        if (node0 !is IWireConnector){
            return true
        }
        if (node1 !is IWireConnector) {
            return true
        }
        TraitElectricity.connectNodes(handler0, node0, handler1, node1)
        return true
    }

    companion object {

        fun load(nbt: NBTTagCompound): UnloadedElectricConnection {
            val pos0 = BlockPos(nbt.getInteger("x0"), nbt.getInteger("y0"), nbt.getInteger("z0"))
            val pos1 = BlockPos(nbt.getInteger("x1"), nbt.getInteger("y1"), nbt.getInteger("z1"))
            return UnloadedElectricConnection(pos0, nbt.getInteger("index0"), pos1, nbt.getInteger("index1"))
        }

        fun save(con: IElectricConnection): NBTTagCompound {
            return NBTTagCompound().apply {
                val pos0 = con.firstNode.pos
                val handler0 = TraitElectricity.getHandler(con.firstNode)
                val index0 = handler0!!.nodes.indexOfFirst { con.firstNode == it }
                setInteger("x0", pos0.x)
                setInteger("y0", pos0.y)
                setInteger("z0", pos0.z)
                setInteger("index0", index0)

                val pos1 = con.secondNode.pos
                val handler1 = TraitElectricity.getHandler(con.secondNode)
                val index1 = handler1!!.nodes.indexOfFirst { con.secondNode == it }
                setInteger("x1", pos1.x)
                setInteger("y1", pos1.y)
                setInteger("z1", pos1.z)
                setInteger("index1", index1)
            }
        }

        fun save(con: UnloadedElectricConnection): NBTTagCompound {
            return NBTTagCompound().apply {
                val pos0 = con.first
                setInteger("x0", pos0.x)
                setInteger("y0", pos0.y)
                setInteger("z0", pos0.z)
                setInteger("index0", con.firstIndex)

                val pos1 = con.second
                setInteger("x1", pos1.x)
                setInteger("y1", pos1.y)
                setInteger("z1", pos1.z)
                setInteger("index1", con.secondIndex)
            }
        }
    }
}