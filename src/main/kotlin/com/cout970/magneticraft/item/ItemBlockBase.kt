package com.cout970.magneticraft.item

import com.cout970.magneticraft.block.core.BlockBase
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * Created by cout970 on 2017/06/11.
 */
class ItemBlockBase(val blockBase: BlockBase) : ItemBlock(blockBase){

    init {
        registryName = blockBase.registryName
        hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return blockBase.getItemName(stack)
    }

    override fun getSubItems(itemIn: CreativeTabs, tab: NonNullList<ItemStack>) {
        blockBase.inventoryVariants.forEach { meta, _ -> tab.add(ItemStack(this, 1, meta)) }
    }

    override fun getMetadata(damage: Int): Int = damage
}

fun itemBlockListOf(vararg blocks: BlockBase): List<Pair<BlockBase, ItemBlockBase>>{
    return blocks.map { it to ItemBlockBase(it) }
}