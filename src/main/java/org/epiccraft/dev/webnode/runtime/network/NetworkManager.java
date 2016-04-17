package org.epiccraft.dev.webnode.runtime.network;

import org.epiccraft.dev.webnode.WebNode;
import org.epiccraft.dev.webnode.event.RawClientDisconnectEvent;
import org.epiccraft.dev.webnode.protocol.NodeInfo;
import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.protocol.channel.ChannelDataPacket;
import org.epiccraft.dev.webnode.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.webnode.runtime.network.client.ClientSocket;
import org.epiccraft.dev.webnode.runtime.network.handler.NetworkHandler;
import org.epiccraft.dev.webnode.runtime.network.server.ServerHandler;
import org.epiccraft.dev.webnode.runtime.network.server.ServerSocket;
import org.epiccraft.dev.webnode.structure.Node;
import org.epiccraft.dev.webnode.structure.channel.Channel;
import org.epiccraft.dev.webnode.structure.channel.ChannelManager;
import org.epiccraft.dev.webnode.structure.channel.LocalMachine;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project WebNode
 */
public class NetworkManager {

    private WebNode server;
    private ServerSocket serverSocket;
    private List<ClientSocket> clientSockets;
    private ConcurrentHashMap<UUID, Node> nodeMap = new ConcurrentHashMap<>();
    private ChannelManager channelManager;
    private List<NetworkHandler> networkHandlers;

    public NetworkManager(WebNode webNode) {
        server = webNode;
        initialize();

        clientSockets = new LinkedList<>();
        networkHandlers = new LinkedList<>();
    }

    private void initialize() {
        this.channelManager = new ChannelManager(this);
        clientSockets = new LinkedList<>();

        if (server.getConfig().channels != null && server.getConfig().channels.length != 0) {
            for (Channel channel : server.getConfig().channels) {
                channelManager.joinChannel(channel, LocalMachine.getInstance());
            }
        }

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

    public void nodeDisconnected(SocketAddress socketAddress) {
        if (nodeMap.contains(socketAddress)) {
            nodeMap.remove(socketAddress);
        }

        server.getEventFactory().broadcastEvent(new RawClientDisconnectEvent(socketAddress));
    }

    public void connectToNewNode(InetSocketAddress address) {
        connectToNewNode(address, server.getConfig().SSL);
    }

    public void connectToNewNode(InetSocketAddress address, boolean ssl) {
        try {
            clientSockets.add(new ClientSocket(this, address, ssl));
        } catch (Exception e) {
            e.printStackTrace();
            server.getLogger().info("Client socket failed to initialize: " + e.getLocalizedMessage());
        }
    }

    public void registerPacketHandler(NetworkHandler packetHandler) {
        networkHandlers.add(packetHandler);
    }

    public void packetReceived(Packet msg, ServerHandler serverHandler) {
        for (NetworkHandler networkHandler : networkHandlers) {
            for (Map.Entry<UUID, Node> uuidNodeEntry : nodeMap.entrySet()) {
                if (uuidNodeEntry.getValue().getHandler().equals(serverHandler)) {
                    msg.setSender(uuidNodeEntry.getValue());
                }
            }

            if (networkHandler.getInterests().includeInterest(msg.getClass())) {
                if (msg instanceof ChannelDataPacket) {
                    networkHandler.channelPacketReceived((ChannelDataPacket) msg);
                } else {
                    networkHandler.packetReceived(msg);
                }
            }
        }
    }

    //Getters

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

    public List<NetworkHandler> getNetworkHandlers() {
        return networkHandlers;
    }

    public List<ClientSocket> getClientSockets() {
        return clientSockets;
    }

}
