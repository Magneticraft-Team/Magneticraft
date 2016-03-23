package com.cout970.magneticraft.api.pathfinding;


import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class OilPathFinding extends PathFinding {

    public OilPathFinding(World ba, Vect3i start) {
        super(ba, start);
    }

    @Override
    protected boolean hasFailed() {
        return super.hasFailed() || (scanned.size() > 4000) || (toScan.size() > 10000);
    }

    @Override
    public boolean hasGoal() {
        return false;
    }

    @Override
    public boolean isGoal(PathNode node) {
        return false;
    }

    @Override
    public boolean isPath(PathNode node) {
        Block b = field.getBlockState(node.getPosition().toBlockPos()).getBlock();
        return false;//b == ManagerBlocks.oilSource || b == ManagerBlocks.oilSourceDrained || b == TilePumpJack.fluidOil;
    }
}
