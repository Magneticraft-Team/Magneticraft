package com.cout970.magneticraft.client.render

import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.item.ItemBase
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader

fun Item.registerInvRender() {
    if(this is ItemBase){
        for(i in this.getModels()) {
            ModelLoader.setCustomModelResourceLocation(this, i.key, i.value)
        }
    }else if(this is ItemBlockBase){
        for(i in this.getModels()) {
            ModelLoader.setCustomModelResourceLocation(this, i.key, i.value)
        }
    }else {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName, "inventory"))
    }
}