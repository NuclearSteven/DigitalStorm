package org.epiccraft.dev.digitalstorm.protocol;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class NodeInformation implements Serializable {

    public String type;
    public InetSocketAddress serverAddress;
    public UUID nodeUUID;
    public List<String> channels = new LinkedList<>();
    public int protocolVersion;
    public int customProtocolHashCode;

}
