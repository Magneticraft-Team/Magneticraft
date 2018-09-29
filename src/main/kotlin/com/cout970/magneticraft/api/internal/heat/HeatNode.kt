package com.cout970.magneticraft.api.internal.heat

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max

class HeatNode(
    val ref: ITileRef,
    private val mass: Double = 1.0,
    private val conductivity: Double = 73.0,
    private val name: String = "heat_node_1"
) : IHeatNode {

    private var internalEnergy = 0.0

    init {
        temperature = STANDARD_AMBIENT_TEMPERATURE
    }

    override fun getWorld(): World = ref.world

    override fun getPos(): BlockPos = ref.pos

    override fun getId(): NodeID = NodeID(name, pos, world.provider.dimension)

    override fun getTemperature(): Double {
        val moles = mass * 1000 / molarMass
        return ((2.0 / 3.0) * internalEnergy) / (moles * IHeatNode.R)
    }

    fun setTemperature(temp: Double) {
        val temperature = max(0.0, temp)
        internalEnergy = tempToEnergy(this, temperature)
    }

    override fun getInternalEnergy(): Double = internalEnergy

    fun setInternalEnergy(e: Double) {
        internalEnergy = max(0.0, e)
    }

    override fun getMass(): Double = mass

    override fun getMolarMass(): Double = 55.845

    override fun getConductivity(): Double = conductivity

    override fun applyHeat(heat: Double) {
        internalEnergy = max(0.0, internalEnergy + heat)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        internalEnergy = nbt.getDouble("U")
    }

    override fun serializeNBT() = newNbt {
        setDouble("U", internalEnergy)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HeatNode) return false

        if (ref != other.ref) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ref.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "HeatNode(mass=$mass, internalEnergy=$internalEnergy)"
    }
}

fun tempToEnergy(node: IHeatNode, temp: Double): Double {
    val moles = node.mass * 1000 / node.molarMass

    // U = T * 3/2 * nR
    return (3.0 / 2.0) * temp * moles * IHeatNode.R
}