package org.epiccraft.dev.webnode.protocol.info.request;

import org.epiccraft.dev.webnode.protocol.info.Request;
import org.epiccraft.dev.webnode.protocol.NodeInfo;

/**
 * Project WebNode
 */
public class HandshakeRequest extends Request {

    public String connectPassword;
    public NodeInfo nodeInfo;

}
