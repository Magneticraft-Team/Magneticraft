package com.cout970.magneticraft.api.computer;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface IRW {

    byte readByte(int addr);

    void writeByte(int addr, byte data);

    void writeWord(int addr, int data);

    int readWord(int addr);
}
