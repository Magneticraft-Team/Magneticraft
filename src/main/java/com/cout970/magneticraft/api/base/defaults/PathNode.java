package com.cout970.magneticraft.api.base.defaults;

import net.darkaqua.blacksmith.api.util.Vect3i;

/**
 * Created by cout970 on 30/12/2015.
 */
public class PathNode {

    private Vect3i position;
    private PathNode before;

    public PathNode(Vect3i position, PathNode node) {
        this.position = position.copy();
        before = node;
    }

    public boolean isStart() {
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
}
