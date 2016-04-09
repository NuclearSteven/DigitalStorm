package org.epiccraft.dev.webnode.structure.channel;

import org.epiccraft.dev.webnode.runtime.network.NodeNetworkManager;

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

    public void createChannel(String name) {
        channels.add(new Channel(name));
    }

}
