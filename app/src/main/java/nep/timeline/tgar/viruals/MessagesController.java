package nep.timeline.tgar.viruals;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.ClientChecker;
import nep.timeline.tgar.ObfuscateHelper;
import nep.timeline.tgar.utils.MethodUtils;

public class MessagesController {
    private final Object instance;
    private final XC_LoadPackage.LoadPackageParam lpparam;

    public MessagesController(Object instance, final XC_LoadPackage.LoadPackageParam lpparam)
    {
        this.instance = instance;
        this.lpparam = lpparam;
    }

    public TLRPC.Chat getChat(long chatId, final XC_LoadPackage.LoadPackageParam lpparam) {
        String getInstanceMethodName = "getChat";
        if (ClientChecker.isNekogram(lpparam))
            getInstanceMethodName = ObfuscateHelper.resolveNekogramMethod(getInstanceMethodName);
        return new TLRPC.Chat(MethodUtils.invokeMethodOfClass(this.instance, getInstanceMethodName, chatId), lpparam);
    }

    public static MessagesController getInstance(final XC_LoadPackage.LoadPackageParam lpparam) {
        String messagesControllerPath = "org.telegram.messenger.MessagesController";
        Class<?> messagesController = XposedHelpers.findClassIfExists(messagesControllerPath, lpparam.classLoader);
        String getInstanceMethodName = "getInstance";
        if (ClientChecker.isNekogram(lpparam))
            getInstanceMethodName = ObfuscateHelper.resolveNekogramMethod(getInstanceMethodName);
        return new MessagesController(MethodUtils.invokeMethodOfClass(messagesController, getInstanceMethodName, UserConfig.getSelectedAccount(lpparam)), lpparam);
    }
}
