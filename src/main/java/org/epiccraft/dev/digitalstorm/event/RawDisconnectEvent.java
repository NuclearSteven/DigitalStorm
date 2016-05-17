package org.epiccraft.dev.digitalstorm.event;

import java.net.SocketAddress;

/**
 * Project DigitalStorm
 */
public class RawDisconnectEvent extends Event {

    public SocketAddress getAddress() {
        return address;
    }

    private SocketAddress address;

    public RawDisconnectEvent(SocketAddress socketAddress) {
        this.address = socketAddress;
    }

}
