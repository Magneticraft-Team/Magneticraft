package com.cout970.magneticraft.api.heat;

import com.cout970.magneticraft.api.energy.INode;

/**
 * Created by Yurgen on 19/10/2016.
 */
public interface IHeatNode extends INode {

    /**
     * Returns the current temperature of the block
     */
    double getTemperature();

    /**
     * Returns the thermal conductivity of the block
     */
    double getConductivity();

    /**
     * Sets the heat dissipation of the block
     */
    @Deprecated
    void setConductivity(double newConductivity);

    /**
     * Returns the heat dissipation of the block
     */
    double getDissipation();

    /**
     * Sets the heat dissipation of the block
     */
    @Deprecated
    void setDissipation(double newDissipation);

    /**
     * Returns the current heat content of the block
     */
    long getHeat();

    /**
     * Sets the current heat content of the block
     */
    void setHeat(long newHeat);

    /**
     * Return if this node corresponds to a light which should emit light at high temperatures
     */
    boolean emitsLight();

    /**
     * Sets the ambient temperature of the block
     */
    void setAmbientTemp(double newAmbient);

    /**
     * Returns the heat capacity of the block
     */
    double getSpecificHeat();

    /**
     * Returns the maximum temperature of the block
     * What happens on exceeding maximum temperature is implementation defined.
     * Default should be refusing heat input
     */
    double getMaxTemperature();

    /**
     * Returns the maximum heat content of the block
     */
    long getMaxHeat();

    /**
     * High temperature blocks should attempt to push heat into low temperature blocks every second based
     * based on temperature difference and conductivity
     * Returns any heat left over after push
     * If simulate is true, don't actually move any heat
     */
    long pushHeat(long heatIn, boolean simulate);

    /**
     * Returns actual heat pulled
     * If simulate is true, don't actually move any heat
     */
    long pullHeat(long heatOut, boolean simulate);

    /**
     * To be called when block exceeds maximum temperature
     */
    @Deprecated
    void onOverTemperature();

    /**
     * Called every tick to transfer heat
     */
    @Deprecated
    void updateHeat();
}