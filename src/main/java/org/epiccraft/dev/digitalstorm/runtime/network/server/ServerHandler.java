package org.epiccraft.dev.digitalstorm.runtime.network.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfo;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.action.reply.HandshakeReply;
import org.epiccraft.dev.digitalstorm.protocol.action.request.HandshakeRequest;
import org.epiccraft.dev.digitalstorm.protocol.heartbeat.ACK;
import org.epiccraft.dev.digitalstorm.protocol.heartbeat.NACK;
import org.epiccraft.dev.digitalstorm.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.runtime.network.PacketHandler;

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
		// TODO: 5/15/2016
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        handlePacket(ctx, msg);
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    private void handlePacket(ChannelHandlerContext ctx, Object msg) {
        lastActive = System.currentTimeMillis();
        if (msg instanceof HandshakeRequest) {
            HandshakeReply reply = new HandshakeReply();
            if (((HandshakeRequest) msg).connectPassword.equals(networkManager.getDigitalStorm().getConfig().connectionPassword)) {
                reply.authSuccess = true;
                List<InetSocketAddress> list = new LinkedList<>();
                networkManager.getNodeMap().forEach((aLong, node) -> {
                    if (!node.getHandler().getSocketChannel().remoteAddress().equals(socketChannel.remoteAddress())) {
                        list.add(node.getHandler().getSocketChannel().remoteAddress());//// TODO: 4/14/2016 fix this 
                    }
                });
                reply.nodeUnits = list;
                reply.nodeInfo = new NodeInfo();
                reply.nodeInfo.nodeUUID = networkManager.getUuid();
            } else {
                reply.authSuccess = false;
            }
            ctx.write(reply);

            try {
                networkManager.newNodeConnected(((HandshakeRequest) msg).nodeInfo).bindHandler(this);
            } catch (NodeAlreadyConnectedException e) {
                networkManager.getDigitalStorm().getLogger().warning("Node already connected: " + socketChannel.remoteAddress());
                ctx.close();
            }
        } else {
            if (msg instanceof ACK) {
                ctx.write(new NACK());
                return;
            }
            if (msg instanceof Packet) {
                networkManager.packetReceived((Packet) msg, this);
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
        networkManager.getDigitalStorm().getLogger().info("Node Disconnected: " + cause.getLocalizedMessage());

        cause.printStackTrace();
        networkManager.getDigitalStorm().getLogger().warning("Connection error caught: " + cause.getLocalizedMessage());
        ctx.close();

        networkManager.nodeDisconnected(ctx.channel().remoteAddress());
    }

    @Override
    public void shutdown(Exception e) {
        networkManager.getDigitalStorm().getLogger().warning("Channel is shutting down due to " + e.getLocalizedMessage());
    }

    @Override
    public NetworkStatus getNetworkStatus() {
        return networkStatus;
    }

}
