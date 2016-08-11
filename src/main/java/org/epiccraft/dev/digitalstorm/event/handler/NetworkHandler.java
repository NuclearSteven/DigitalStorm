package org.epiccraft.dev.digitalstorm.event.handler;

import org.epiccraft.dev.digitalstorm.event.Event;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.structure.Channel;

/**
 * Project DigitalStorm
 */
public abstract class NetworkHandler {

    public abstract void packetReceived(Packet packet);

    public abstract void channelPacketReceived(Packet packet, Channel channel);

    public abstract void exceptionCaught(Exception e);

    public abstract void onEvent(Event event);

    public abstract Interests getInterests();

}
