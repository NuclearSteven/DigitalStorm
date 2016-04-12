package org.epiccraft.dev.webnode.structure.channel;

import org.epiccraft.dev.webnode.runtime.network.NodeNetworkManager;
import org.epiccraft.dev.webnode.structure.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Project WebNode
 */
public class ChannelManager {

    private NodeNetworkManager networkManager;
    private List<Channel> channels;

    public ChannelManager(NodeNetworkManager nodeNetworkManager) {
        networkManager = nodeNetworkManager;
        channels = new LinkedList<>();
    }

    public void joinChannel(String name, Node node) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                channel.join(node);
            }
        }
    }

    public void joinChannel(Channel channel, Node node) {
        for (Channel c : channels) {
            if (channel.getName().equals(c.getName())) {
                c.join(node);
                return;
            }
        }
        channels.add(channel);
        channel.join(node);
    }

    public Channel[] getChannels() {
        return (Channel[]) channels.toArray();
    }
}
