package com.cout970.magneticraft.api.multiblock;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

public class MultiBlockEvent extends Event {

    // Multiblock that send the event
    private final IMultiblock multiblock;
    // World of the multiblock
    private final World world;
    // Position of the controller block
    private final BlockPos center;
    // Orientation of the multiblock
    private final Direction facing;

    public MultiBlockEvent(IMultiblock multiblock, World world, BlockPos center, Direction facing) {
        this.multiblock = multiblock;
        this.world = world;
        this.center = center;
        this.facing = facing;
    }

    /**
     * Event called before the multiblock starts forming, this can be used to cancel the formation
     */
    public static class CheckIntegrity extends MultiBlockEvent {

        // Player forming the multiblock
        private final PlayerEntity player;
        // Errors found scanning the multiblock, if empty the multiblock will form, otherwise it will show the errors
        private final List<ITextComponent> integrityErrors;

        public CheckIntegrity(IMultiblock multiblock, World world, BlockPos center, Direction facing,
                              PlayerEntity player, List<ITextComponent> integrityErrors) {
            super(multiblock, world, center, facing);
            this.player = player;
            this.integrityErrors = integrityErrors;
        }

        public PlayerEntity getPlayer() {
            return player;
        }

        // You can edit the list to prevent the formation of the multiblock
        public List<ITextComponent> getIntegrityErrors() {
            return integrityErrors;
        }
    }

    /**
     * Called after the multiblock activation
     */
    public static class Activate extends MultiBlockEvent {

        public Activate(IMultiblock multiblock, World world, BlockPos center, Direction facing) {
            super(multiblock, world, center, facing);
        }
    }

    /**
     * Called after the multiblock deactivation
     */
    public static class Deactivate extends MultiBlockEvent {

        public Deactivate(IMultiblock multiblock, World world, BlockPos center, Direction facing) {
            super(multiblock, world, center, facing);
        }
    }

    public IMultiblock getMultiblock() {
        return multiblock;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getCenter() {
        return center;
    }

    public Direction getFacing() {
        return facing;
    }
}
