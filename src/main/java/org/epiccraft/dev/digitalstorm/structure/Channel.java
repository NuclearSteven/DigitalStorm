package org.epiccraft.dev.digitalstorm.structure;

import org.epiccraft.dev.digitalstorm.protocol.system.channel.ChannelInfoPacket;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by steven on 16-6-1.
 */
public class Channel implements Serializable {

    private static List<Channel> channelList = new LinkedList<>();
    private static List<String> localChannelList = new LinkedList<>();

    private String channelID;
    private List<Node> joinedNodes = new LinkedList<>();

    public static Channel initialize(ChannelInfoPacket packet, Node node) {
        for (String joinedChannel : packet.joinedChannels) {
            Channel existsChannel = null;
            for (Channel channel : channelList) {
                if (channel.channelID.equals(joinedChannel)) {
                    existsChannel = channel;
                }
            }

            if (existsChannel == null) {
                existsChannel = new Channel(joinedChannel);
                channelList.add(existsChannel);
            }

            existsChannel.getJoinedNodes().add(node);
        }
        return null;
    }

    public static Channel getChannel(String channelID) {
        for (Channel channel : channelList) {
            if (channel.getChannelID().equals(channelID)) {
                return channel;
            }
        }
        return null;
    }

    public static void join(String channelID) {
        localChannelList.add(channelID);
    }

    public static void leave(String channelID) {
        for (int i = 0; i < localChannelList.size(); i++) {

        }
    }

    private Channel(String channelID) {
        this.channelID = channelID;
    }

    public List<Node> getJoinedNodes() {
        return joinedNodes;
    }

    public String getChannelID() {
        return channelID;
    }
}
