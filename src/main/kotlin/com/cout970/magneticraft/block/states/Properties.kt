package com.cout970.magneticraft.block.states

import net.minecraft.block.properties.PropertyDirection
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 17/07/2016.
 */

val PROPERTY_FACING: PropertyDirection = PropertyDirection.create("facing")
val PROPERTY_DIRECTION: PropertyDirection = PropertyDirection.create("direction", listOf(*EnumFacing.HORIZONTALS))