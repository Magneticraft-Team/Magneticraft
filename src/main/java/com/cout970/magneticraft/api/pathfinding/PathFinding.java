package com.cout970.magneticraft.api.pathfinding;

import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.world.IWorld;

import java.util.*;
import java.util.stream.Collectors;

public abstract class PathFinding {
    protected List<PathNode> scanned;
    protected Queue<PathNode> toScan;
    protected IWorld field;
    protected State currentState;
    protected PathNode goal;
    protected PathNode start;

    /**
     * Initializes a new PathFinding instance that will work on given field, starting with a block at given position.
     *
     * @param ba {@link IWorld} that will provide information about blocks for the algorithm
     * @param start {@link Vect3i} containing coordinates of a start block.
     */
    public PathFinding(IWorld ba, Vect3i start) {
        scanned = new ArrayList<>();
        toScan = new ArrayDeque<>();
        this.start = new PathNode(start, null);
        field = ba;
        currentState = State.NOT_STARTED;
        goal = null;
    }

    /**
     * Iterates algorithms for a maximum of <code>maxIterations</code> steps. This ensures that algorithm can be
     * interrupted and resumed later. Default implementation uses Breadth-First Search algorithm.
     *
     * @param maxIterations maximal number of iterations
     */
    public void iterate(int maxIterations) {
        //prepare for work by adding initial node
        if (currentState == State.NOT_STARTED) {
            toScan.add(start);
            currentState = State.WORKING;
        }

        //don't try to work if it's done
        if (isDone()) {
            return;
        }

        //stop working on fail condition
        if (hasFailed()) {
            currentState = State.FAIL;
            return;
        }

        int curIterations = 0;
        PathNode node;
        while ((node = toScan.poll()) != null) {
            scanned.add(node);

            //if reached end, finalize work
            if (isGoal(node)) {
                goal = node;
                currentState = State.SUCCESS;
                return;
            }

            //add all adjacent blocks if they are suitable and have not been added yet
            for (Direction dir : Direction.values()) {
                PathNode next = node.step(dir);
                if (!isPath(next)) {
                    continue;
                }

                if (scanned.stream().anyMatch(n -> n.getPosition().equals(next.getPosition()))) {
                    continue;
                }

                if (toScan.stream().anyMatch(n -> n.getPosition().equals(next.getPosition()))) {
                    continue;
                }
                toScan.add(next);
            }

            if ((++curIterations) >= maxIterations) {
                break;
            }
        }

        //if no blocks are left, search failed
        if (toScan.isEmpty()) {
            currentState = State.FAIL;
        }
    }

    /**
     * Determines if algorithm has reached its failing condition and should stop iterating
     *
     * @return <code>true</code> if algorithm has failed and <code>false</code> otherwise
     */
    protected abstract boolean hasFailed();

    /**
     * Determines if this algorithm has a goal, such as oil block, TileEntity with specific properties, etc.
     *
     * @return <code>true</code> if algorithm has a goal and <code>false</code> otherwise.
     */
    public abstract boolean hasGoal();

    /**
     * Determines if given {@link PathNode} is an algorithm's goal
     *
     * @param node potential goal node
     *
     * @return <code>true</code> if given PathNode is a suitable goal and <code>false</code> otherwise.
     */
    public abstract boolean isGoal(PathNode node);

    /**
     * Determines if algorithm can move through a given {@link PathNode} to another one
     *
     * @param node potential path node
     *
     * @return <code>true</code> if given PathNode can be a part of the path and <code>false</code> otherwise.
     */
    public abstract boolean isPath(PathNode node);

    /**
     * Determines if algorithm is done working and that its result can now be used. Note that this does not mean
     * that algorithm has reached its goal, because it may have failed or could have no goal in the first place.
     *
     * @return <code>true</code> if algorithm has completed its work and <code>false</code> otherwise.
     */
    public boolean isDone() {
        return (currentState == State.FAIL) || (currentState == State.SUCCESS);
    }

    /**
     * Determines current state of the algorithm
     *
     * @see State
     *
     * @return current {@link State} of the algorithm
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * returns the {@link Result} of algorithm's operation
     *
     * @return instance of {@link Result} containing data about algorithm's operation or <code>null</code> if algorithm
     * is still running
     *
     * @see Result
     */
    public Result getResult() {
        if (!isDone()) {
            return null;
        }

        return new Result(this);
    }

    public enum State {
        NOT_STARTED,
        WORKING,
        FAIL,
        SUCCESS
    }

    public static class Result {
        private final Vect3i goal;
        private final List<Vect3i> pathToGoal;
        private final List<Vect3i> scanned;
        private final List<Vect3i> toScan;
        private final List<Vect3i> all;
        private final boolean success;

        public Result(PathFinding p) {
            success = p.currentState == State.SUCCESS;
            toScan = p.toScan.stream().map(PathNode::getPosition).collect(Collectors.toList());
            scanned = p.scanned.stream().map(PathNode::getPosition).collect(Collectors.toList());
            all = new ArrayList<>();
            all.addAll(toScan);
            all.addAll(scanned);

            goal = (p.goal == null) ? null : p.goal.getPosition();
            if (goal == null) {
                pathToGoal = null;
            } else {
                pathToGoal = new ArrayList<>();
                PathNode t = p.goal;
                while (t != null) {
                    pathToGoal.add(t.getPosition());
                    t = t.getBefore();
                }
                Collections.reverse(pathToGoal);
            }
        }

        public List<Vect3i> getScanned() {
            return scanned;
        }

        public List<Vect3i> getToScan() {
            return toScan;
        }

        public List<Vect3i> getAllScanned() {
            return all;
        }

        public boolean isSuccessful() {
            return success;
        }
    }
}
