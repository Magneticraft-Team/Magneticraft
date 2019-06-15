package com.cout970.magneticraft.api.pneumatic;

/**
 * This interface is provided by pneumatic machines/tubes
 */
public interface ITubeConnectable {

    /**
     * Add an item to this block
     *
     * @param box item
     * @param mode current state of the item
     * @return if the insertion was successful
     */
    boolean insert(PneumaticBox box, PneumaticMode mode);

    /**
     * Indicates if a item can enter this block
     *
     * @param box item
     * @param mode current state of the item
     * @return if the item can be inserted
     */
    boolean canInsert(PneumaticBox box, PneumaticMode mode);

    /**
     * This parameter affect how far the block is perceived for the pathfinder
     * <p>
     * Used by restriction tubes
     * <p>
     * Also normal blocks use this to avoid accumulation of items in buffers, they do this by returning the amount of
     * items in the buffer, so the items go further and find other blocks
     *
     * @return distance in blocks
     */
    int getWeight();
}
