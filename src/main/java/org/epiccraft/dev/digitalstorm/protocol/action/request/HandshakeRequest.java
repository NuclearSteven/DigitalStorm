package org.epiccraft.dev.digitalstorm.protocol.action.request;

import org.epiccraft.dev.digitalstorm.protocol.action.Request;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfo;

/**
 * Project DigitalStorm
 */
public class HandshakeRequest extends Request {

    public String connectPassword;
    public NodeInfo nodeInfo;

}