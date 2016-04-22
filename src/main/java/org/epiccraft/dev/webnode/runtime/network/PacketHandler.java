package org.epiccraft.dev.webnode.runtime.network;

/**
 * Project WebNode
 */
public interface PacketHandler {

    enum NetworkStatus {
        ACTIVE, INACTIVE
    }

    NetworkStatus getNetworkStatus();

    io.netty.channel.socket.SocketChannel getSocketChannel();

    void shutdown(Exception e);

}
