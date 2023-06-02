package nep.timeline.tgar;

import java.util.HashMap;
import java.util.Map;

public class ObfuscateHelper {
    // Nekogram
    private static final Map<String, String> nekogramClass = new HashMap<>();
    private static final Map<String, String> nekogramMethod = new HashMap<>();

    static {
        // Nekogram
        nekogramClass.put("org.telegram.messenger.MessageObject", "bc0");
        nekogramClass.put("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages", "Ga1");
        nekogramClass.put("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages", "Fa1");
        nekogramMethod.put("isChatNoForwards", "i1");
        nekogramMethod.put("canForwardMessage", "n");
    }

    public static String resolveNekogramClass(String classPathAndName)
    {
        if (!nekogramClass.containsKey(classPathAndName))
            return classPathAndName;

        return nekogramClass.get(classPathAndName);
    }

    public static String resolveNekogramMethod(String methodName)
    {
        if (!nekogramMethod.containsKey(methodName))
            return methodName;

        return nekogramMethod.get(methodName);
    }
}
