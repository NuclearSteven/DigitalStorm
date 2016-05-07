package org.epiccraft.dev.digitalstorm.runtime.network.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfo;
import org.epiccraft.dev.digitalstorm.protocol.action.reply.HandshakeReply;
import org.epiccraft.dev.digitalstorm.protocol.action.request.HandshakeRequest;
import org.epiccraft.dev.digitalstorm.runtime.exception.NodeAlreadyConnectedException;
import org.epiccraft.dev.digitalstorm.runtime.exception.UnknownException;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.runtime.network.PacketHandler;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class ClientHandler extends ChannelInboundHandlerAdapter implements PacketHandler {

    private ClientSocket clientSocket;
    private SocketChannel socketChannel;
	private NetworkManager networkManager;
    private Node node;
	private NetworkStatus networkStatus;

    public ClientHandler(NetworkManager nodeNetworkManager, ClientSocket clientSocket, SocketChannel ch) {
        this.networkManager = nodeNetworkManager;
        this.clientSocket = clientSocket;
		this.socketChannel = ch;
    }

    @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		HandshakeRequest handshakeRequest = new HandshakeRequest();
		handshakeRequest.nodeInfo = new NodeInfo();
		handshakeRequest.nodeInfo.nodeUUID = UUID.randomUUID();
		handshakeRequest.nodeInfo.channels = networkManager.getChannelManager().getLocalChannels();
		handshakeRequest.connectPassword = networkManager.getServer().getConfig().connectionPassword;
		ctx.write(handshakeRequest);
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		handlePacket(ctx, msg);
	}

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();//// TODO: 4/24/2016 remove this
        ctx.close();
    }

    @Override
    public void shutdown(Exception e) {
        networkManager.getServer().getLogger().warning("Channel is shutting down due to " + e.getLocalizedMessage());
        clientSocket.disconnect();
    }

	private void handlePacket(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HandshakeReply) {
			if (!((HandshakeReply) msg).authSuccess) {
				networkManager.getServer().getLogger().warning("Node auth failed: " + ctx.channel().remoteAddress());
                ctx.close();
                socketChannel.close();
                return;
			} else {
				networkStatus = NetworkStatus.ACTIVE;
				networkManager.getServer().getLogger().info("Successfully started connection.");
			}

            Node node = null;
            try {
                node = networkManager.newNodeConnected(((HandshakeReply) msg).nodeInfo).bindHandler(this);
            } catch (NodeAlreadyConnectedException e) {
                networkManager.getServer().getLogger().warning("Node already connected: " + socketChannel.remoteAddress());
                ctx.close();
                socketChannel.close();
            }
            if (node == null) {
                shutdown(new UnknownException());
            }
            this.node = node;

			for (InetSocketAddress nodeUnit : ((HandshakeReply) msg).nodeUnits) {
				networkManager.getServer().getLogger().info("Connecting to " + nodeUnit.getAddress() + "...");
				networkManager.connectToNewNode(nodeUnit);
				networkManager.getServer().getLogger().info("Succeed!");
			}
		}

		ReferenceCountUtil.release(msg);
	}

	@Override
	public NetworkStatus getNetworkStatus() {
		return networkStatus;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

}
