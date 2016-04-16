package org.epiccraft.dev.webnode.event;

import java.net.SocketAddress;

/**
 * Project WebNode
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
