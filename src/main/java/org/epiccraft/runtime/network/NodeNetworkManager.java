package org.epiccraft.runtime.network;

import org.epiccraft.WebNode;

/**
 * Project WebNode
 */
public class NodeNetworkManager {

    private WebNode node;
    private ServerSocket serverSocket;
    private ClientSocket clientSocket;

    public NodeNetworkManager(WebNode webNode) {
        node = webNode;
        initialize();
    }

    private void initialize() {
        try {
            serverSocket = new ServerSocket(this, node.getConfig().localNodeNetworkAddress, node.getConfig().SSL);
            clientSocket = new ClientSocket(this, node.getConfig().interfaceNodeNetworkAddress, node.getConfig().SSL);
        } catch (Exception e) {
            node.getLogger().warning("Could not connect to network: " + e.getMessage());
        }
    }

}
