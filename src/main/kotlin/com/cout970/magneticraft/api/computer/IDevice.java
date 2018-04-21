package com.cout970.magneticraft.api.computer;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface IDevice extends IMapSerializable {

    byte readByte(int addr);

    void writeByte(int addr, byte data);
}
