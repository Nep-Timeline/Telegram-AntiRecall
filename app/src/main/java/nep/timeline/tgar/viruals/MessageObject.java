package nep.timeline.tgar.viruals;

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
        return (CharSequence) FieldUtils.getFieldClassOfClass(this.instance, messageTextField);
    }
}
