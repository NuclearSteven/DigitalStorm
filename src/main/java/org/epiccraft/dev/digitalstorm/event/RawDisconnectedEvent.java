package org.epiccraft.dev.digitalstorm.event;

import java.net.SocketAddress;

/**
 * Project DigitalStorm
 */
public class RawDisconnectedEvent extends Event {

    private boolean reconnect = false;

    public SocketAddress getAddress() {
        return address;
    }

    private SocketAddress address;

    public boolean doReconnect() {
        return reconnect;
    }

    public RawDisconnectedEvent(SocketAddress socketAddress) {
        this.address = socketAddress;

    }

    public void reconnect() {
        this.reconnect = true;
    }

}
