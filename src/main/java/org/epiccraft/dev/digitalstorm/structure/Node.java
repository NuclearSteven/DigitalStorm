package org.epiccraft.dev.digitalstorm.structure;

import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.runtime.network.PacketHandler;
import org.epiccraft.dev.digitalstorm.structure.channel.Channel;
import org.epiccraft.dev.digitalstorm.structure.channel.ChannelMember;

import java.io.Serializable;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class Node implements NodeUnit, ChannelMember, Serializable {

    private NetworkManager networkManager;
    public UUID id;
    private PacketHandler handler;
    private Channel[] channels;

    public Node(NetworkManager nodeNetworkManager, UUID nid, Channel[] channels) {
        this.networkManager = nodeNetworkManager;
        this.id = nid;
        this.channels = channels;
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
        handler.getSocketChannel().flush();
    }

    public UUID getId() {
        return id;
    }

    public PacketHandler getHandler() {
        return handler;
    }
}
