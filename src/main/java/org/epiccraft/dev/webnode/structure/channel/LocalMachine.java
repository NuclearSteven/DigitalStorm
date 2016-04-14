package org.epiccraft.dev.webnode.structure.channel;

import org.epiccraft.dev.webnode.protocol.Packet;

import java.util.UUID;

/**
 * Project WebNode
 */
public class LocalMachine implements ChannelMember {

    private static LocalMachine instance;
    private UUID uuid;

    private LocalMachine() {
        uuid = UUID.randomUUID();
        instance = this;
    }

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public UUID getId() {
        return uuid;
    }

    public static LocalMachine getInstance() {
        if (instance == null) {
            instance = new LocalMachine();
        }
        return instance;
    }

}
