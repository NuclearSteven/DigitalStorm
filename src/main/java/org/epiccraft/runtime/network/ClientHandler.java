package org.epiccraft.runtime.network;

import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

/**
 * Project WebNode
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final List<Integer> firstMessage;
    private NodeNetworkManager nodeNetworkManager;

    public ClientHandler(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

}
