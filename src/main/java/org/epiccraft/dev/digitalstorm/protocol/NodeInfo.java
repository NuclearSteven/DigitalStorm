package org.epiccraft.dev.digitalstorm.protocol;

import org.epiccraft.dev.digitalstorm.structure.channel.Channel;

import java.io.Serializable;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class NodeInfo implements Serializable {

    public UUID nodeUUID;
    public Channel[] channels;

}
