package org.epiccraft.dev.webnode.runtime.network.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.webnode.protocol.action.reply.HandshakeReply;
import org.epiccraft.dev.webnode.protocol.action.request.HandshakeRequest;
import org.epiccraft.dev.webnode.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.webnode.runtime.network.NodeNetworkManager;
import org.epiccraft.dev.webnode.runtime.network.PacketHandler;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Project WebNode
 */
public class ServerHandler extends ChannelInboundHandlerAdapter implements PacketHandler {

    private SocketChannel socketChannel;
    private NodeNetworkManager networkManager;

    public ServerHandler(NodeNetworkManager networkManager, SocketChannel ch) {
        this.networkManager = networkManager;
        this.socketChannel = ch;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        handlePacket(ctx, msg);
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    private void handlePacket(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HandshakeRequest) {
            HandshakeReply reply = new HandshakeReply();
            if (((HandshakeRequest) msg).connectPassword.equals(networkManager.getServer().getConfig().connectionPassword)) {
                reply.authSuccess = true;
                List<InetSocketAddress> list = new LinkedList<>();
                networkManager.getNodeMap().forEach((aLong, node) -> {
                    if (!node.getHandler().getSocketChannel().remoteAddress().equals(socketChannel.remoteAddress())) {
                        list.add(node.getHandler().getSocketChannel().remoteAddress());
                    }
                });
                reply.nodeUnits = list;
            } else {
                reply.authSuccess = false;
            }
            ctx.write(msg);
            
            try {
                networkManager.newNodeConnected(((HandshakeRequest) msg).nodeInfo).bindHandler(this);
            } catch (NodeAlreadyConnectedException e) {
                networkManager.getServer().getLogger().warning("Node already connected: " + socketChannel.remoteAddress());
                ctx.close();
            }
        }

        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        networkManager.getServer().getLogger().warning("Connection error caught: " + cause.getLocalizedMessage());
        ctx.close();
    }

    @Override
    public void shutdown(Exception e) {
        networkManager.getServer().getLogger().warning("Channel is shutting down due to " + e.getLocalizedMessage());
    }

}
