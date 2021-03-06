package org.epiccraft.dev.digitalstorm.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
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
import org.epiccraft.dev.digitalstorm.network.NetworkManager;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project DigitalStorm
 */
public class ServerSocket extends Thread {

    private boolean ssl;
    private InetSocketAddress localAddr;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private NetworkManager networkManager;
    private ChannelFuture channelFuture;
    private ConcurrentHashMap<String, ServerHandler> serverHandlers = new ConcurrentHashMap<>();

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    @Override
    public void run() {
        SslContext sslCtx = null;
        try {
            if (ssl) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            SslContext finalSslCtx = sslCtx;
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (finalSslCtx != null) {
                                p.addLast(finalSslCtx.newHandler(ch.alloc()));
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

            channelFuture = b.bind(localAddr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServerSocket(final NetworkManager nodeNetworkManager, InetSocketAddress localAddr, boolean ssl) throws Exception {
        networkManager = nodeNetworkManager;
        this.localAddr = localAddr;
        this.ssl = ssl;
    }

    public void shutdown() {
        channelFuture.channel().close();
        channelFuture.channel().parent().close();
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    private ServerHandler newServerHandler(InetSocketAddress address, SocketChannel ch) {
        ServerHandler serverHandler = new ServerHandler(networkManager, ch);
        serverHandlers.put(networkManager.getNetworkID(address), serverHandler);
        return serverHandler;
    }

}
