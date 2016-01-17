package com.cout970.magneticraft.api.base.defaults;

import com.cout970.magneticraft.api.network.INetwork;
import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cout970 on 30/12/2015.
 */
public abstract class NetworkBase implements INetwork {

    protected List<INetworkNode> nodes;

    public NetworkBase(INetworkNode start) {
        nodes = new LinkedList<>();
        nodes.add(start);
    }

    @Override
    public INetworkNode getFirstNode() {
        return nodes.get(0);
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public List<INetworkNode> getNetworkNodes() {
        return new ArrayList<>(nodes);
    }

    @Override
    public void addNetworkNode(INetworkNode node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            onNetworkChange();
        }
        node.setNetwork(this);
    }

    @Override
    public void removeNetworkNode(INetworkNode node) {
        if (nodes.contains(node)) {
            nodes.remove(node);
            refreshNetwork();
        }
    }

    @Override
    public void refreshNetwork() {
        List<INetworkNode> list = getNetworkNodes();
        for (INetworkNode node : nodes) {
            node.setNetwork(null);
        }

        nodes.clear();
        for (INetworkNode node : list) {
            if (node.getNetwork() == null && node.isValid()) {
                NetworkPathFinder pathFinder = new NetworkPathFinder(this, node.getWorldReference(), getInterfaceIdentifier());
                pathFinder.getPathEnd();
                node.createNetwork();
                for (INetworkNode n : pathFinder.getConductors()) {
                    node.getNetwork().addNetworkNode(n);
                }
            }
        }
        onNetworkChange();
    }

    protected abstract IInterfaceIdentifier getInterfaceIdentifier();

    public void onNetworkChange() {
        for (INetworkNode node : getNetworkNodes()) {
            node.onNetworkChange();
        }
    }

    @Override
    public void mergerNetwork(INetwork net) {
        List<INetworkNode> list = net.getNetworkNodes();
        net.destroyNetwork();
        nodes.addAll(list);
        for (INetworkNode n : list) {
            n.setNetwork(this);
        }
    }

    @Override
    public void destroyNetwork() {
        for (INetworkNode node : nodes) {
            node.setNetwork(null);
        }
        nodes.clear();
    }
}
