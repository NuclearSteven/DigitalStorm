package org.epiccraft.dev.digitalstorm.protocol.system.action.request;

import org.epiccraft.dev.digitalstorm.protocol.system.action.Request;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfomation;

/**
 * Project DigitalStorm
 */
public class HandshakeRequest extends Request {

    public String connectPassword;
    public NodeInfomation nodeInfomation;

}
