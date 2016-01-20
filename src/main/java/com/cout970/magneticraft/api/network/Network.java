package com.cout970.magneticraft.api.network;

import com.cout970.magneticraft.api.pathfinding.NetworkPathFinding;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Network<T extends INetworkNode> {
    protected List<T> nodes;
    protected T masterNode;

    public Network(T node) {
        nodes = new ArrayList<>();
        nodes.add(node);
        node.setNetwork(this);
        this.masterNode = node;
    }

    public void iterate() {
        List<T> tmp = new ArrayList<>(nodes);
    }

    public INetworkNode getMasterNode() {
        return masterNode;
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public List<T> getNetworkNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public void addNetworkNode(@Nonnull T node) {
        if (!canAddToNetwork(node)) {
            return;
        }

        nodes.add(node);
        node.setNetwork(this);

        onNetworkChange();
    }

    public void removeNetworkNode(@Nonnull T node) {
        node.setNetwork(null);

        nodes.remove(node);
        if (nodes.isEmpty()) {
            destroyNetwork();
            return;
        }

        if (masterNode.equals(node)) {
            masterNode = nodes.get(0);
        }

        onNetworkChange();
    }

    public void refreshNetwork() {
        List<T> nList = new ArrayList<>(nodes);
        nodes.forEach(n -> n.setNetwork(null));

        nodes.clear();
        nList.stream().filter(n -> n != null && n.isValid()).forEach(n -> {
            NetworkPathFinding<T> path = new NetworkPathFinding<>(this, n.getWorldReference());
            while (!path.isDone()) {
                path.iterate(10000);
            }

            n.createNetwork();
            path.getResult().getNodes()
                    .forEach(n.getNetwork()::addNetworkNode);
        });
    }

    public void onNetworkChange() {

    }

    public void expandNetwork(Network<? extends T> net) {
        nodes.addAll(net.getNetworkNodes());
        net.mergeIntoNetwork(this);
    }

    protected void mergeIntoNetwork(Network<? super T> net) {
        nodes.forEach(n -> n.setNetwork(net));
        masterNode = null;
        nodes.clear();
    }

    public void destroyNetwork() {
        nodes.forEach(n -> n.setNetwork(null));
        nodes.clear();

        masterNode = null;
    }

    public boolean canAddToNetwork(T node) {
        return node != null && !nodes.contains(node);
    }

    public abstract IInterfaceIdentifier<T> getInterfaceIdentifier();
}
