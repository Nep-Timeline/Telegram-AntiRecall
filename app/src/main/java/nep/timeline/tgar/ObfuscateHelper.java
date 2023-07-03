package nep.timeline.tgar;

import java.util.HashMap;
import java.util.Map;

public class ObfuscateHelper {
    // Nekogram
    private static final Map<String, String> nekogramClass = new HashMap<>();
    private static final Map<String, String> nekogramField = new HashMap<>();
    private static final Map<String, String> nekogramMethod = new HashMap<>();

    static {
        // Nekogram
        nekogramClass.put("org.telegram.messenger.MessageObject", "bc0");
        nekogramClass.put("org.telegram.messenger.UserConfig", "sm1");
        nekogramClass.put("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages", "Ja1");
        nekogramClass.put("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages", "Ia1");
        nekogramClass.put("org.telegram.ui.Cells.ChatMessageCell", "org.telegram.ui.Cells.r");
        nekogramField.put("selectedAccount", "o");
        nekogramMethod.put("updateMessageText", "y3");
        nekogramMethod.put("isChatNoForwards", "i1");
        nekogramMethod.put("canForwardMessage", "n");
        nekogramMethod.put("getChat", "j0");
        nekogramMethod.put("getInstance", "I0");
        nekogramMethod.put("measureTime", "W4");
    }

    public static String resolveNekogramClass(String classPathAndName)
    {
        if (!nekogramClass.containsKey(classPathAndName))
            return classPathAndName;

        return nekogramClass.get(classPathAndName);
    }

    public static String resolveNekogramField(String fieldName)
    {
        if (!nekogramField.containsKey(fieldName))
            return fieldName;

        return nekogramField.get(fieldName);
    }

    public static String resolveNekogramMethod(String methodName)
    {
        if (!nekogramMethod.containsKey(methodName))
            return methodName;

        return nekogramMethod.get(methodName);
    }
}
