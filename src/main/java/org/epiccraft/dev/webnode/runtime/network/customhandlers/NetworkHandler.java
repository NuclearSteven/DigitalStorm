package org.epiccraft.dev.webnode.runtime.network.customhandlers;

import org.epiccraft.dev.webnode.event.Event;
import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.protocol.channel.ChannelDataPacket;

/**
 * Project WebNode
 */
public abstract class NetworkHandler {

    public abstract void packetReceived(Packet packet);

    public abstract void channelPacketReceived(ChannelDataPacket channelDataPacket);

    public abstract void exceptionCaught(Exception e);

    public abstract void onEvent(Event event);

    public abstract Interests getInterests();

}
