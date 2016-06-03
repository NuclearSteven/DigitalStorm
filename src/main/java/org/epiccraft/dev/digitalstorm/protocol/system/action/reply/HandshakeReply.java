package org.epiccraft.dev.digitalstorm.protocol.system.action.reply;

import org.epiccraft.dev.digitalstorm.protocol.system.action.Reply;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfomation;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Project DigitalStorm
 */
public class HandshakeReply extends Reply {

    public boolean authStatus;
    public FailureReason failureReason;
    public List<InetSocketAddress> nodeUnits;
    public NodeInfomation nodeInfomation; //remote node action

    public enum FailureReason {
        INCOMPATIBLE_PROTOCOL_VERSION, INCOMPATIBLE_PROTOCOL_LIB_VERSION, AUTHORIZE_FAILED
    }

}
