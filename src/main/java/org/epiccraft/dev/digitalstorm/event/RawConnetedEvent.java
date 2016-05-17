package org.epiccraft.dev.digitalstorm.event;

import java.net.SocketAddress;

/**
 * Project DigitalStorm
 */
public class RawConnetedEvent extends Event {

    public SocketAddress getAddress() {
        return address;
    }

    private SocketAddress address;

    public RawConnetedEvent(SocketAddress socketAddress) {
        this.address = socketAddress;
    }

}
