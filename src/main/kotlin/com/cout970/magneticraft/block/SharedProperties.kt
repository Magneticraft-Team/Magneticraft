package com.cout970.magneticraft.block

import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 30/06/2016.
 */

val PROPERTY_FACING: PropertyDirection = PropertyDirection.create("facing")
val PROPERTY_DIRECTION: PropertyDirection = PropertyDirection.create("direction", listOf(*EnumFacing.HORIZONTALS))
val PROPERTY_CENTER: PropertyBool = PropertyBool.create("center")
val PROPERTY_ACTIVE: PropertyBool = PropertyBool.create("active")
val ELECTRIC_POLE_PLACE: PropertyEnum<BlockElectricPoleBase.ElectricPoleStates> = PropertyEnum.create("place", BlockElectricPoleBase.ElectricPoleStates::class.java)