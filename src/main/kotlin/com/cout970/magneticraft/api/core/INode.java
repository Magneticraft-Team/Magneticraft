package com.cout970.magneticraft.api.core;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by cout970 on 16/06/2016.
 */
public interface INode extends INBTSerializable<CompoundNBT>, ITileRef {

    /**
     * Unique identifier of this node
     */
    NodeID getId();
}
