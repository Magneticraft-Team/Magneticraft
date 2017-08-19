package com.cout970.magneticraft.misc.item

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/08/19.
 */
class ItemCapabilityProvider(vararg pairs: Pair<Capability<*>?, IItemCapability>) : ICapabilityProvider {

    val capabilityMap: Map<Capability<*>, IItemCapability> = pairs.map { it.first!! to it.second }.toMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>,
                                          facing: EnumFacing?): T? = capabilityMap[capability] as? T?

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean = capability in capabilityMap
}