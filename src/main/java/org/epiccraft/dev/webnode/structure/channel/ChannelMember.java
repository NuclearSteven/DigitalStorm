package org.epiccraft.dev.webnode.structure.channel;

import org.epiccraft.dev.webnode.protocol.Packet;

import java.util.UUID;

/**
 * Project WebNode
 */
public interface ChannelMember {

    void sendPacket(Packet packet);

    UUID getId();

}
