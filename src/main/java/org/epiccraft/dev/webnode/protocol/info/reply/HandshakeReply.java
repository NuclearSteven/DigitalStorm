package org.epiccraft.dev.webnode.protocol.info.reply;

import org.epiccraft.dev.webnode.protocol.info.Reply;
import org.epiccraft.dev.webnode.structure.NodeUnit;

import java.util.List;

/**
 * Project WebNode
 */
public class HandshakeReply extends Reply {

    public boolean authSuccess;
    public List<NodeUnit> nodeUnits;

}
