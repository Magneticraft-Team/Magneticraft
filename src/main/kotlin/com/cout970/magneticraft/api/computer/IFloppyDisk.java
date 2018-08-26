package com.cout970.magneticraft.api.computer;

import java.io.File;

/**
 * Created by cout970 on 2016/10/13.
 */
public interface IFloppyDisk {

    /**
     * Gets the file used to store all the data in this floppy disk, this can change using setLabel(...)
     */
    File getStorageFile();

    /**
     * Gets the name of the disk, this can be changed by the computer This is used to display the name of the disk in
     * the MC item
     */
    String getLabel();

    /**
     * Sets the name of the disk, this will try to change the name of the file where the data is stored
     */
    void setLabel(String str);

    /**
     * Unique id used to detect when a disk has been replaced by another
     */
    int getSerialNumber();

    /**
     * Gets tha number of sector of the disk, every sector has 1Kb (1024B), this is used to get the size of the disk
     */
    int getSectorCount();

    /**
     * Gets the amount of ticks to wait for the read/write operations
     */
    int getAccessTime();

    /**
     * Checks if the floppy disk allow to read the sectors
     */
    boolean canRead();

    /**
     * Checks if the floppy disk allow to write the sectors
     */
    boolean canWrite();
}
