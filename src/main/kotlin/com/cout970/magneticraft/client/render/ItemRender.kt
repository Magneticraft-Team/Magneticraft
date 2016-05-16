package com.cout970.magneticraft.client.render

import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.item.ItemBase
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader

fun Item.registerInvRender() {
    if(this is ItemBase){
        for(i in 0..this.getMaxModels()-1) {
            ModelLoader.setCustomModelResourceLocation(this, i, this.getModelLoc(i))
        }
    }else if(this is ItemBlockBase){
        for(i in 0..this.getMaxModels()-1) {
            ModelLoader.setCustomModelResourceLocation(this, i, this.getModelLoc(i))
        }
    }else {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName, "inventory"))
    }
}