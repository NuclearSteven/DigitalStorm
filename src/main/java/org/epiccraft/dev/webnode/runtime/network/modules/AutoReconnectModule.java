package org.epiccraft.dev.webnode.runtime.network.modules;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.epiccraft.dev.webnode.runtime.network.NetworkManager;
import org.epiccraft.dev.webnode.runtime.network.client.ClientSocket;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class AutoReconnectModule extends Module {

    private NetworkManager networkManger;
    private List<ReconnectListener> reconnectListeners;

    @Override
    public void onEnabled(NetworkManager networkManager) {
        this.networkManger = networkManager;
        reconnectListeners = new LinkedList<>();

        for (ClientSocket clientSocket : networkManager.getClientSockets()) {
            if (getReconnectListener(clientSocket) != null) {
                continue;
            }
            try {
                clientSocket.getChannelFuture().addListener(connectionFeature -> {
                    if (!connectionFeature.isSuccess()) {
                        ReconnectListener reconnectListener = new ReconnectListener(networkManager, clientSocket);
                        reconnectListeners.add(reconnectListener);
                        clientSocket.initConnection().addListener(reconnectListener);
                    }
                });
            } catch (Exception e) {
                networkManager.getServer().getLogger().warning("Exception caused starting the client socket: " + e.getLocalizedMessage());
            }
        }
    }

    public ReconnectListener getReconnectListener(ClientSocket clientSocket) {
        for (ReconnectListener reconnectListener : reconnectListeners) {
            if (reconnectListener.clientSocket == clientSocket) {
                return reconnectListener;
            }
        }
        return null;
    }

    @Override
    public void onDisabled(NetworkManager networkManager) {

    }

    public static class ReconnectListener  implements ChannelFutureListener {

        private ClientSocket clientSocket;
        private NetworkManager networkManger;
        private AtomicBoolean disconnectRequested = new AtomicBoolean(false);
        private ScheduledExecutorService executorService;
        private int reconnectInterval;

        public ReconnectListener(NetworkManager networkManager, ClientSocket clientSocket) {
            this.networkManger = networkManager;
            this.clientSocket = clientSocket;
            executorService = clientSocket.getGroup();
            this.reconnectInterval = networkManager.getServer().getConfig().maxRetryTimes;
        }

        public void requestReconnect() {
            disconnectRequested.set(false);
        }

        public void requestDisconnect() {
            disconnectRequested.set(true);
        }

        public ClientSocket getClientSocket() {
            return clientSocket;
        }

        public NetworkManager getNetworkManger() {
            return networkManger;
        }

        public AtomicBoolean getDisconnectRequested() {
            return disconnectRequested;
        }

        public ScheduledExecutorService getExecutorService() {
            return executorService;
        }

        public int getReconnectInterval() {
            return reconnectInterval;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            networkManger.getServer().getLogger().log(Level.FINE, "Node connection lost(" + future.channel().remoteAddress() + "), trying to reconnect.");
            future.channel().disconnect();
            scheduleReconnect();
        }

        public void scheduleReconnect() {
            if (!disconnectRequested.get()) {
                networkManger.getServer().getLogger().info("Will try again in " + reconnectInterval + " millis");
                executorService.schedule(
                        clientSocket::initConnection,
                        reconnectInterval, TimeUnit.MILLISECONDS);
            }
        }

    }

}
