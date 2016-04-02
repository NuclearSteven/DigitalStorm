package org.epiccraft.runtime.network;

import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Project WebNode
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final List<Integer> firstMessage;
    private NodeNetworkManager networkManager;

    public ClientHandler(NodeNetworkManager nodeNetworkManager) {
        this.networkManager = nodeNetworkManager;
        firstMessage = new ArrayList<Integer>();
        for (int i = 0; i < networkManager.getClientSocket().getSize(); i++) {
			firstMessage.add(i);
		}
    }

}
