package com.cout970.magneticraft.block

import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 30/06/2016.
 */

val PROPERTY_UP: PropertyBool = PropertyBool.create("up")
val PROPERTY_DOWN: PropertyBool = PropertyBool.create("down")
val PROPERTY_NORTH: PropertyBool = PropertyBool.create("north")
val PROPERTY_SOUTH: PropertyBool = PropertyBool.create("south")
val PROPERTY_EAST: PropertyBool = PropertyBool.create("east")
val PROPERTY_WEST: PropertyBool = PropertyBool.create("west")

val PropertyDirections: Map<EnumFacing, PropertyBool> = mapOf(EnumFacing.UP to PROPERTY_UP,
        EnumFacing.DOWN to PROPERTY_DOWN,
        EnumFacing.NORTH to PROPERTY_NORTH,
        EnumFacing.SOUTH to PROPERTY_SOUTH,
        EnumFacing.EAST to PROPERTY_EAST,
        EnumFacing.WEST to PROPERTY_WEST)

val PROPERTY_FACING: PropertyDirection = PropertyDirection.create("facing")
val PROPERTY_DIRECTION: PropertyDirection = PropertyDirection.create("direction", listOf(*EnumFacing.HORIZONTALS))
val PROPERTY_CENTER: PropertyBool = PropertyBool.create("center")
val PROPERTY_ACTIVE: PropertyBool = PropertyBool.create("active")
val ELECTRIC_POLE_PLACE: PropertyEnum<BlockElectricPoleBase.ElectricPoleStates> = PropertyEnum.create("place", BlockElectricPoleBase.ElectricPoleStates::class.java)