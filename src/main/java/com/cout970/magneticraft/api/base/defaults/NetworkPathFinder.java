package com.cout970.magneticraft.api.base.defaults;

import com.cout970.magneticraft.api.network.INetwork;
import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.ObjectScanner;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cout970 on 30/12/2015.
 */
public class NetworkPathFinder extends PathFinding {

    protected WorldRef ref;
    protected List<INetworkNode> conductors;
    protected INetwork network;
    protected IInterfaceIdentifier identifier;

    public NetworkPathFinder(INetwork net, WorldRef ref, IInterfaceIdentifier identifier) {
        network = net;
        this.ref = ref;
        conductors = new LinkedList<>();
        setStart(ref.getPosition());
        this.identifier = identifier;
    }

    public List<INetworkNode> getConductors() {
        return conductors;
    }

    @Override
    public void addNode(PathNode node, Vect3i dir) {

        if (scanned.size() > 4000) return;
        if (toScan.size() > 10000) return;

        Vect3i vec = node.getPosition().copy().add(dir);

        if (scanned.contains(vec)) return;
        for (PathNode n : toScan) {
            if (n.getPosition().equals(vec))
                return;
        }

        ITileEntity b = ref.getWorld().getTileEntity(vec);
        INetworkNode cond = (INetworkNode) ObjectScanner.findInTileEntity(b, identifier, null);
        if (cond != null && network.canAddToNetwork(cond)) {
            toScan.add(new PathNode(vec, node));
            conductors.add(cond);
        }
    }

    @Override
    public boolean isEnd(PathNode node) {
        return false;
    }
}
