package org.epiccraft.dev.digitalstorm.structure.channel;

import org.epiccraft.dev.digitalstorm.protocol.Packet;

import java.util.UUID;

/**
 * Project DigitalStorm
 */
public interface ChannelMember {

    void sendPacket(Packet packet);

    UUID getId();

}
