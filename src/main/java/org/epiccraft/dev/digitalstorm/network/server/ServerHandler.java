package org.epiccraft.dev.digitalstorm.network.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.digitalstorm.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.network.PacketHandler;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfomation;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.system.action.reply.HandshakeReply;
import org.epiccraft.dev.digitalstorm.protocol.system.action.request.HandshakeRequest;
import org.epiccraft.dev.digitalstorm.runtime.exception.ConnectionException;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.io.InvalidClassException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Project DigitalStorm
 */
public class ServerHandler extends ChannelInboundHandlerAdapter implements PacketHandler {

    private SocketChannel socketChannel;
    private NetworkManager networkManager;
    private NetworkStatus networkStatus;
    private long lastActive;
    private Node node;

    public ServerHandler(NetworkManager networkManager, SocketChannel ch) {
        this.networkManager = networkManager;
        this.socketChannel = ch;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        networkStatus = NetworkStatus.ACTIVE;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        networkStatus = NetworkStatus.INACTIVE;

        //Start reconnect process
        networkManager.getDigitalStorm().getLogger().warning("Lost connection from " + node + ", waiting response...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        lastActive = System.currentTimeMillis();

        handlePacket(ctx, msg);
    }

    private void handlePacket(ChannelHandlerContext ctx, Object msg) {
        lastActive = System.currentTimeMillis();
        if (msg instanceof HandshakeRequest) {
            HandshakeReply reply = new HandshakeReply();
            if (((HandshakeRequest) msg).nodeInfomation.protocolVersion != Packet.PROTOCOL_VERSION) {
                reply.authStatus = false;
                reply.failureReason = HandshakeReply.FailureReason.INCOMPATIBLE_PROTOCOL_VERSION;
            } else if (((HandshakeRequest) msg).nodeInfomation.customProtocolHashCode != networkManager.getProtocolManager().getCustomPacketHashCode()) {
                reply.authStatus = false;
                reply.failureReason = HandshakeReply.FailureReason.INCOMPATIBLE_PROTOCOL_LIB_VERSION;
            } else if (!((HandshakeRequest) msg).connectPassword.equals(networkManager.getDigitalStorm().getConfig().connectionPassword)) {
                reply.authStatus = false;
                reply.failureReason = HandshakeReply.FailureReason.AUTHORIZE_FAILED;
            } else {
                reply.authStatus = true;
                List<InetSocketAddress> list = new LinkedList<>();
                networkManager.getNodeMap().forEach((aLong, node) -> {
                    if (!node.getPacketHandler().getSocketChannel().remoteAddress().equals(socketChannel.remoteAddress())) {
                        list.add(node.getPacketHandler().getSocketChannel().remoteAddress());//// TODO: 4/14/2016 fix this
                    }
                });
                reply.nodeUnits = list;
                reply.nodeInfomation = new NodeInfomation();
                reply.nodeInfomation.nodeUUID = networkManager.getUuid();
                reply.nodeInfomation.protocolVersion = Packet.PROTOCOL_VERSION;
                reply.nodeInfomation.type = networkManager.getDigitalStorm().getConfig().type;
            }
            ctx.write(reply);

            try {
                node = ConnectionAdaptor.newNodeConnected(networkManager, ((HandshakeRequest) msg).nodeInfomation, socketChannel.remoteAddress()).bindHandler(this);
            } catch (ConnectionException e) {
                networkManager.getDigitalStorm().getLogger().warning(e.toString());
                ctx.close();
            }
        } else {
            if (node == null || !(msg instanceof Packet)) {
                //unauthorized
                return;
            }
            ConnectionAdaptor.handleUniversal(networkManager, (Packet) msg, ctx, node);
        }

        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvalidClassException) {
            networkManager.getDigitalStorm().getLogger().warning("Incompatible protocol version: " + socketChannel.remoteAddress());
        }
        networkManager.getDigitalStorm().getLogger().warning("Connection error caught: " + cause.getLocalizedMessage());
        cause.printStackTrace();
        ctx.close();
        ConnectionAdaptor.nodeDisconnected(networkManager, ctx.channel().remoteAddress());
    }

    @Override
    public NetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    @Override
    public void shutdown(Exception e) {
        networkManager.getDigitalStorm().getLogger().warning("Channel is shutting down due to " + e.getLocalizedMessage());
    }

}
