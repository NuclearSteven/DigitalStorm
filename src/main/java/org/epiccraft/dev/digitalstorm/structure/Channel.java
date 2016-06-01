package org.epiccraft.dev.digitalstorm.structure;

import org.epiccraft.dev.digitalstorm.protocol.system.channel.ChannelInfoPacket;

import java.io.Serializable;

/**
 * Created by steven on 16-6-1.
 */
public class Channel implements Serializable {

    private String channelID;

    private Channel(String channelID) {
        this.channelID = channelID;
    }

    public Channel initialize(ChannelInfoPacket packet, Node node) {
        return null;
    }

}
