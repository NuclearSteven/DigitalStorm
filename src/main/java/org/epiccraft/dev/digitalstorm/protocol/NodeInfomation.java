package org.epiccraft.dev.digitalstorm.protocol;

import java.io.Serializable;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class NodeInfomation implements Serializable {

    public String type;
    public UUID nodeUUID;
    public int protocolVersion;
    public int customProtocolHashCode;

}
