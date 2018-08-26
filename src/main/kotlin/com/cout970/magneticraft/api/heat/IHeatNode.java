package com.cout970.magneticraft.api.heat;

import com.cout970.magneticraft.api.core.INode;

/**
 * Created by Yurgen on 19/10/2016. Modified by Cout970 on 14/5/2018
 */
public interface IHeatNode extends INode {

    // Ideal gas constant
    double R = 8.3144598;

    /**
     * Returns the current temperature of the block in kelvin
     * <p>
     * Calculated with T = (2/3 * U)/ (n * R) where n is the amount of moles in the node and U is the internal energy of
     * the node
     * <p>
     * n will be calculated using the mass and the molar mass: <code>n = mass * 1000 / molar_mass</code> the 1000 comes
     * from the fact that mass is expressed in kilograms but a mole is the amount of atoms in a gram so it needs to be
     * converted from kilograms to grams
     */
    double getTemperature();

    /**
     * Returns the internal energy of this node
     * <p>
     * Represented as U in the first law of thermodynamics: <code>dU = dQ - dW</code> where dQ is the heat
     * extracted/applied and dW id the work done/applied
     */
    double getInternalEnergy();

    /**
     * Returns the mass of the node in kilograms
     */
    double getMass();

    /**
     * Returns the heat conductivity of the node in watts / (meter * kelvin)
     * <p>
     * Amount of watts transferred in a meter with a temperature difference of 1 kelvin
     * <p>
     * The pure iron thermal conductivity is around 73 watts per meter kelvin
     */
    double getConductivity();

    /**
     * Returns the molar mass of the node
     * <p>
     * The default value is the molar mass of iron: 55.845
     */
    double getMolarMass();

    /**
     * Increases or decreases the internal energy of the node using a heat exchange process
     *
     * @param heat the amount of heat to apply/extract in Joules, must be negative for extraction
     */
    void applyHeat(double heat);
}