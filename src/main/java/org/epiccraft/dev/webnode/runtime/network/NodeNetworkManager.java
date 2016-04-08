package org.epiccraft.dev.webnode.runtime.network;

import org.epiccraft.dev.webnode.WebNode;
import org.epiccraft.dev.webnode.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.webnode.runtime.network.client.ClientSocket;
import org.epiccraft.dev.webnode.runtime.network.server.ServerSocket;
import org.epiccraft.dev.webnode.structure.Node;
import org.epiccraft.dev.webnode.structure.NodeInfo;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project WebNode
 */
public class NodeNetworkManager {

    private WebNode node;
    private ServerSocket serverSocket;
    private ClientSocket clientSocket;
    private ConcurrentHashMap<UUID, Node> nodeMap = new ConcurrentHashMap<>();

    public NodeNetworkManager(WebNode webNode) {
        node = webNode;
        initialize();
    }

    public ConcurrentHashMap<UUID, Node> getNodeMap() {
        return nodeMap;
    }

    private void initialize() {
        try {
            serverSocket = new ServerSocket(this, node.getConfig().localNodeNetworkAddress, node.getConfig().SSL);
            Thread.sleep(3000);
            clientSocket = new ClientSocket(this, node.getConfig().interfaceNodeNetworkAddress, node.getConfig().SSL);
        } catch (Exception e) {
            e.printStackTrace();
            node.getLogger().warning("Could not connect to network: " + e.getMessage());
        }
    }
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public ClientSocket getClientSocket() {
		return clientSocket;
	}

    public WebNode getNode() {
        return node;
    }

    public String getNetworkID(InetSocketAddress address) {
        return address.getHostString() + ":" + address.getPort();
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

        Node nodeUnit = new Node(this, nodeInfo.nodeUUID, nodeInfo.nodeGroup);
        nodeMap.put(nodeInfo.nodeUUID, nodeUnit);
        return nodeUnit;
    }

}
