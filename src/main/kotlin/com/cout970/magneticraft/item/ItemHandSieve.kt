package com.cout970.magneticraft.item

import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 12/06/2016.
 */
object ItemHandSieve : ItemBase("hand_sieve") {

    override  fun getModelLoc(i :Int): ModelResourceLocation = ModelResourceLocation("$registryName.obj", "inventory")
}