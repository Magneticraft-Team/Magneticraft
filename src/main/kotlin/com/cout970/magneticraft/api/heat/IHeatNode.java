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
     * Fraction of temperature difference between current and ambient temperature dissipated per second
     * Small values cause fast heat transfer.
     * Very large values can cause strange directional transfer behavior || why?? //TODO fix
     * default value: 1.0
     */
    double getConductivity();

    /**
     * Returns the heat dissipation of the block
     * Fraction of temperature difference between current and ambient temperature dissipated per second
     * Small values cause fast heat dissipation
     * default value: 0.0
     */
    double getDissipation();

    /**
     * Returns the current heat content of the block
     */
    double getHeat();

    /**
     * Returns the maximum heat content of the block
     */
    double getMaxHeat();

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
     * Inserts heat or extracts heat from the node
     *
     * @param heatArg heat ti insert if positive, heat to extract if negative
     * @param simulate true if the internal heat should not be modified
     * @return heat inserted or extracted, always positive
     */
    double applyHeat(double heatArg, boolean simulate);

    /**
     * Called every tick to dissipate heat
     */
    void iterate();
}