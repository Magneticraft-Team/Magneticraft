package com.cout970.magneticraft.item

import com.cout970.magneticraft.block.BlockBurnLimestone
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 12/06/2016.
 */
object ItemHandSieve : ItemBase("hand_sieve") {


    override fun getModels(): Map<Int, ModelResourceLocation> {
        return mapOf(0 to ModelResourceLocation("$registryName.obj", "inventory"))
    }
}