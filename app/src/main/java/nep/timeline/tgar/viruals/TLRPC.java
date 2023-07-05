package nep.timeline.tgar.viruals;

import java.util.ArrayList;

import nep.timeline.tgar.Utils;
import nep.timeline.tgar.utils.FieldUtils;

public class TLRPC {
    public static class Chat { // Nekogram is eL0
        private final Object instance;

        public Chat(Object instance)
        {
            this.instance = instance;
        }

        public long getId()
        {
            return FieldUtils.getFieldLongOfClass(this.instance, "id");
        }

        public String getTitle()
        {
            return (String) FieldUtils.getFieldClassOfClass(this.instance, "title");
        }
    }

    public static class Peer {
        private final Object instance;

        public Peer(Object instance)
        {
            this.instance = instance;
        }

        public long getUserID()
        {
            return FieldUtils.getFieldLongOfClass(this.instance, "user_id");
        }

        public long getChatID()
        {
            return FieldUtils.getFieldLongOfClass(this.instance, "chat_id");
        }

        public long getChannelID()
        {
            return FieldUtils.getFieldLongOfClass(this.instance, "channel_id");
        }
    }

    public static class Message {
        private final Object instance;

        public Message(Object instance)
        {
            this.instance = instance;
        }

        public int getID()
        {
            return FieldUtils.getFieldIntOfClass(this.instance, "id");
        }

        public Peer getPeerID()
        {
            return new Peer(FieldUtils.getFieldClassOfClass(this.instance, "peer_id"));
        }
    }

    public static class TL_updateDeleteChannelMessages {
        private final Object instance;

        public TL_updateDeleteChannelMessages(Object instance)
        {
            this.instance = instance;
        }

        public long getChannelID()
        {
            return FieldUtils.getFieldLongOfClass(this.instance, "channel_id");
        }

        public ArrayList<Integer> getMessages()
        {
            return Utils.castList(FieldUtils.getFieldClassOfClass(this.instance, "messages"), Integer.class);
        }
    }

    public static class TL_updateDeleteMessages {
        private final Object instance;

        public TL_updateDeleteMessages(Object instance)
        {
            this.instance = instance;
        }

        public ArrayList<Integer> getMessages()
        {
            return Utils.castList(FieldUtils.getFieldClassOfClass(this.instance, "messages"), Integer.class);
        }
    }
}
