package com.cout970.magneticraft.item.core

import com.cout970.magneticraft.util.resource
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
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
    var onItemUse: ((OnItemUseArgs) -> EnumActionResult)? = null
    var onItemRightClick: ((OnItemRightClickArgs) -> ActionResult<ItemStack>)? = null
    var capabilityProvider: ((InitCapabilitiesArgs) -> ICapabilityProvider?)? = null
    var addInformation: ((AddInformationArgs) -> Unit)? = null
    var createStack: ((Item, Int, Int) -> ItemStack)? = null
    var containerItem: Item? = null
    var customModels: List<Pair<String, ResourceLocation>>? = null


    fun withName(name: String): ItemBuilder {
        registryName = resource(name)
        return this
    }

    fun build(): ItemBase {
        requireNotNull(registryName) { "registryName was null" }
        val item = constructor()


        item.let {
            it.registryName = registryName
            creativeTab?.let { t -> it.setCreativeTab(t) }
            variants?.let { v -> it.variants = v }
            if (isFull3d) it.setFull3D()
            it.setMaxStackSize(maxStackSize)
            if (maxDamage > 0) it.maxDamage = maxDamage
            it.onHitEntity = onHitEntity
            it.capabilityProvider = capabilityProvider
            it.onItemUse = onItemUse
            it.onItemRightClick = onItemRightClick
            it.addInformation = addInformation
            it.createStack = createStack
            it.containerItem = containerItem
            customModels?.let { c -> it.customModels = c }
        }

        return item
    }

    fun copy(func: ItemBuilder.() -> Unit): ItemBuilder {
        val newBuilder = ItemBuilder()

        newBuilder.constructor = constructor
        newBuilder.registryName = registryName
        newBuilder.creativeTab = creativeTab
        newBuilder.variants = variants
        newBuilder.isFull3d = isFull3d
        newBuilder.maxStackSize = maxStackSize
        newBuilder.maxDamage = maxDamage
        newBuilder.onHitEntity = onHitEntity
        newBuilder.capabilityProvider = capabilityProvider
        newBuilder.addInformation = addInformation
        newBuilder.createStack = createStack
        newBuilder.containerItem = containerItem

        func(newBuilder)
        return newBuilder
    }
}