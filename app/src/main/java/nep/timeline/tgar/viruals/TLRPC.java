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
