package org.epiccraft.runtime.network;

import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import io.netty.channel.*;

/**
 * Project WebNode
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final List<Integer> firstMessage;
    private NodeNetworkManager networkManager;

    public ClientHandler(NodeNetworkManager nodeNetworkManager) {
        this.networkManager = nodeNetworkManager;
        firstMessage = new ArrayList<Integer>();
        for (int i = 0; i < networkManager.getClientSocket().getSize(); i++) {
			firstMessage.add(i);
		}
    }

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		ctx.writeAndFlush(firstMessage);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		// TODO: Implement this method
		super.channelRead(ctx, msg);
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
