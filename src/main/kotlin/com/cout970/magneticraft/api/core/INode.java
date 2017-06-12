package com.cout970.magneticraft.api.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by cout970 on 16/06/2016.
 */
public interface INode extends INBTSerializable<NBTTagCompound> {

    /**
     * @return The world where this node is
     */
    World getWorld();

    /**
     * @return The position where this node is
     */
    BlockPos getPos();

    /**
     * Unique identifier of this node
     */
    NodeID getId();
}
