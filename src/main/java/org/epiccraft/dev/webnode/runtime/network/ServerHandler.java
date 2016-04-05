package org.epiccraft.dev.webnode.runtime.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.webnode.protocol.info.reply.HandshakeReply;
import org.epiccraft.dev.webnode.protocol.info.request.HandshakeRequest;
import org.epiccraft.dev.webnode.structure.NodeUnit;

import java.util.LinkedList;
import java.util.List;

/**
 * Project WebNode
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private SocketChannel socketChannel;
    private NodeNetworkManager nodeNetworkManager;

    public ServerHandler(NodeNetworkManager nodeNetworkManager, SocketChannel ch) {
        this.nodeNetworkManager = nodeNetworkManager;
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
            if (((HandshakeRequest) msg).connectPassword == nodeNetworkManager.getNode().getConfig().connectionPassword) {
                reply.authSuccess = true;
                List<NodeUnit> list = new LinkedList<>();
                nodeNetworkManager.getNodeMap().forEach((aLong, node) -> {
                    list.add(node);
                });
                reply.nodeUnits = list;
            } else {
                reply.authSuccess = false;
            }
            ctx.write(msg);
            nodeNetworkManager.newNode(this, ((HandshakeRequest) msg).nodeID);
        }

        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeNetworkManager.getNode().getLogger().warning("Connection error caught: " + cause.getLocalizedMessage());
        ctx.close();
    }

}
