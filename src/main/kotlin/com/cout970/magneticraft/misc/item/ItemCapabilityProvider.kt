package com.cout970.magneticraft.misc.item


import com.cout970.magneticraft.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

/**
 * Created by cout970 on 2017/08/19.
 */
class ItemCapabilityProvider(vararg pairs: Pair<Capability<*>?, IItemCapability>) : ICapabilityProvider {

    val capabilityMap: Map<Capability<*>, LazyOptional<IItemCapability>> = pairs.map { it.first!! to LazyOptional.of { it.second } }.toMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): LazyOptional<T> {
        return capabilityMap[capability]?.cast() ?: LazyOptional.empty()
    }
}