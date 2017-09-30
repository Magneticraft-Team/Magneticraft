package com.cout970.magneticraft.integration

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by cout970 on 2017/09/30.
 */
object ItemHolder {

    @JvmField
    @GameRegistry.ItemStackHolder("immersiveengineering:material", meta = 6)
    val coalCoke: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("immersiveengineering:stone_decoration", meta = 3)
    val coalCokeBlock: ItemStack? = null
}