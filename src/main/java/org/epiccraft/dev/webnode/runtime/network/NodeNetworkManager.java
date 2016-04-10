package org.epiccraft.dev.webnode.runtime.network;

import org.epiccraft.dev.webnode.WebNode;
import org.epiccraft.dev.webnode.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.webnode.runtime.network.client.ClientSocket;
import org.epiccraft.dev.webnode.runtime.network.server.ServerSocket;
import org.epiccraft.dev.webnode.structure.Node;
import org.epiccraft.dev.webnode.protocol.NodeInfo;
import org.epiccraft.dev.webnode.structure.channel.ChannelManager;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project WebNode
 */
public class NodeNetworkManager {

    private WebNode server;
    private ServerSocket serverSocket;
    private List<ClientSocket> clientSockets;
    private ConcurrentHashMap<UUID, Node> nodeMap = new ConcurrentHashMap<>();
    private ChannelManager channelManager;

    public NodeNetworkManager(WebNode webNode) {
        server = webNode;
        initialize();
    }

    private void initialize() {
        this.channelManager = new ChannelManager(this);

        try {
            serverSocket = new ServerSocket(this, server.getConfig().localNodeNetworkAddress, server.getConfig().SSL);
            Thread.sleep(3000);
            connectToNewNode(server.getConfig().interfaceNodeNetworkAddress);
        } catch (Exception e) {
            e.printStackTrace();
            server.getLogger().warning("Could not connect to network: " + e.getMessage());
        }
    }

    public Node newNodeConnected(NodeInfo nodeInfo) throws NodeAlreadyConnectedException {
        boolean match = false;
        for (Map.Entry<UUID, Node> uuidNodeEntry : getNodeMap().entrySet()) {
            if (uuidNodeEntry.getKey().equals(nodeInfo.nodeUUID)) {
                match = true;
            }
        }

        if (match) {
            throw new NodeAlreadyConnectedException();
        }

        Node nodeUnit = new Node(this, nodeInfo.nodeUUID, nodeInfo.channels);
        nodeMap.put(nodeInfo.nodeUUID, nodeUnit);
        return nodeUnit;
    }

    public void connectToNewNode(InetSocketAddress address) {
        connectToNewNode(address, server.getConfig().SSL);
    }

    public void connectToNewNode(InetSocketAddress address, boolean ssl) {
        try {
            clientSockets.add(new ClientSocket(this, address, ssl));
        } catch (Exception e) {
            server.getLogger().info("Client socket failed to initialize: " + e.getLocalizedMessage());
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public List<ClientSocket> getClientSocketList() {
        return clientSockets;
    }

    public WebNode getServer() {
        return server;
    }

    public String getNetworkID(InetSocketAddress address) {
        return address.getHostString() + ":" + address.getPort();
    }

    public ConcurrentHashMap<UUID, Node> getNodeMap() {
        return nodeMap;
    }

    public Node getNode(UUID uuid) {
        for (Map.Entry<UUID, Node> uuidNodeEntry : nodeMap.entrySet()) {
            if (uuidNodeEntry.getKey().equals(uuid)) {
                return uuidNodeEntry.getValue();
            }
        }
        return null;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

}
