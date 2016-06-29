package com.cout970.magneticraft.client.render

import coffee.cypher.mcextlib.extensions.resources.toModel
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.item.ItemBase
import net.minecraftforge.client.model.ModelLoader

fun ItemBase.registerInvRender() {
    variants.forEach {
        ModelLoader.setCustomModelResourceLocation(this, it.key, registryName.toModel(it.value))
    }
}

fun ItemBlockBase.registerInvRender() {
    blockBase.inventoryVariants.forEach {
        ModelLoader.setCustomModelResourceLocation(this, it.key, registryName.toModel(it.value))
    }
}