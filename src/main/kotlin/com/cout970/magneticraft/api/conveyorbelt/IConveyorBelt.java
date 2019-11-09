package com.cout970.magneticraft.api.conveyorbelt;

import com.cout970.magneticraft.systems.tilemodules.conveyorbelt.BoxedItem;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public interface IConveyorBelt {

    Direction getFacing();

    List<BoxedItem> getBoxes();

    int getLevel();

    boolean addItem(ItemStack stack, boolean simulated);

    boolean addItem(ItemStack stack, Direction side, Route oldRoute);
}
