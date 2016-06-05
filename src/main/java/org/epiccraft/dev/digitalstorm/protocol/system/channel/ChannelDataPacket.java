package org.epiccraft.dev.digitalstorm.protocol.system.channel;

import java.io.Serializable;

/**
 * Project DigitalStorm
 */
public class ChannelDataPacket extends ChannelPacket {

    public String channelID;
    public Serializable msg;

    @Override
    public String toString() {
        return "ChannelDataPacket{" +
                "channelID='" + channelID + '\'' +
                ", msg=" + msg +
                '}';
    }
}
