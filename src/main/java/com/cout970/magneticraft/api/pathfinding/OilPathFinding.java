package com.cout970.magneticraft.api.pathfinding;

import com.cout970.magneticraft.ManagerBlocks;
import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.world.IIBlockAccess;
import net.darkaqua.blacksmith.api.world.IWorld;

public class OilPathFinding extends PathFinding {

    public OilPathFinding(IWorld ba, Vect3i start) {
        super(ba, start);
    }

    @Override
    protected boolean hasFailed() {
        return (scanned.size() > 4000) || (toScan.size() > 10000);
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
        IBlock b = field.getBlockVariant(node.getPosition()).getBlock();

        return false;//b == ManagerBlocks.oilSource || b == ManagerBlocks.oilSourceDrained || b == TilePumpJack.fluidOil;
    }
}
