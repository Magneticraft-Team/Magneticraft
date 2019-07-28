package com.cout970.magneticraft.api.computer;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface IDevice extends IMapSerializable {

    void update();

    byte readByte(IRW bus, int addr);

    void writeByte(IRW bus, int addr, byte data);
}
