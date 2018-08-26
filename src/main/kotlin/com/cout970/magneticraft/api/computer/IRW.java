package com.cout970.magneticraft.api.computer;

/**
 * Created by cout970 on 2016/09/30.
 * <p>
 * Represent an element that can be accessed to read or write data, for example the RAM, a BUS or an external device
 */
public interface IRW {

    byte readByte(int addr);

    void writeByte(int addr, byte data);

    void writeWord(int addr, int data);

    int readWord(int addr);
}
