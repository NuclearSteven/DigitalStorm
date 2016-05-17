package org.epiccraft.dev.digitalstorm.runtime.network;

import org.epiccraft.dev.digitalstorm.DigitalStorm;
import org.epiccraft.dev.digitalstorm.event.RawConnetedEvent;
import org.epiccraft.dev.digitalstorm.event.RawDisconnectEvent;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfo;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.digitalstorm.runtime.network.client.ClientSocket;
import org.epiccraft.dev.digitalstorm.runtime.network.server.ServerSocket;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project DigitalStorm
 */
public class NetworkManager extends Thread {

    private DigitalStorm digitalStorm;
    private ServerSocket serverSocket;
    private List<ClientSocket> clientSockets;
    private ConcurrentHashMap<UUID, Node> nodeMap = new ConcurrentHashMap<>();
    private UUID uuid;

    public NetworkManager(DigitalStorm digitalStorm) {
        this.digitalStorm = digitalStorm;
        clientSockets = new LinkedList<>();
        uuid = UUID.randomUUID();
        clientSockets = new LinkedList<>();
        start();
    }

    @Override
    public void run() {
        initialize();
    }

    private void initialize() {
        try {
            if (digitalStorm.getConfig().serverSideTraffic)
                serverSocket = new ServerSocket(this, digitalStorm.getConfig().localNodeNetworkAddress, digitalStorm.getConfig().SSL);
            if (digitalStorm.getConfig().clientSideTraffic)
                connectToNewNode(digitalStorm.getConfig().interfaceNodeNetworkAddress);
        } catch (Exception e) {
            e.printStackTrace();
            digitalStorm.getLogger().warning("Could not connect to network: " + e.getMessage());
        }
    }

    public void connectToNewNode(InetSocketAddress address) {
        connectToNewNode(address, digitalStorm.getConfig().SSL);
    }
	
    public void connectToNewNode(InetSocketAddress address, boolean ssl) {
        try {
            clientSockets.add(new ClientSocket(this, address, ssl));
        } catch (Exception e) {
            e.printStackTrace();
            digitalStorm.getLogger().info("Client socket failed to initialize: " + e.getLocalizedMessage());
        }
    }

    public Node newNodeConnected(NodeInfo nodeInfo, SocketAddress socketAddress) throws NodeAlreadyConnectedException {
        boolean match = false;
        for (Map.Entry<UUID, Node> uuidNodeEntry : getNodeMap().entrySet()) {
            if (uuidNodeEntry.getKey().equals(nodeInfo.nodeUUID)) {
                match = true;
            }
        }

        if (match) {
            throw new NodeAlreadyConnectedException();
        }

        Node node = new Node(this, nodeInfo.nodeUUID);
        nodeMap.put(nodeInfo.nodeUUID, node);

        digitalStorm.getEventFactory().broadcastEvent(new RawConnetedEvent(socketAddress));

        return node;
    }

    public void nodeDisconnected(SocketAddress socketAddress) {
        nodeMap.entrySet().stream().filter(uuidNodeEntry -> uuidNodeEntry.getValue().getHandler().getSocketChannel().remoteAddress().equals(socketAddress)).forEach(uuidNodeEntry -> {
            nodeMap.remove(uuidNodeEntry.getKey());
        });

        digitalStorm.getEventFactory().broadcastEvent(new RawDisconnectEvent(socketAddress));
    }

    public void packetReceived(Packet msg, PacketHandler packetHandler) {
        digitalStorm.getEventFactory().broadcastPacket(msg, packetHandler);
    }

    public ConcurrentHashMap<UUID, Node> getNodeMap() {
        return nodeMap;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public List<ClientSocket> getClientSocketList() {
        return clientSockets;
    }

    public DigitalStorm getDigitalStorm() {
        return digitalStorm;
    }

    public String getNetworkID(InetSocketAddress address) {
        return address.getHostString() + ":" + address.getPort();
    }

    public Node getNode(UUID uuid) {
        for (Map.Entry<UUID, Node> uuidNodeEntry : nodeMap.entrySet()) {
            if (uuidNodeEntry.getKey().equals(uuid)) {
                return uuidNodeEntry.getValue();
            }
        }
        return null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<ClientSocket> getClientSockets() {
        return clientSockets;
    }

}
