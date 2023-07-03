package nep.timeline.tgar.viruals;

import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.FieldUtils;

public class MessageObject {
    private final Object instance;
    private final XC_LoadPackage.LoadPackageParam lpparam;

    public MessageObject(Object instance, final XC_LoadPackage.LoadPackageParam lpparam)
    {
        this.instance = instance;
        this.lpparam = lpparam;
    }

    public CharSequence getMessageText()
    {
        String messageTextField = AutomationResolver.resolve("MessageObject", "messageText", AutomationResolver.ResolverType.Field, lpparam);
        return (CharSequence) FieldUtils.getFieldClassOfClass(this.instance, messageTextField);
    }
}
