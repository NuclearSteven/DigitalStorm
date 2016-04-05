package org.epiccraft.dev.webnode.runtime.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.webnode.protocol.info.reply.HandshakeReply;
import org.epiccraft.dev.webnode.protocol.info.request.HandshakeRequest;
import org.epiccraft.dev.webnode.structure.NodeUnit;

import java.util.LinkedList;
import java.util.List;

/**
 * Project WebNode
 */
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private NodeNetworkManager nodeNetworkManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        handlePacket(ctx, msg);
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
            } else {
                reply.authSuccess = false;
            }
            ctx.write(msg);
        }

        ctx.flush();
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

    public ServerHandler(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

}
