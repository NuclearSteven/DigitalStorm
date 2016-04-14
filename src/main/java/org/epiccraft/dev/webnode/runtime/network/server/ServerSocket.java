package org.epiccraft.dev.webnode.runtime.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.epiccraft.dev.webnode.runtime.network.NetworkManager;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project WebNode
 */
public class ServerSocket {

    private NetworkManager networkManager;
    private ChannelFuture channelFuture;
    private ConcurrentHashMap<String, ServerHandler> serverHandlers = new ConcurrentHashMap<>();

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public ServerSocket(final NetworkManager nodeNetworkManager, InetSocketAddress localAddr, boolean ssl) throws Exception {
        networkManager = nodeNetworkManager;

        final SslContext sslCtx;
        if (ssl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            p.addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    newServerHandler(ch.remoteAddress(), ch)//// TODO: 4/5/2016 Check needed
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
            ;

            ChannelFuture channelFuture = b.bind(localAddr);
            channelFuture.sync().channel().closeFuture().sync();
            this.channelFuture = channelFuture;
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private ServerHandler newServerHandler(InetSocketAddress address, SocketChannel ch) {
        ServerHandler serverHandler = new ServerHandler(networkManager, ch);
        serverHandlers.put(networkManager.getNetworkID(address), serverHandler);
        return serverHandler;
    }

}
