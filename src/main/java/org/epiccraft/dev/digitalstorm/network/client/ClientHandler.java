package org.epiccraft.dev.digitalstorm.network.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.epiccraft.dev.digitalstorm.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.network.PacketHandler;
import org.epiccraft.dev.digitalstorm.protocol.NodeInformation;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.system.action.reply.HandshakeReply;
import org.epiccraft.dev.digitalstorm.protocol.system.action.request.HandshakeRequest;
import org.epiccraft.dev.digitalstorm.runtime.exception.ConnectionException;
import org.epiccraft.dev.digitalstorm.runtime.exception.UnknownException;
import org.epiccraft.dev.digitalstorm.structure.Channel;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class ClientHandler extends ChannelInboundHandlerAdapter implements PacketHandler {

    private final boolean redirect;
    private ClientSocket clientSocket;
    private SocketChannel socketChannel;
	private NetworkManager networkManager;
    private Node node;
	private NetworkStatus networkStatus;

    public ClientHandler(NetworkManager nodeNetworkManager, boolean redirect, ClientSocket clientSocket) {
        this.networkManager = nodeNetworkManager;
        this.redirect = redirect;
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
		handshakeRequest.nodeInformation = new NodeInformation();
        handshakeRequest.nodeInformation.serverAddress = networkManager.getDigitalStorm().getConfig().localNodeNetworkAddress;
        handshakeRequest.nodeInformation.type = networkManager.getDigitalStorm().getConfig().type;
		handshakeRequest.nodeInformation.nodeUUID = UUID.randomUUID();
        handshakeRequest.nodeInformation.protocolVersion = Packet.PROTOCOL_VERSION;
        handshakeRequest.nodeInformation.customProtocolHashCode = networkManager.getProtocolManager().getCustomPacketHashCode();
        handshakeRequest.nodeInformation.channels = Channel.getLocalChannelList();
        handshakeRequest.connectPassword = networkManager.getDigitalStorm().getConfig().connectionPassword;
		ctx.write(handshakeRequest);
		ctx.flush();
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        networkStatus = NetworkStatus.INACTIVE;
        if (!ConnectionAdaptor.nodeDisconnected(networkManager, getSocketChannel().remoteAddress()).doReconnect()) {
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
                ((HandshakeReply) msg).nodeInformation.serverAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                node = ConnectionAdaptor.newNodeConnected(networkManager, ((HandshakeReply) msg).nodeInformation, socketChannel.remoteAddress()).bindHandler(this);
                Channel.join(((HandshakeReply) msg).nodeInformation.channels, node);
            } catch (ConnectionException e) {
                e.printStackTrace();
                networkManager.getDigitalStorm().getLogger().warning(e.toString());
                ctx.close();
                socketChannel.close();
            }
            if (node == null) {
                shutdown(new UnknownException());
            }
            this.node = node;

            if (redirect) {
                for (InetSocketAddress nodeUnit : ((HandshakeReply) msg).nodeUnits) {
                    System.out.println("Node unit" + nodeUnit);
                    networkManager.connectToNewNode(nodeUnit, false);
                    networkManager.getDigitalStorm().getLogger().info("Connection succeeded!");
                }
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
