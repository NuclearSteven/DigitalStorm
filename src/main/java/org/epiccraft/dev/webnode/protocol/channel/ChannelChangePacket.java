package org.epiccraft.dev.webnode.protocol.channel;

import org.epiccraft.dev.webnode.structure.channel.Channel;

import java.util.LinkedList;
import java.util.List;

/**
 * Project WebNode
 */
public class ChannelChangePacket extends ChannelPacket {

    public List<Action> list = new LinkedList<>();

    public static class Action {

        public enum ActionType {
            JOIN, QUIT
        }

        public ActionType actionType;
        public Channel targetChannel;

    }

}
