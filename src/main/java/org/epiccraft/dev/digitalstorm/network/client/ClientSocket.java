package org.epiccraft.dev.digitalstorm.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
import org.epiccraft.dev.digitalstorm.network.NetworkManager;

import java.net.InetSocketAddress;

/**
 * Project DigitalStorm
 */
public class ClientSocket {

    private InetSocketAddress address;
    private SslContext sslCtx;
    private boolean redirect;
    private ReconnectListener reconnectListsner;
    private NetworkManager networkManager;
    private ChannelFuture channelFuture;
    private ClientHandler clientHandler;
    private NioEventLoopGroup group;

    public ClientSocket(final NetworkManager nodeNetworkManager, final InetSocketAddress address, boolean ssl, boolean redirect) throws Exception {
        this.networkManager = nodeNetworkManager;
        this.redirect = redirect;

        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        this.address = address;
        this.sslCtx = sslCtx;

        group = new NioEventLoopGroup();
        this.reconnectListsner = new ReconnectListener();

        initConnection();
    }


    public void initConnection() throws Exception {
        initConnection(newClientHandler(address, redirect));
    }

    public NioEventLoopGroup getGroup() {
        return group;
    }

    public void initConnection(ClientHandler clientHandler) throws InterruptedException {
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
                    }
                });

        ChannelFuture channelFuture = b.connect(address);
        clientHandler.setSocketChannel((SocketChannel) channelFuture.channel());
        if (networkManager.getDigitalStorm().getConfig().autoRetry) {
            channelFuture.addListener(connFuture -> {
                if (!channelFuture.isSuccess()) {
                    reconnectListsner.operationComplete(channelFuture);
                }
                channelFuture.channel().closeFuture().addListener(reconnectListsner);
            });
        }
    }

    public void shutdown() {
        reconnectListsner.setReconnect(false);
        try {
            channelFuture.channel().close();
            channelFuture.channel().closeFuture().sync();
            group.shutdownGracefully();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ReconnectListener getReconnectListsner() {
        return reconnectListsner;
    }

    public class ReconnectListener implements ChannelFutureListener {

        private boolean reconnect = true;

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!reconnect) {
                return;
            }
            future.channel().disconnect();
            initConnection();
        }

        public void setReconnect(boolean reconnect) {
            this.reconnect = reconnect;
        }
    }

    public void disconnect() {
        channelFuture.channel().disconnect();
        group.shutdownGracefully();
    }

    private ClientHandler newClientHandler(InetSocketAddress address, boolean redirect) {
        ClientHandler clientHandler = new ClientHandler(networkManager, redirect, this);
        this.clientHandler = clientHandler;
        return clientHandler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

}
