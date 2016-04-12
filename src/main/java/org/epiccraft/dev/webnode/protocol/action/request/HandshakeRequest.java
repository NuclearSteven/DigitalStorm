package org.epiccraft.dev.webnode.protocol.action.request;

import org.epiccraft.dev.webnode.protocol.action.Request;
import org.epiccraft.dev.webnode.protocol.NodeInfo;

/**
 * Project WebNode
 */
public class HandshakeRequest extends Request {

    public String connectPassword;
    public NodeInfo nodeInfo;

}
