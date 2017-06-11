package com.cout970.magneticraft.item.core

import com.cout970.magneticraft.util.resource
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 2017/06/11.
 */
class ItemBuilder {

    var constructor: () -> ItemBase = { ItemBase() }
    var registryName: ResourceLocation? = null
    var creativeTab: CreativeTabs? = null
    var variants: Map<Int, String>? = null

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
        }

        return item
    }
}