package org.epiccraft.dev.digitalstorm.event;

import java.net.SocketAddress;

/**
 * Project DigitalStorm
 */
public class RawClientDisconnectEvent extends Event {

    public SocketAddress getAddress() {
        return address;
    }

    private SocketAddress address;

    public RawClientDisconnectEvent(SocketAddress socketAddress) {
        this.address = socketAddress;
    }

}
