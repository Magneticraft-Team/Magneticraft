package com.cout970.magneticraft.block.states

import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum

/**
 * Created by cout970 on 12/05/2016.
 */
object BlockProperties {

    val blockOreState : IProperty<BlockOreStates> = PropertyEnum.create("type", BlockOreStates::class.java)
    val blockLimestoneState: IProperty<BlockLimestoneStates> = PropertyEnum.create("type", BlockLimestoneStates::class.java)
    val blockBurnLimestoneState: IProperty<BlockLimestoneStates> = PropertyEnum.create("type", BlockLimestoneStates::class.java)
}