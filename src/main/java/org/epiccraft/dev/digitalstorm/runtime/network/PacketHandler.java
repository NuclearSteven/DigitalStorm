package org.epiccraft.dev.digitalstorm.runtime.network;

import org.epiccraft.dev.digitalstorm.structure.Node;

/**
 * Project DigitalStorm
 */
public interface PacketHandler {

    enum NetworkStatus {
        ACTIVE, INACTIVE
    }

    NetworkStatus getNetworkStatus();

    io.netty.channel.socket.SocketChannel getSocketChannel();

    void shutdown(Exception e);

}
