package com.cout970.magneticraft.util.misc

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.TileElectricBase
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

    fun create(world: World, handler: IElectricNodeHandler): IElectricConnection? {
        if (!world.isAreaLoaded(first, second)) {
            return null
        }
        val tile0 = world.getTileEntity(first)
        val tile1 = world.getTileEntity(second)
        if (tile0 == null || tile1 == null) {
            isValid = false
            return null
        }
        val handler0 = NODE_HANDLER!!.fromTile(tile0)
        val handler1 = NODE_HANDLER!!.fromTile(tile1)
        if (handler0 !is IElectricNodeHandler || handler1 !is IElectricNodeHandler) {
            isValid = false
            return null
        }
        val nodes0 = handler0.nodes
        val nodes1 = handler1.nodes
        if (firstIndex < 0 || secondIndex < 0) {
            isValid = false
            return null
        }
        if (nodes0.size <= firstIndex || nodes1.size <= secondIndex) {
            isValid = false
            return null
        }
        if (nodes0[firstIndex] !is IElectricNode || nodes1[secondIndex] !is IElectricNode) {
            isValid = false
            return null
        }
        val con: IElectricConnection?
        if (handler == handler0) {
            con = handler1.createConnection(handler0, nodes0[firstIndex] as IElectricNode, nodes1[secondIndex] as IElectricNode, null)
        } else {
            con = handler0.createConnection(handler1, nodes1[secondIndex] as IElectricNode, nodes0[firstIndex] as IElectricNode, null)
        }

        if (con == null) {
            isValid = false
            return null
        }
        return con
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
                val handler0 = TileElectricBase.getHandler(con.firstNode)
                val index0 = handler0!!.nodes.indexOfFirst { con.firstNode == it }
                setInteger("x0", pos0.x)
                setInteger("y0", pos0.y)
                setInteger("z0", pos0.z)
                setInteger("index0", index0)

                val pos1 = con.secondNode.pos
                val handler1 = TileElectricBase.getHandler(con.secondNode)
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