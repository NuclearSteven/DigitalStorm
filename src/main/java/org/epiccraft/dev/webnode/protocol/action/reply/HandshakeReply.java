package org.epiccraft.dev.webnode.protocol.action.reply;

import org.epiccraft.dev.webnode.protocol.action.Reply;
import org.epiccraft.dev.webnode.protocol.NodeInfo;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Project WebNode
 */
public class HandshakeReply extends Reply {

    public boolean authSuccess;
    public List<InetSocketAddress> nodeUnits;
    public NodeInfo nodeInfo; //remote node action

}
