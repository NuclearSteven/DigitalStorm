package org.epiccraft.dev.digitalstorm.runtime.network;

import org.epiccraft.dev.digitalstorm.DigitalStorm;
import org.epiccraft.dev.digitalstorm.event.RawClientDisconnectEvent;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfo;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.digitalstorm.runtime.network.client.ClientSocket;
import org.epiccraft.dev.digitalstorm.runtime.network.server.ServerHandler;
import org.epiccraft.dev.digitalstorm.runtime.network.server.ServerSocket;
import org.epiccraft.dev.digitalstorm.structure.Node;
import org.epiccraft.dev.digitalstorm.structure.channel.Channel;
import org.epiccraft.dev.digitalstorm.structure.channel.ChannelManager;
import org.epiccraft.dev.digitalstorm.structure.channel.LocalMachine;

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
public class NetworkManager {

    private DigitalStorm digitalStorm;
    private ServerSocket serverSocket;
    private List<ClientSocket> clientSockets;
    private ConcurrentHashMap<UUID, Node> nodeMap = new ConcurrentHashMap<>();
    private ChannelManager channelManager;

    public NetworkManager(DigitalStorm digitalStorm) {
        this.digitalStorm = digitalStorm;
        initialize();

        clientSockets = new LinkedList<>();
    }

    private void initialize() {
        this.channelManager = new ChannelManager(this);
        clientSockets = new LinkedList<>();

        if (digitalStorm.getConfig().channels != null && digitalStorm.getConfig().channels.length != 0) {
            for (Channel channel : digitalStorm.getConfig().channels) {
                channelManager.joinChannel(channel, LocalMachine.getInstance());
            }
        }

        try {
            //serverSocket = new ServerSocket(this, digitalStorm.getConfig().localNodeNetworkAddress, digitalStorm.getConfig().SSL);
            Thread.sleep(3000);
            connectToNewNode(digitalStorm.getConfig().interfaceNodeNetworkAddress);
        } catch (Exception e) {
            e.printStackTrace();
            digitalStorm.getLogger().warning("Could not connect to network: " + e.getMessage());
        }
    }
	
	//Functions
	


	//Network
	
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

    public void nodeDisconnected(SocketAddress socketAddress) {
        if (nodeMap.contains(socketAddress)) {
            nodeMap.remove(socketAddress);
        }

        digitalStorm.getEventFactory().broadcastEvent(new RawClientDisconnectEvent(socketAddress));
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

    public void packetReceived(Packet msg, ServerHandler serverHandler) {
        digitalStorm.getEventFactory().broadcastPacket(msg, serverHandler);
    }

    //Getters

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

    public List<ClientSocket> getClientSockets() {
        return clientSockets;
    }

}
