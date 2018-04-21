package com.cout970.magneticraft.api.computer;

import org.jetbrains.annotations.NotNull;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface ICPU extends IMapSerializable, IResettable {

    /**
     * Sends and interruption to the CPU, this interruption can be used to notify an event like a task finished, a timer
     * or an error
     *
     * @param i the interruption to be handled
     */
    void interrupt(@NotNull IInterruption i);

    /**
     * This method make the CPU advance 1 step, this means that 1 instruction will be executed
     */
    void iterate();

    /**
     * Links the CPU with the other components of the computer
     */
    void setMotherboard(@NotNull IMotherboard mb);

    /**
     * This interface define an interruption to the CPU execution, the CPU will stop working and delegate to the OS the
     * task of handling the event
     */
    interface IInterruption {

        /**
         * Code that the OS will receive to identify the interruption
         *
         * @return interruption code
         */
        int getCode();

        /**
         * The name of the interruption, this is used for debug purposes
         *
         * @return the interruption name
         */
        @NotNull
        String getName();

        /**
         * Some description about what causes the interruption, this is used for debug purposes
         *
         * @return the interruption description
         */
        @NotNull
        String getDescription();
    }
}
