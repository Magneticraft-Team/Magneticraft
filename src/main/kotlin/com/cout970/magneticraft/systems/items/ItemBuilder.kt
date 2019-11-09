package com.cout970.magneticraft.systems.items

import com.cout970.magneticraft.IBlockState
import com.cout970.magneticraft.misc.resource
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.ToolType
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/06/11.
 */
class ItemBuilder {

    var constructor: (Item.Properties) -> ItemBase = { ItemBase(it) }
    var registryName: ResourceLocation? = null
    var creativeTab: ItemGroup? = null
    var isFull3d: Boolean = false
    var maxStackSize = 64
    var maxDamage = 0
    var onHitEntity: ((HitEntityArgs) -> Boolean)? = null
    var onItemUse: ((OnItemUseArgs) -> ActionResultType)? = null
    var onItemRightClick: ((OnItemRightClickArgs) -> ActionResult<ItemStack>)? = null
    var itemInteractionForEntity: ((ItemInteractionForEntityArgs) -> Boolean)? = null
    var capabilityProvider: ((InitCapabilitiesArgs) -> ICapabilityProvider?)? = null
    var addInformation: ((AddInformationArgs) -> Unit)? = null
    var getDestroySpeed: ((GetDestroySpeedArgs) -> Float)? = null
    var getHarvestLevel: ((GetHarvestLevelArgs) -> Int)? = null
    var canHarvestBlock: ((IBlockState) -> Boolean)? = null
    var onBlockDestroyed: ((OnBlockDestroyedArgs) -> Boolean)? = null
    var getToolClasses: ((ItemStack) -> MutableSet<ToolType>)? = null
    var postCreate: ((ItemStack) -> Unit)? = null
    var containerItem: Item? = null
    var canRepair = false
    var burnTime = -1
    var customModels: List<Pair<String, ResourceLocation>>? = null

    fun withName(name: String): ItemBuilder {
        registryName = resource(name)
        return this
    }

    fun build(): ItemBase {
        requireNotNull(registryName) { "registryName was null" }
        val props = Item.Properties()

        if (!canRepair) props.setNoRepair()
        props.maxStackSize(maxStackSize)
        props.maxDamage(maxDamage)
        creativeTab?.let { props.group(it) }
        containerItem?.let { props.containerItem(it) }

        val item = constructor(props)

        item.let {
            it.registryName = registryName
            it.onHitEntity = onHitEntity
            it.postCreate = postCreate
            it.capabilityProvider = capabilityProvider
            it.getDestroySpeed = getDestroySpeed
            it.onItemUse = onItemUse
            it.onItemRightClick = onItemRightClick
            it.itemInteractionForEntity = itemInteractionForEntity
            it.addInformation = addInformation
            it.getHarvestLevel = getHarvestLevel
            it.canHarvestBlock = canHarvestBlock
            it.onBlockDestroyed = onBlockDestroyed
            it.getToolClasses = getToolClasses
            it.burnTime = burnTime
            customModels?.let { c -> it.customModels = c }
        }

        return item
    }

    fun copy(func: ItemBuilder.() -> Unit): ItemBuilder {
        val newBuilder = ItemBuilder()

        newBuilder.constructor = constructor
        newBuilder.registryName = registryName
        newBuilder.creativeTab = creativeTab
        newBuilder.isFull3d = isFull3d
        newBuilder.maxStackSize = maxStackSize
        newBuilder.maxDamage = maxDamage
        newBuilder.onHitEntity = onHitEntity
        newBuilder.postCreate = postCreate
        newBuilder.capabilityProvider = capabilityProvider
        newBuilder.addInformation = addInformation
        newBuilder.getDestroySpeed = getDestroySpeed
        newBuilder.getHarvestLevel = getHarvestLevel
        newBuilder.getToolClasses = getToolClasses
        newBuilder.containerItem = containerItem

        func(newBuilder)
        return newBuilder
    }
}