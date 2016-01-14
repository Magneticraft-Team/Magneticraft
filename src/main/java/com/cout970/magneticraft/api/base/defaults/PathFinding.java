package com.cout970.magneticraft.api.base.defaults;

import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3i;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by cout970 on 30/12/2015.
 */
public abstract class PathFinding {

    protected HashSet<Vect3i> scanned;
    protected Queue<PathNode> toScan;
    protected Vect3i start;
    protected Vect3i end;

    public PathFinding() {
        scanned = new HashSet<>();
        toScan = new LinkedList<>();
    }

    public void setStart(Vect3i vec) {
        start = vec.copy();
    }

    public void setEnd(Vect3i vec) {
        end = vec.copy();
    }

    public void addAdjacentNodes(PathNode node) {
        for (Direction d : Direction.values())
            addNode(node, d.toVect3i());
    }

    public PathNode scan(PathNode node) {

        scanned.add(node.getPosition());
        if (isEnd(node)) {
            return node;
        }
        addAdjacentNodes(node);
        return null;
    }

    public abstract void addNode(PathNode node, Vect3i dir);

    public abstract boolean isEnd(PathNode node);

    public LinkedList<Vect3i> getPath() {
        PathNode node = getPathEnd();

        if (node != null) {
            LinkedList<Vect3i> path = new LinkedList<>();
            for (PathNode current = node; current.getBefore() != null; current = current.getBefore()) {
                path.addFirst(current.getPosition());
            }
            return path;
        }

        return null;
    }

    public PathNode getPathEnd() {
        toScan.clear();
        scanned.clear();
        addNode(new PathNode(start, null), Vect3i.nullVector());
        PathNode node = null;

        while (!toScan.isEmpty()) {
            node = scan(toScan.poll());
            if (node != null) {
                break;
            }
        }

        return node;
    }
}
