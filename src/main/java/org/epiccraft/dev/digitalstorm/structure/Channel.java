package org.epiccraft.dev.digitalstorm.structure;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Project DigitalStorm
 */
public class Channel implements Serializable {

    private static List<Channel> channelList = new LinkedList<>();
    private static List<String> localChannelList = new LinkedList<>();

    private String channelID;
    private List<Node> joinedNodes = new LinkedList<>();

    public static Channel join(List<String> joinedChannels, Node node) {
        for (String joinedChannel : joinedChannels) {
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

    public static void leave(List<String> leavedChannels, Node node) {
        for (String leavedChannel : leavedChannels) {
            for (Channel channel : channelList) {
                if (channel.getChannelID().equals(leavedChannel)) {
                    channel.getJoinedNodes().remove(node);
                }
            }
        }
    }

    public static void joinLocal(String channelID) {
        localChannelList.add(channelID);
    }

    public static void leaveLocal(String channelID) {
        localChannelList.remove(channelID);
    }

    public static Channel getChannel(String channelID) {
        for (Channel channel : channelList) {
            if (channel.getChannelID().equals(channelID)) {
                return channel;
            }
        }
        return null;
    }

    public static List<String> getLocalChannelList() {
        return localChannelList;
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
