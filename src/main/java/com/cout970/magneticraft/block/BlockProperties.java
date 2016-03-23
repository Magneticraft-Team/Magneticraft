package com.cout970.magneticraft.block;

import net.darkaqua.blacksmith.util.Direction;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;

/**
 * Created by cout970 on 20/03/2016.
 */
public interface BlockProperties {
    IProperty<Direction> ATTRIBUTE_ALL_DIRECTIONS = PropertyEnum.create("all_directions", Direction.class);
    IProperty<Direction> ATTRIBUTE_HORIZONTAL_DIRECTIONS = PropertyEnum.create("horizontal_directions", Direction.class, d -> d.getOffsetY() == 0);
    IProperty<Boolean> ACTIVATION = PropertyBool.create("activation");
}
