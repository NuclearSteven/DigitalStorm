package org.epiccraft.dev.webnode.protocol.info.request;

import org.epiccraft.dev.webnode.protocol.info.Request;
import org.epiccraft.dev.webnode.structure.NodeUnit;

import java.util.List;

/**
 * Project WebNode
 */
public class HandshakeRequest extends Request {

    public String connectPassword;
    public List<NodeUnit> nodeUnits;

}
