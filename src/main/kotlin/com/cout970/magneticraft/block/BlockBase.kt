package com.cout970.magneticraft.block

import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.resource
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack

abstract class BlockBase(material: Material, registryName: String, unlocalizedName : String = registryName) : Block(material) {

    init{
        this.unlocalizedName = "$MODID.$unlocalizedName"
        this.registryName = resource(registryName)
        setHardness(1.5F)
        setResistance(10.0F)
        setCreativeTab(CreativeTabMg)
    }

    open fun getMaxModels():Int = 1

    open fun getModelLoc(i :Int): ModelResourceLocation = ModelResourceLocation(registryName, "inventory")

    open fun getUnlocalizedName(stack: ItemStack?): String? = unlocalizedName

}