package com.cout970.magneticraft.api.computer;

/**
 * Created by cout970 on 2016/09/30.
 * <p>
 * Represents the RAM of the computer
 */
public interface IRAM extends IRW, IMapSerializable {

    /**
     * The order of the bytes in the CPU
     * <p>
     * For the integer 0x12345678 will be stored as: - Little Endian: 0x78 0x56 0x34 0x12 - Big Endian:    0x12 0x23
     * 0x45 0x78
     *
     * @return the endianness of the RAM
     */
    boolean isLittleEndian();

    /**
     * The max size of the memory
     *
     * @return amount of addressable bytes in the memory
     */
    int getMemorySize();
}
