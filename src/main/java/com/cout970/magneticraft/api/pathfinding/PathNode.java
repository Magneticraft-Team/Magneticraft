package com.cout970.magneticraft.api.pathfinding;

import net.darkaqua.blacksmith.api.common.util.Direction;
import net.darkaqua.blacksmith.api.common.util.vectors.Vect3i;

public class PathNode {

    private Vect3i position;
    private PathNode before;

    public PathNode(Vect3i position, PathNode node) {
        this.position = position;
        before = node;
    }

    public boolean isStart(){
        return before == null;
    }

    public PathNode getBefore() {
        return before;
    }

    public void setBefore(PathNode before) {
        this.before = before;
    }

    public Vect3i getPosition() {
        return position;
    }

    public void setPosition(Vect3i position) {
        this.position = position;
    }

    public PathNode step(Direction dir) {
        return new PathNode(position.add(dir), this);
    }
}
