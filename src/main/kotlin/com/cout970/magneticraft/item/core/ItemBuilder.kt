package com.cout970.magneticraft.item.core

import com.cout970.magneticraft.util.resource
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/06/11.
 */
class ItemBuilder {

    var constructor: () -> ItemBase = { ItemBase() }
    var registryName: ResourceLocation? = null
    var creativeTab: CreativeTabs? = null
    var variants: Map<Int, String>? = null
    var isFull3d: Boolean = false
    var maxStackSize = 64
    var maxDamage = 0
    var onHitEntity: ((HitEntityArgs) -> Boolean)? = null
    var capabilityProvider: ((InitCapabilitiesArgs) -> ICapabilityProvider?)? = null

    fun withName(name: String): ItemBuilder {
        registryName = resource(name)
        return this
    }

    fun build(): ItemBase {
        requireNotNull(registryName) { "registryName was null" }
        val item = constructor()

        item.apply {
            registryName = this@ItemBuilder.registryName
            this@ItemBuilder.creativeTab?.let { setCreativeTab(it) }
            this@ItemBuilder.variants?.let { variants = it }
            if(isFull3d) setFull3D()
            setMaxStackSize(this@ItemBuilder.maxStackSize)
            if(this@ItemBuilder.maxDamage > 0) maxDamage = this@ItemBuilder.maxDamage
            onHitEntity = this@ItemBuilder.onHitEntity
            capabilityProvider = this@ItemBuilder.capabilityProvider
        }

        return item
    }
}