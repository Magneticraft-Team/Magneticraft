package com.cout970.magneticraft.api.heat;

import com.cout970.magneticraft.api.energy.INode;
import net.minecraft.util.math.BlockPos;

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
     * Returns the heat dissipation of the block
     */
    double getDissipation();

    /**
     * Returns the current heat content of the block
     */
    long getHeat();

    /**
     * Sets the current heat content of the block
     */
    void setHeat(long newHeat);


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
    void onOverTemperature();

    /**
     * Called every tick to transfer heat
     */
    void updateHeat();

    BlockPos getPos();
}