package org.epiccraft.dev.webnode.runtime.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.epiccraft.dev.webnode.protocol.info.reply.HandshakeReply;
import org.epiccraft.dev.webnode.protocol.info.request.HandshakeRequest;

/**
 * Project WebNode
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

	private SocketChannel socketChannel;
	private NodeNetworkManager networkManager;

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
		handshakeRequest.connectPassword = networkManager.getNode().getConfig().connectionPassword;
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
			}
		}
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

}
