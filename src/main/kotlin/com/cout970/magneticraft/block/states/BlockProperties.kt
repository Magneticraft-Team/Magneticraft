package com.cout970.magneticraft.block.states

import com.google.common.base.Predicate
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 12/05/2016.
 */
object BlockProperties {

    val blockOreState : IProperty<BlockOreStates> = PropertyEnum.create("type", BlockOreStates::class.java)
    val blockLimestoneState: IProperty<BlockLimestoneStates> = PropertyEnum.create("type", BlockLimestoneStates::class.java)
    val blockBurnLimestoneState: IProperty<BlockLimestoneStates> = PropertyEnum.create("type", BlockLimestoneStates::class.java)
    val blockFeedingTroughCenter: IProperty<Boolean> = PropertyBool.create("center")
    val blockFeedingTroughCompanion: IProperty<EnumFacing> = PropertyEnum.create("companion", EnumFacing::class.java, Predicate { it!!.frontOffsetY == 0 })
}