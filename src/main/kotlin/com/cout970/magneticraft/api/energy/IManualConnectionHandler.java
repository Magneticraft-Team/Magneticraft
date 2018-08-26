package com.cout970.magneticraft.api.energy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by cout970 on 17/07/2016.
 * <p>
 * This interface is intended to be implemented by a block, to allow the player to manually connect wires between
 * blocks
 */
public interface IManualConnectionHandler {

    /**
     * This method retrieves the position that will be stored in the ItemStack to call
     * <code>IManualConnectionHandler.connectWire()</code> in the future
     *
     * @param thisBlock The position of this block
     * @param world The world where this block is
     * @param player The player that is connecting the wire
     * @param side The side of the block where the player is clicking
     * @param stack The item used to store the position, can be used to send data to
     * <code>IManualConnectionHandler.connectWire()</code>
     *
     * @return the position to store in the item
     */
    BlockPos getBasePos(BlockPos thisBlock, World world, EntityPlayer player, EnumFacing side, ItemStack stack);

    /**
     * @param otherBlock The position other the other block obtained from <code>IManualConnectionHandler.getBasePos()</code>
     * @param thisBlock The position of this block
     * @param world The world where this block is
     * @param player The player that is connecting the wire
     * @param side The side of the block where the player is clicking
     * @param stack The item that stores the position of the other block, can have data from
     * <code>IManualConnectionHandler.getBasePos()</code>
     *
     * @return true if the connection has been set, false otherwise
     */
    Result connectWire(BlockPos otherBlock, BlockPos thisBlock, World world, EntityPlayer player, EnumFacing side,
                       ItemStack stack);

    enum Result {
        SUCCESS,
        TOO_FAR,
        NOT_A_CONNECTOR,
        INVALID_CONNECTOR,
        SAME_CONNECTOR,
        ALREADY_CONNECTED,
        ERROR
    }
}
