package org.epiccraft.dev.digitalstorm.structure;

import org.epiccraft.dev.digitalstorm.protocol.NodeInformation;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.network.PacketHandler;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class Node implements Serializable {

    public UUID id;
    private InetSocketAddress serverAddress;
    private String type;
    private NetworkManager networkManager;
    private PacketHandler packetHandler;

    public Node(NetworkManager nodeNetworkManager, NodeInformation nodeInformation) {
        this.networkManager = nodeNetworkManager;
        this.id = nodeInformation.nodeUUID;
        this.type = nodeInformation.type;

        this.serverAddress = nodeInformation.serverAddress;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public Node bindHandler(PacketHandler handler) {
        this.packetHandler = handler;
        return this;
    }

    public void sendPacket(Packet packet) {
        packetHandler.getSocketChannel().write(packet);
        packetHandler.getSocketChannel().flush();
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", networkManager=" + networkManager +
                ", packetHandler=" + packetHandler +
                '}';
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public String getType() {
        return type;
    }

    public UUID getUUID() {
        return id;
    }
}
