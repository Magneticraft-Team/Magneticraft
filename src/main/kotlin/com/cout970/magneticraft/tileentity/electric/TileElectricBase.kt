package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.energy.INodeProvider
import com.cout970.magneticraft.api.energy.impl.ElectricConnection
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.registry.NODE_PROVIDER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.shouldTick
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 29/06/2016.
 */
abstract class TileElectricBase : TileBase(), INodeProvider, ITickable {

    var node = createNode()
    val connections = mutableListOf<IElectricConnection>()
    var needUpdate = true

    open fun createNode():ElectricNode =  ElectricNode(worldGetter = { world }, posGetter = { pos })

    override fun update() {
        if (needUpdate || shouldTick(40)) {
            needUpdate = false
            updateConnections()
        }

        node.iterate()
        connections.forEach { it.iterate() }
    }

    open fun updateConnections(){
        connections.clear()

        for (dir in EnumFacing.values().filter { it.axisDirection == EnumFacing.AxisDirection.NEGATIVE }) {
            val tile = worldObj.getTileEntity(pos.offset(dir)) ?: continue
            val provider = NODE_PROVIDER!!.fromTile(tile, dir.opposite) ?: continue
            for (n in provider.nodes.filter { it is IElectricNode }) {
                connections.add(ElectricConnection(node, n as IElectricNode))
            }
        }
    }

    fun interpolate(v: Double, min: Double, max: Double): Double {
        if (v < min) return 0.0;
        if (v > max) return 1.0;
        val dif = max - min
        return (v - min) / dif
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        node.deserializeNBT(compound!!.getCompoundTag("ElectricNode"))
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        compound!!.setTag("ElectricNode", node.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun getNodes(): List<INode> = listOf(node)

    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T {
        if (capability == NODE_PROVIDER) return this as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == NODE_PROVIDER) return true
        return super.hasCapability(capability, facing)
    }
}