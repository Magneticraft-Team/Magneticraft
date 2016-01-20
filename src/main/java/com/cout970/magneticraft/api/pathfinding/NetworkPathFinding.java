package com.cout970.magneticraft.api.pathfinding;

import com.cout970.magneticraft.api.network.INetworkNode;
import com.cout970.magneticraft.api.network.Network;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.ObjectScanner;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

import java.util.ArrayList;
import java.util.List;

public class NetworkPathFinding<T extends INetworkNode> extends PathFinding {
    IInterfaceIdentifier id;
    Network<T> network;

    /**
     * Initializes a new PathFinding instance that will work on given field, starting with a block at given position.
     *
     * @param net {@link Network} to check nodes against
     * @param ba    {@link IWorld} that will provide information about blocks for the algorithm
     * @param start {@link Vect3i} containing coordinates of a start block.
     */
    public NetworkPathFinding(Network<T> net, IWorld ba, Vect3i start) {
        super(ba, start);
        network = net;
    }

    public NetworkPathFinding(Network<T> net, WorldRef w) {
        this(net, w.getWorld(), w.getPosition());
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
    public Result getResult() {
        if (!isDone()) {
            return null;
        }

        return new Result(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isPath(PathNode node) {
        ITileEntity tile = field.getTileEntity(node.getPosition());
        T n = ObjectScanner.findInTileEntity(tile, network.getInterfaceIdentifier(), null);
        return network.canAddToNetwork(n);
    }

    public class Result extends PathFinding.Result {
        private List<T> nodes;

        public Result(NetworkPathFinding<T> p) {
            super(p);
            nodes = new ArrayList<>();
            getAllScanned().stream()
                    .map(p.field::getTileEntity)
                    .map(t -> ObjectScanner.findInTileEntity(t, p.network.getInterfaceIdentifier(), null))
                    .forEach(nodes::add);
        }

        public List<T> getNodes() {
            return nodes;
        }
    }
}
