package com.cout970.magneticraft.block.itemblock

import com.cout970.magneticraft.block.BlockBase
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack

open class ItemBlockBase(
    val blockBase: BlockBase
) : ItemBlock(blockBase) {
    init {
        registryName = block.registryName
        unlocalizedName = block.unlocalizedName
    }

    override fun getHasSubtypes() = blockBase.inventoryVariants.size > 1

    override fun getUnlocalizedName(stack: ItemStack?) = blockBase.getItemName(stack) ?: "unnamed"

    override fun getMetadata(damage: Int) = damage
}
