package org.epiccraft.dev.digitalstorm.network;

import org.epiccraft.dev.digitalstorm.DigitalStorm;
import org.epiccraft.dev.digitalstorm.network.client.ClientSocket;
import org.epiccraft.dev.digitalstorm.network.server.ServerSocket;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.ChannelDataPacket;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.JoinChannelPacket;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.LeaveChannelPacket;
import org.epiccraft.dev.digitalstorm.structure.Channel;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.net.InetSocketAddress;
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
    private ProtocolManager protocolManager;

    public NetworkManager(DigitalStorm digitalStorm) {
        this.digitalStorm = digitalStorm;
        clientSockets = new LinkedList<>();
        uuid = UUID.randomUUID();
        clientSockets = new LinkedList<>();
        this.protocolManager = new ProtocolManager(this);
    }

    @Override
    public void run() {
        initialize();
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    private void initialize() {
        protocolManager.lock();

        try {
            if (digitalStorm.getConfig().serverSideTraffic)
                serverSocket = new ServerSocket(this, digitalStorm.getConfig().localNodeNetworkAddress, digitalStorm.getConfig().SSL);
            serverSocket.start();
            if (digitalStorm.getConfig().clientSideTraffic)
                connectToNewNode(digitalStorm.getConfig().interfaceNodeNetworkAddress, true);
        } catch (Exception e) {
            e.printStackTrace();
            digitalStorm.getLogger().warning("Could not connect to network: " + e.getMessage());
        }
    }

    public void broadcast(Packet packet) {
        for (Map.Entry<UUID, Node> uuidNodeEntry : getNodeMap().entrySet()) {
            uuidNodeEntry.getValue().sendPacket(packet);
        }
    }

    public void broadcast(Packet packet, Channel channel) {
        for (Map.Entry<UUID, Node> uuidNodeEntry : getNodeMap().entrySet()) {
            Node node = uuidNodeEntry.getValue();
            if (channel.getJoinedNodes().contains(node)) {
                ChannelDataPacket dataPacket = new ChannelDataPacket();
                dataPacket.msg = packet;
                dataPacket.channelID = channel.getChannelID();
                node.sendPacket(dataPacket);
            }
        }
    }

    public void joinChannel(String channel) {
        Channel.joinLocal(channel);
        JoinChannelPacket packet = new JoinChannelPacket();
        packet.channelIDs.add(channel);
        broadcast(packet);
    }

    public void leaveChannel(String channel) {
        if (Channel.getChannel(channel) == null) {
            return;
        }
        LeaveChannelPacket packet = new LeaveChannelPacket();
        packet.channelIDs.add(channel);
        broadcast(packet);
    }

    public void connectToNewNode(InetSocketAddress address, boolean redirect) {
        getDigitalStorm().getLogger().info("Connecting to " + address + "...");
        connectToNewNode(address, digitalStorm.getConfig().SSL, redirect);
    }
	
    public void connectToNewNode(InetSocketAddress address, boolean ssl, boolean redirect) {
        try {
            clientSockets.add(new ClientSocket(this, address, ssl, redirect));
        } catch (Exception e) {
            e.printStackTrace();
            digitalStorm.getLogger().info("Client socket failed to initialize: " + e.getLocalizedMessage());
        }
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
