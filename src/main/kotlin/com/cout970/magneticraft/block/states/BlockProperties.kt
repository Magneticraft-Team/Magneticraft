package com.cout970.magneticraft.block.states

import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum

/**
 * Created by cout970 on 12/05/2016.
 */
object BlockProperties {

    val blockOreState : IProperty<BlockOreStates> = PropertyEnum.create("type", BlockOreStates.COPPER.javaClass)
}