package org.epiccraft.dev.digitalstorm.structure;

import org.epiccraft.dev.digitalstorm.protocol.NodeInfo;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.runtime.network.PacketHandler;

import java.io.Serializable;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class Node implements Serializable {

    public UUID id;
    private String type;
    private NetworkManager networkManager;
    private PacketHandler handler;
    public Node(NetworkManager nodeNetworkManager, NodeInfo nodeInfo) {
        this.networkManager = nodeNetworkManager;
        this.id = nodeInfo.nodeUUID;
        this.type = nodeInfo.type;
    }

    public String getType() {
        return type;
    }

    public Node bindHandler(PacketHandler handler) {
        this.handler = handler;
        return this;
    }

    public void sendPacket(Packet packet) {
        handler.getSocketChannel().write(packet);
        handler.getSocketChannel().flush();
    }

    public UUID getUUID() {
        return id;
    }

    public PacketHandler getHandler() {
        return handler;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", networkManager=" + networkManager +
                ", handler=" + handler +
                '}';
    }
}
