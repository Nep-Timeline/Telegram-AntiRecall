package nep.timeline.tgar.viruals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import nep.timeline.tgar.TMoe.Initiator;
import nep.timeline.tgar.Utils;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.FieldUtils;

public class MessageObject {
    private final Object instance;

    public MessageObject(Object instance)
    {
        this.instance = instance;
    }

    public CharSequence getMessageText()
    {
        String messageTextField = AutomationResolver.resolve("MessageObject", "messageText", AutomationResolver.ResolverType.Field);
        Object messageTextFieldUnchecked = FieldUtils.getFieldClassOfClass(this.instance, messageTextField);
        if (messageTextFieldUnchecked instanceof CharSequence)
            return (CharSequence) messageTextFieldUnchecked;

        return null;
    }

    public TLRPC.Message getMessageOwner()
    {
        List<Field> fields = new ArrayList<>();
        for (Field declaredField : this.instance.getClass().getDeclaredFields())
            if (declaredField.getName().equals(AutomationResolver.resolve("MessageObject", "messageOwner", AutomationResolver.ResolverType.Field)))
                fields.add(declaredField);

        if (!fields.isEmpty()) {
            try
            {
                Field messageOwnerField = null;
                //Class<?> TL_updateDeleteMessages = Initiator.loadClass(AutomationResolver.resolve("org.telegram.tgnet.TLRPC$Message"));
                for (Field field : fields) {
                    if (field.getType().getName().equals("YL0"))
                    {
                        messageOwnerField = field;
                    }
                }
                if (messageOwnerField != null)
                    return new TLRPC.Message(messageOwnerField.get(this.instance));
                else
                    XposedBridge.log("[TGAR Error] Not found messageOwner field in MessageObject's fields, " + Utils.issue);
            }
            catch (IllegalAccessException  e)
            {
                e.printStackTrace();
            }
        }
        else
            XposedBridge.log("[TGAR Error] Not found messageOwner field in MessageObject, " + Utils.issue);

        return null;
    }
}
