package org.epiccraft.dev.webnode.runtime.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.epiccraft.dev.webnode.runtime.network.NetworkManager;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project WebNode
 */
public class ClientSocket {

    private NetworkManager networkManager;
    private ChannelFuture channelFuture;
    private ConcurrentHashMap<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();

    public ClientSocket(final NetworkManager nodeNetworkManager, final InetSocketAddress address, boolean ssl) throws Exception {
        this.networkManager = nodeNetworkManager;

        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
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
                                    newClientHandler(address, ch)//// TODO: 4/5/2016 Check needed
                            );
                        }
                    });

            ChannelFuture channelFuture = b.connect(address);
            channelFuture.sync().channel().closeFuture().sync();
            this.channelFuture = channelFuture;
        } finally {
            group.shutdownGracefully();
        }
    }

    private ClientHandler newClientHandler(InetSocketAddress address, SocketChannel ch) {
        ClientHandler clientHandler = new ClientHandler(networkManager, ch);
        clientHandlers.put(networkManager.getNetworkID(address), clientHandler);
        return clientHandler;
    }

}
