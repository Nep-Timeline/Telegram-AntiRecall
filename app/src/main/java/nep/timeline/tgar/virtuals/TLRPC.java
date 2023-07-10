package nep.timeline.tgar.virtuals;

import java.util.ArrayList;

import nep.timeline.tgar.Utils;
import nep.timeline.tgar.obfuscate.AutomationResolver;
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

    public static class Message {
        private final Object instance;
        private final Class<?> clazz;

        public Message(Object instance)
        {
            this.instance = instance;
            if (!instance.getClass().getName().equals(AutomationResolver.resolve("org.telegram.tgnet.TLRPC$Message")))
            {
                Class<?> clazz = instance.getClass().getSuperclass();
                if (!clazz.getName().equals(AutomationResolver.resolve("org.telegram.tgnet.TLRPC$Message")))
                    this.clazz = clazz.getSuperclass();
                else
                    this.clazz = clazz;
            }
            else
            {
                this.clazz = instance.getClass();
            }
        }

        public int getID()
        {
            return FieldUtils.getFieldIntOfClass(this.instance, this.clazz, "id");
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
