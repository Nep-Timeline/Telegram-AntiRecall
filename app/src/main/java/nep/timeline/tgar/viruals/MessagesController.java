package nep.timeline.tgar.viruals;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.ClientChecker;
import nep.timeline.tgar.ObfuscateHelper;

public class MessagesController {
    private final Object instance;
    private final XC_LoadPackage.LoadPackageParam lpparam;

    public MessagesController(Object instance, final XC_LoadPackage.LoadPackageParam lpparam)
    {
        this.instance = instance;
        this.lpparam = lpparam;
    }

    public Object getChat(long chatId, final XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        String getInstanceMethodName = "getChat";
        if (ClientChecker.isNekogram(lpparam))
            getInstanceMethodName = ObfuscateHelper.resolveNekogramMethod(getInstanceMethodName);
        return this.instance.getClass().getDeclaredMethod(getInstanceMethodName).invoke(this.instance, chatId);
    }

    public static MessagesController getInstance(final XC_LoadPackage.LoadPackageParam lpparam) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        String messagesControllerPath = "org.telegram.messenger.MessagesController";
        Class<?> messagesController = XposedHelpers.findClassIfExists(messagesControllerPath, lpparam.classLoader);
        String getInstanceMethodName = "getInstance";
        if (ClientChecker.isNekogram(lpparam))
            getInstanceMethodName = ObfuscateHelper.resolveNekogramMethod(getInstanceMethodName);
        return new MessagesController(messagesController.getDeclaredMethod(getInstanceMethodName).invoke(messagesController, UserConfig.getSelectedAccount(lpparam)), lpparam);
    }
}
