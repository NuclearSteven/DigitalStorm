package org.epiccraft.dev.digitalstorm.network;

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
