package nep.timeline.tgar.viruals;

import de.robv.android.xposed.callbacks.XC_LoadPackage;
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
}
