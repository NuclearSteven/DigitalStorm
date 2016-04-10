package org.epiccraft.dev.webnode.protocol;

import org.epiccraft.dev.webnode.structure.channel.Channel;

import java.io.Serializable;
import java.util.UUID;

/**
 * Project WebNode
 */
public class NodeInfo implements Serializable {

    public UUID nodeUUID;
    public Channel[] channels;

}
