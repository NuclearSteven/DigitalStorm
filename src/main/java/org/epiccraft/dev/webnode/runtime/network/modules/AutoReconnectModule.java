package org.epiccraft.dev.webnode.runtime.network.modules;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.epiccraft.dev.webnode.runtime.network.NetworkManager;
import org.epiccraft.dev.webnode.runtime.network.client.ClientSocket;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class AutoReconnectModule extends Module {

    private NetworkManager networkManger;

    @Override
    public void onEnabled(NetworkManager networkManager) {
        this.networkManger = networkManager;

        for (ClientSocket clientSocket : networkManager.getClientSockets()) {
            try {
                clientSocket.initConnection().addListener(connectionFeature -> {
                    if (!connectionFeature.isSuccess()) {
                        clientSocket.initConnection();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisabled(NetworkManager networkManager) {

    }

    public static class ReconnectListener  implements ChannelFutureListener {

        private NetworkManager networkManger;
        private AtomicBoolean disconnectRequested = new AtomicBoolean(false);
        private ScheduledExecutorService executorService;
        private AtomicInteger reconnectInterval;

        public ReconnectListener(NetworkManager networkManager) {
            this.networkManger = networkManager;
            this.reconnectInterval = new AtomicInteger(networkManager.getServer().getConfig().maxRetryTimes);
        }

        public void requestReconnect() {
            disconnectRequested.set(false);
        }

        public void requestDisconnect() {
            disconnectRequested.set(true);
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            networkManger.getServer().getLogger().log(Level.FINE, "Node connection lost(" + future.channel().remoteAddress() + "), trying to reconnect.");
            future.channel().disconnect();

        }

        public void scheduleReconnect() {
            if (!disconnectRequested.get()) {
                networkManger.getServer().getLogger().info("Will try again in " + reconnectInterval + " millis");
                executorService.schedule(
                        client::connectAsync,
                        reconnectInterval, TimeUnit.MILLISECONDS);
            }
        }

    }

}
