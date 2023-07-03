package nep.timeline.tgar.viruals;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.ClientChecker;
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

    public TLRPC.Chat getChat(long chatId, final XC_LoadPackage.LoadPackageParam lpparam) {
        return new TLRPC.Chat(MethodUtils.invokeMethodOfClass(this.instance, AutomationResolver.resolve("MessagesController", "getChat", AutomationResolver.ResolverType.Method, lpparam), chatId), lpparam);
    }

    public static MessagesController getInstance(final XC_LoadPackage.LoadPackageParam lpparam) {
        return new MessagesController(MethodUtils.invokeMethodOfClass(XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.MessagesController", lpparam), lpparam.classLoader), AutomationResolver.resolve("MessagesController", "getInstance", AutomationResolver.ResolverType.Method, lpparam), UserConfig.getSelectedAccount(lpparam)), lpparam);
    }
}
