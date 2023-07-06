package nep.timeline.tgar.viruals;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
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
        private final Class<?> clazz;

        public Message(Object instance)
        {
            this.instance = instance;
            Class<?> clazz = instance.getClass().getSuperclass();
            if (!clazz.getName().equals("YL0"))
                this.clazz = clazz.getSuperclass();
            else
                this.clazz = clazz;
        }

        public int getID()
        {
            try
            {
                Field field = this.clazz.getDeclaredField("id");

                if (!field.isAccessible())
                    field.setAccessible(true);

                return field.getInt(this.instance);
            }
            catch (Exception e)
            {
                XposedBridge.log(e);
                e.printStackTrace();
                return Integer.MIN_VALUE;
            }
        }

        public Peer getPeerID()
        {
            try
            {
                Field field = this.clazz.getDeclaredField("peer_id");

                if (!field.isAccessible())
                    field.setAccessible(true);

                return new Peer(field.get(this.instance));
            }
            catch (Exception e)
            {
                XposedBridge.log(e);
                e.printStackTrace();
                return null;
            }
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
