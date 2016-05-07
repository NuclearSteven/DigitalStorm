package org.epiccraft.dev.digitalstorm.structure.channel;

import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Project DigitalStorm
 */
public class ChannelManager {

    private NetworkManager networkManager;
    private List<Channel> channels;

    public ChannelManager(NetworkManager nodeNetworkManager) {
        networkManager = nodeNetworkManager;
        channels = new LinkedList<>();
        channels.add(new SystemNetworkingChannel());
    }

    public void joinChannel(String name, ChannelMember node) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                channel.join(node);
            }
        }
    }

    public void joinChannel(Channel channel, ChannelMember node) {
        for (Channel c : channels) {
            if (channel.getName().equals(c.getName())) {
                c.join(node);
                return;
            }
        }
        channels.add(channel);
        channel.join(node);
    }

    public Channel[] getChannels() {
        return (Channel[]) channels.toArray();
    }

    public SystemNetworkingChannel getSystemChannel() {
        return (SystemNetworkingChannel) channels.get(0);
    }

    public Channel[] getLocalChannels() {
        List<Channel> localChannels = new LinkedList<>();

        for (Channel channel : channels) {
            for (ChannelMember channelMember : channel.getJoinedNode()) {
                if (channelMember instanceof LocalMachine) {
                    localChannels.add(channel);
                }
            }
        }
        return toChannelArray(localChannels);
    }

    public Channel[] toChannelArray(List<Channel> channels) {
        Channel[] channelArray = new Channel[channels.size()];
        for (int i = 0; i < channels.size(); i++) {
            channelArray[i] = channels.get(i);
        }
        return channelArray;
    }

}
