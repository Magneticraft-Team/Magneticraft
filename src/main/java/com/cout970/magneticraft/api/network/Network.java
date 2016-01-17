package com.cout970.magneticraft.api.network;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cout970 on 29/12/2015.
 */
public abstract class Network<T extends INetworkNode> {
    protected List<T> nodes;
    protected T masterNode;

    public Network(T node) {
        nodes = new ArrayList<>();
        nodes.add(node);
        node.setNetwork(this);
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
    }

    public void refreshNetwork() {

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

    }

    public boolean canAddToNetwork(T node) {
        return node != null;
    }
}
