package com.cout970.magneticraft.api.computer;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface IMotherboard extends IMapSerializable, IResettable {

    /**
     * The bus used in the components of the computer to communicate with each other
     *
     * @return the system bus
     */
    IRW getBus();

    /**
     * The cpu of the computer
     *
     * @return the computer cpu
     */
    ICPU getCPU();

    /**
     * The rom installed in the computer
     *
     * @return the computer rom
     */
    IROM getROM();

    /**
     * The ram installed in the computer
     *
     * @return the computer ram
     */
    IRAM getRAM();

    /**
     * The system clock, measure the number of cycles of the cpu since the computer started working, is set to 0 when
     * the computer restarts
     *
     * @return the system clock
     */
    int getClock();

    /**
     * Starts cpu clock
     */
    void start();

    /**
     * Stops the cpu clock
     */
    void halt();

    /**
     * Checks if the cpu clock is running or stopped
     *
     * @return true if the cpu clock running, false otherwise
     */
    boolean isOnline();
}
