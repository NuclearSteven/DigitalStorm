package org.epiccraft.dev.webnode.runtime.network.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.webnode.protocol.info.reply.HandshakeReply;
import org.epiccraft.dev.webnode.protocol.info.request.HandshakeRequest;
import org.epiccraft.dev.webnode.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.webnode.runtime.exception.UnknownException;
import org.epiccraft.dev.webnode.runtime.network.NodeNetworkManager;
import org.epiccraft.dev.webnode.runtime.network.PacketHandler;
import org.epiccraft.dev.webnode.structure.Node;

import java.util.UUID;

/**
 * Project WebNode
 */
public class ClientHandler extends ChannelInboundHandlerAdapter implements PacketHandler {

	private SocketChannel socketChannel;
	private NodeNetworkManager networkManager;
    private Node node;

    public ClientHandler(NodeNetworkManager nodeNetworkManager, SocketChannel ch) {
        this.networkManager = nodeNetworkManager;
		this.socketChannel = ch;
    }

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

    @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		HandshakeRequest handshakeRequest = new HandshakeRequest();
		handshakeRequest.nodeInfo.nodeUUID = UUID.randomUUID();
		handshakeRequest.connectPassword = networkManager.getNode().getConfig().connectionPassword;
		handshakeRequest.nodeInfo.nodeGroup = networkManager.getNode().getConfig().nodeGroup;
		ctx.write(handshakeRequest);
		ctx.flush();

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		handlePacket(ctx, msg);
	}

	private void handlePacket(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HandshakeReply) {
			if (!((HandshakeReply) msg).authSuccess) {
				networkManager.getNode().getLogger().warning("Node auth failed: " + ctx.channel().remoteAddress());
                ctx.close();
                socketChannel.close();
                return;
			}

            Node node = null;
            try {
                node = networkManager.newNodeConnected(((HandshakeReply) msg).nodeInfo).bindHandler(this);
            } catch (NodeAlreadyConnectedException e) {
                networkManager.getNode().getLogger().warning("Node already connected: " + socketChannel.remoteAddress());
                ctx.close();
                socketChannel.close();
            }
            if (node == null) {
                shutdown(new UnknownException());
            }
            this.node = node;
        }

		ReferenceCountUtil.release(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
	{
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		cause.printStackTrace();
		ctx.close();
	}

    @Override
    public void shutdown(Exception e) {
        networkManager.getNode().getLogger().warning("Channel is shutting down due to " + e.getLocalizedMessage());
        socketChannel.close();
    }

}
