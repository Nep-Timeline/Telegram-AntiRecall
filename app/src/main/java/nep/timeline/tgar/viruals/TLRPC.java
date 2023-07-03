package nep.timeline.tgar.viruals;

import java.util.ArrayList;

import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.Utils;
import nep.timeline.tgar.utils.FieldUtils;

public class TLRPC {
    public static class Chat { // Nekogram is eL0
        private final Object instance;
        private final XC_LoadPackage.LoadPackageParam lpparam;

        public Chat(Object instance, final XC_LoadPackage.LoadPackageParam lpparam)
        {
            this.instance = instance;
            this.lpparam = lpparam;
        }

        public long getId()
        {
            return FieldUtils.getFieldLongOfClass(this.instance, "id");
        }
    }

    public static class TL_updateDeleteChannelMessages {
        private final Object instance;
        private final XC_LoadPackage.LoadPackageParam lpparam;

        public TL_updateDeleteChannelMessages(Object instance, final XC_LoadPackage.LoadPackageParam lpparam)
        {
            this.instance = instance;
            this.lpparam = lpparam;
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
        private final XC_LoadPackage.LoadPackageParam lpparam;

        public TL_updateDeleteMessages(Object instance, final XC_LoadPackage.LoadPackageParam lpparam)
        {
            this.instance = instance;
            this.lpparam = lpparam;
        }

        public ArrayList<Integer> getMessages()
        {
            return Utils.castList(FieldUtils.getFieldClassOfClass(this.instance, "messages"), Integer.class);
        }
    }
}
