package org.epiccraft.dev.webnode.runtime.network;

/**
 * Project WebNode
 */
public interface PacketHandler {

    io.netty.channel.socket.SocketChannel getSocketChannel();

    void shutdown(Exception e);

}
