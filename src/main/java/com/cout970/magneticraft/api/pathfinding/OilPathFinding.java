package com.cout970.magneticraft.api.pathfinding;

import net.darkaqua.blacksmith.api.common.block.IBlock;
import net.darkaqua.blacksmith.api.common.util.vectors.Vect3i;
import net.darkaqua.blacksmith.api.common.world.IWorld;

public class OilPathFinding extends PathFinding {

    public OilPathFinding(IWorld ba, Vect3i start) {
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
        IBlock b = field.getBlockData(node.getPosition()).getBlock();

        return false;//b == ManagerBlocks.oilSource || b == ManagerBlocks.oilSourceDrained || b == TilePumpJack.fluidOil;
    }
}
