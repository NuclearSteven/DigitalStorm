package org.epiccraft.dev.webnode.structure;

import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.runtime.network.NetworkManager;
import org.epiccraft.dev.webnode.runtime.network.PacketHandler;
import org.epiccraft.dev.webnode.structure.channel.Channel;

import java.io.Serializable;
import java.util.UUID;

/**
 * Project WebNode
 */
public class Node implements NodeUnit, Serializable {

    private NetworkManager networkManager;
    public UUID id;
    private PacketHandler handler;
    private Channel[] channels;

    public Node(NetworkManager nodeNetworkManager, UUID nid, Channel[] nodeGroup) {
        this.networkManager = nodeNetworkManager;
        this.id = nid;
        channels = nodeGroup;
        init();
    }

    private void init() {
        for (Channel channel : channels) {
            networkManager.getChannelManager().joinChannel(channel, this);
        }
    }

    public Node bindHandler(PacketHandler handler) {
        this.handler = handler;
        return this;
    }

    public void sendPacket(Packet packet) {
        handler.getSocketChannel().write(packet);
    }

    public UUID getId() {
        return id;
    }

    public PacketHandler getHandler() {
        return handler;
    }
}
