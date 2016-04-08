package org.epiccraft.dev.webnode.structure;

import org.epiccraft.dev.webnode.runtime.network.NodeNetworkManager;
import org.epiccraft.dev.webnode.runtime.network.PacketHandler;

import java.util.UUID;

/**
 * Project WebNode
 */
public class Node implements NodeUnit {

    private NodeNetworkManager networkManager;
    public UUID id;
    private PacketHandler handler;

    public NodeGroup getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(NodeGroup fromGroup) {
        this.fromGroup = fromGroup;
    }

    private NodeGroup fromGroup;

    public Node(NodeNetworkManager nodeNetworkManager, UUID nid, String nodeGroup) {
        this.networkManager = nodeNetworkManager;
        this.id = nid;
        NodeGroup.group(this, nodeGroup);
    }

    public UUID getId() {
        return id;
    }

    public PacketHandler getHandler() {
        return handler;
    }

    public Node bindHandler(PacketHandler handler) {
        this.handler = handler;
        return this;
    }

}
