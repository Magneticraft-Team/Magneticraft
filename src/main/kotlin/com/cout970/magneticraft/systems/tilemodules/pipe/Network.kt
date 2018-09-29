package com.cout970.magneticraft.systems.tilemodules.pipe

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/28.
 */

typealias InspectFunc = (TileEntity, EnumFacing) -> List<IPathFindingNode>

abstract class Network<T : INetworkNode>(
    mainNode: T,
    val inspectFunc: InspectFunc,
    val factory: (T) -> Network<T>
) {

    val members = mutableSetOf(mainNode)

    fun add(node: T) {
        setNetwork(node, this)
        members.add(node)
        clearCache()
    }

    fun remove(node: T) {
        members.remove(node)
        clearCache()
    }

    // Destroys this network and create new ones for the isolated parts
    fun split(node: T) {
        clearCache()
        val remaining = (members - node).toMutableList()
        remaining.forEach { it.network = null }

        while (true) {
            val it = remaining.firstOrNull() ?: return
            val net = factory.invoke(it)
            setNetwork(node, net)
            net.expand()
            remaining.removeAll { it.network != null }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun expand() {
        clearCache()
        val first = members.firstOrNull() ?: return
        val results = nearestFirstSearch(first, inspectFunc)
        results.forEach { add(it as T) }
    }

    abstract fun clearCache()

//    // merges the members of this network with other network, this network is destroyed in the process
//    fun merge(other: Network<T>) {
//        members.forEach { other.add(it) }
//        members.clear()
//    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <T : INetworkNode> setNetwork(node: T, net: Network<T>?) {
            if (node.network != net) {
                (node.network as? Network<T>)?.remove(node)
            }
            node.network = net
        }
    }
}


interface INetworkNode : IPathFindingNode {

    var network: Network<*>?
}