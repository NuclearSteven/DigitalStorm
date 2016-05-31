package org.epiccraft.dev.digitalstorm.runtime.network.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfo;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.action.reply.HandshakeReply;
import org.epiccraft.dev.digitalstorm.protocol.action.request.HandshakeRequest;
import org.epiccraft.dev.digitalstorm.runtime.exception.ConnectionException;
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

    public ClientHandler(NetworkManager nodeNetworkManager, ClientSocket clientSocket) {
        this.networkManager = nodeNetworkManager;
        this.clientSocket = clientSocket;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
        networkStatus = NetworkStatus.ACTIVE;
        HandshakeRequest handshakeRequest = new HandshakeRequest();
		handshakeRequest.nodeInfo = new NodeInfo();
		handshakeRequest.nodeInfo.nodeUUID = UUID.randomUUID();
        handshakeRequest.nodeInfo.protocolVersion = Packet.PROTOCOL_VERSION;
        handshakeRequest.nodeInfo.type = networkManager.getDigitalStorm().getConfig().type;
        handshakeRequest.connectPassword = networkManager.getDigitalStorm().getConfig().connectionPassword;
		ctx.write(handshakeRequest);
		ctx.flush();
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        networkStatus = NetworkStatus.INACTIVE;
        if (!networkManager.nodeDisconnected(getSocketChannel().remoteAddress()).doReconnect()) {
            clientSocket.getReconnectListsner().setReconnect(false);
        }
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.fireChannelInactive();
    }

	private void handlePacket(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HandshakeReply) {
			if (!((HandshakeReply) msg).authStatus) {
				networkManager.getDigitalStorm().getLogger().warning("Node authorize failed: " + ctx.channel().remoteAddress() + "because of" + ((HandshakeReply) msg).failureReason);
                ctx.close();
                return;
			} else {
				networkStatus = NetworkStatus.ACTIVE;
				networkManager.getDigitalStorm().getLogger().info("Successfully started connection.");
			}

            Node node = null;
            try {
                node = networkManager.newNodeConnected(((HandshakeReply) msg).nodeInfo, socketChannel.remoteAddress()).bindHandler(this);
            } catch (ConnectionException e) {
                networkManager.getDigitalStorm().getLogger().warning(e.toString());
                ctx.close();
                socketChannel.close();
            }
            if (node == null) {
                shutdown(new UnknownException());
            }
            this.node = node;

			for (InetSocketAddress nodeUnit : ((HandshakeReply) msg).nodeUnits) {
				networkManager.getDigitalStorm().getLogger().info("Connecting to " + nodeUnit.getAddress() + "...");
				networkManager.connectToNewNode(nodeUnit);
				networkManager.getDigitalStorm().getLogger().info("Succeed!");
			}
		} else {
            networkManager.packetReceived((Packet) msg, this);
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

    @Override
    public void shutdown(Exception e) {
        networkManager.getDigitalStorm().getLogger().warning("Channel is shutting down due to " + e.getLocalizedMessage());
        clientSocket.disconnect();
    }

}
