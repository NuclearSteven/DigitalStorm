package org.epiccraft.dev.webnode.runtime.network.handler;

import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.protocol.data.ChannelPacket;

/**
 * Project WebNode
 */
public abstract class NetworkHandler {

    public abstract void packetReceived(Packet packet);

    public abstract void channelPacketReceived(ChannelPacket channelPacket);

    public abstract void exceptionCaught(Exception e);

    public abstract Interests getInterests();

}
