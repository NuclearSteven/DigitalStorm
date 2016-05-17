package org.epiccraft.dev.digitalstorm.runtime.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.epiccraft.dev.digitalstorm.protocol.heartbeat.ACK;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;

import java.net.InetSocketAddress;

/**
 * Project DigitalStorm
 */
public class ClientSocket {

    private final InetSocketAddress address;
    private final SslContext sslCtx;
    private NetworkManager networkManager;
    private ChannelFuture channelFuture;
    private ClientHandler clientHandler;
    private NioEventLoopGroup group;

    public ClientSocket(final NetworkManager nodeNetworkManager, final InetSocketAddress address, boolean ssl) throws Exception {
        this.networkManager = nodeNetworkManager;

        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        this.address = address;
        this.sslCtx = sslCtx;

        initConnection();
    }


    public void initConnection() throws Exception {
        initConnection(newClientHandler(address));
    }

    public void initConnection(ClientHandler clientHandler) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), address.getHostString(), address.getPort()));
                        }
                        p.addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                clientHandler
                        );
                        ch.write(new ACK());
                    }
                });

        ChannelFuture channelFuture = b.connect(address);
        clientHandler.setSocketChannel((SocketChannel) channelFuture.channel());
        this.channelFuture = channelFuture;
        channelFuture.channel().write(new ACK());
        channelFuture.sync().channel().closeFuture().sync();
    }

    public void disconnect() {
        channelFuture.channel().disconnect();
        group.shutdownGracefully();
    }

    private ClientHandler newClientHandler(InetSocketAddress address) {
        ClientHandler clientHandler = new ClientHandler(networkManager, this);
        this.clientHandler = clientHandler;
        return clientHandler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public NioEventLoopGroup getGroup() {
        return group;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

}
