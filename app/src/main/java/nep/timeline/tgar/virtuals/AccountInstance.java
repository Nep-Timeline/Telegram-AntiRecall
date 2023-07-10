package nep.timeline.tgar.virtuals;

import de.robv.android.xposed.XposedHelpers;
import nep.timeline.tgar.Utils;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.MethodUtils;

public class AccountInstance {
    private final Object instance;

    public AccountInstance(Object instance)
    {
        this.instance = instance;
    }

    public static AccountInstance getInstance() {
        Class<?> accountInstance = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.AccountInstance"), Utils.globalLoadPackageParam.classLoader);
        String getInstanceMethod = AutomationResolver.resolve("AccountInstance", "getInstance", AutomationResolver.ResolverType.Method);
        return new AccountInstance(MethodUtils.invokeMethodOfClass(accountInstance, getInstanceMethod, UserConfig.getSelectedAccount()));
    }

    public MessagesController getMessagesController() {
        String method = AutomationResolver.resolve("AccountInstance", "getMessagesController", AutomationResolver.ResolverType.Method);
        return new MessagesController(MethodUtils.invokeMethodOfClass(this.instance, method));
    }

    public MessagesStorage getMessagesStorage() {
        String method = AutomationResolver.resolve("AccountInstance", "getMessagesStorage", AutomationResolver.ResolverType.Method);
        return new MessagesStorage(MethodUtils.invokeMethodOfClass(this.instance, method));
    }
}
