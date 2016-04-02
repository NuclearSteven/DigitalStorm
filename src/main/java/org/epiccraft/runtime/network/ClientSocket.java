package org.epiccraft.runtime.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;

/**
 * Project WebNode
 */
public class ClientSocket {

    private ClientHandler clientHandler;
    private NodeNetworkManager networkManager;
	private int size;

    public ClientSocket(NodeNetworkManager nodeNetworkManager, InetSocketAddress address, boolean ssl) throws Exception {
        this(nodeNetworkManager, address, ssl, 256);
    }

    public ClientSocket(final NodeNetworkManager nodeNetworkManager, final InetSocketAddress address, boolean ssl, int size) throws Exception {
        this.networkManager = nodeNetworkManager;
        this.clientHandler = new ClientHandler(nodeNetworkManager);
		this.size = size;

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
                    .channel(NioServerSocketChannel.class)
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

            b.connect(address).sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }
	
	public int getSize() {
		return size;
	}

}
