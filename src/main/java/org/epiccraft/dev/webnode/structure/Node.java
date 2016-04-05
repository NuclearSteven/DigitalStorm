package org.epiccraft.dev.webnode.structure;

import org.epiccraft.dev.webnode.runtime.network.NodeNetworkManager;
import org.epiccraft.dev.webnode.runtime.network.ServerHandler;

/**
 * Project WebNode
 */
public class Node implements NodeUnit {

    private NodeNetworkManager networkManager;
    private ServerHandler serverHandler;
    public long id;

    public Node(NodeNetworkManager nodeNetworkManager, ServerHandler serverHandler, long nid) {
        this.networkManager = nodeNetworkManager;
        this.serverHandler = serverHandler;
        this.id = nid;
        NodeGroup.group(this);
    }

}
