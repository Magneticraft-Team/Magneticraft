package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.blocks.creativeTab
import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

abstract class BlockBase(
    material: Material,
    registryName: String,
    unlocalizedName: String = registryName
) : Block(material) {
    open val inventoryVariants = mapOf(
        0 to "inventory"
    )

    init {
        this.unlocalizedName = "$MODID.$unlocalizedName"
        this.registryName = resource(registryName)
        setHardness(1.5F)
        setResistance(10.0F)
        creativeTab = CreativeTabMg
    }

    open fun getItemName(stack: ItemStack?) = unlocalizedName

    override fun getSubBlocks(itemIn: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>?) {
        if (list == null || itemIn == null) {
            return
        }
        list += inventoryVariants.map { itemIn.stack(meta = it.key) }
    }
}