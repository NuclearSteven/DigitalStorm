package org.epiccraft.dev.digitalstorm.protocol.channel;

import org.epiccraft.dev.digitalstorm.structure.channel.Channel;

/**
 * Project DigitalStorm
 */
public class ChannelDataPacket extends ChannelPacket {

    public Channel channel;
    public Object msg;

    @Override
    public String toString() {
        return "ChannelDataPacket{" +
                "channel=" + channel +
                ", msg=" + msg +
                '}';
    }
}
