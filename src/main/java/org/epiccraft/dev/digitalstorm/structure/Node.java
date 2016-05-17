package org.epiccraft.dev.digitalstorm.structure;

import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.runtime.network.PacketHandler;

import java.io.Serializable;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class Node implements NodeUnit, Serializable {

    private NetworkManager networkManager;
    public UUID id;
    private PacketHandler handler;

    public Node(NetworkManager nodeNetworkManager, UUID nid) {
        this.networkManager = nodeNetworkManager;
        this.id = nid;
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
}
