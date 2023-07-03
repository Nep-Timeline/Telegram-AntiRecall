package nep.timeline.tgar.viruals;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.MethodUtils;

public class MessagesController {
    private final Object instance;
    private final XC_LoadPackage.LoadPackageParam lpparam;

    public MessagesController(Object instance, final XC_LoadPackage.LoadPackageParam lpparam)
    {
        this.instance = instance;
        this.lpparam = lpparam;
    }

    public TLRPC.Chat getChat(long chatId) {
        String getChatMethod = AutomationResolver.resolve("MessagesController", "getChat", AutomationResolver.ResolverType.Method, lpparam);
        return new TLRPC.Chat(MethodUtils.invokeMethodOfClass(this.instance, getChatMethod, chatId), lpparam);
    }

    public static MessagesController getInstance(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> messagesController = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.MessagesController", lpparam), lpparam.classLoader);
        String getInstanceMethod = AutomationResolver.resolve("MessagesController", "getInstance", AutomationResolver.ResolverType.Method, lpparam);
        return new MessagesController(MethodUtils.invokeMethodOfClass(messagesController, getInstanceMethod, UserConfig.getSelectedAccount(lpparam)), lpparam);
    }
}
