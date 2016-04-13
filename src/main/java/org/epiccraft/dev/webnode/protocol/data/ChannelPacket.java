package org.epiccraft.dev.webnode.protocol.data;

import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.structure.channel.Channel;

/**
 * Project WebNode
 */
public class ChannelPacket extends Packet {

    public Channel channel;
    public Object msg;

}
