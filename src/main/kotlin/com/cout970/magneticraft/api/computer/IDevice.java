package com.cout970.magneticraft.api.computer;

import com.cout970.magneticraft.api.core.INode;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface IDevice extends INode {

    byte readByte(int addr);

    void writeByte(int addr, byte data);
}
