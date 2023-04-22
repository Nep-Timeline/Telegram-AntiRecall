package nep.timeline.tgar;

import android.content.res.XModuleResources;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookInit implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    private static final List<String> hookPackages = Arrays.asList("org.telegram.messenger", "org.telegram.messenger.web", "org.telegram.messenger.beta", "org.telegram.plus",
            "xyz.nextalone.nagram",
            "xyz.nextalone.nnngram",
            "nekox.messenger",
            "tw.nekomimi.nekogram",
            "com.cool2645.nekolite",
            "com.exteragram.messenger",
            "org.forkgram.messenger",
            "org.forkclient.messenger.beta");
    private static String MODULE_PATH = null;

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!hookPackages.contains(resparam.packageName))
            return;

        XModuleResources.createInstance(MODULE_PATH, resparam.res);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (hookPackages.contains(lpparam.packageName)) {
            XposedBridge.log("[TGAR] Trying to hook app: " + lpparam.packageName);

            Class<?> messagesController = XposedHelpers.findClass("org.telegram.messenger.MessagesController", lpparam.classLoader);

            Method[] messagesControllerMethods = messagesController.getDeclaredMethods();

            List<String> methodNames = new ArrayList<>();

            for (Method method : messagesControllerMethods)
                if (method.getParameterCount() == 5 && method.getParameterTypes()[0] == ArrayList.class && method.getParameterTypes()[1] == ArrayList.class && method.getParameterTypes()[2] == ArrayList.class && method.getParameterTypes()[3] == boolean.class && method.getParameterTypes()[4] == int.class)
                    methodNames.add(method.getName());

            if (methodNames.size() != 1)
                XposedBridge.log("[TGAR] Failed to hook anti-recall! reason: " + (methodNames.isEmpty() ? "No method found" : "Multiple methods found") + ", your telegram may have been modified!");
            else
            {
                String methodName = methodNames.get(0);
                XposedBridge.log("[TGAR] Trying to hook " + methodName);
                // Anti Recall
                XposedHelpers.findAndHookMethod(messagesController, methodName, ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Class<?> TL_updateDeleteChannelMessages;
                        Class<?> TL_updateDeleteMessages;
                        if (lpparam.packageName.equals("tw.nekomimi.nekogram"))
                        {
                            TL_updateDeleteChannelMessages = lpparam.classLoader.loadClass("x81");
                            TL_updateDeleteMessages = lpparam.classLoader.loadClass("y81");
                        }
                        else
                        {
                            TL_updateDeleteChannelMessages = lpparam.classLoader.loadClass("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages");
                            TL_updateDeleteMessages = lpparam.classLoader.loadClass("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages");
                        }
                        ArrayList<Object> updates = castList(param.args[0], Object.class);
                        if (updates != null && !updates.isEmpty())
                        {
                            ArrayList<Object> newUpdates = new ArrayList<>();

                            for (Object item : updates)
                                if (!item.getClass().equals(TL_updateDeleteChannelMessages) && !item.getClass().equals(TL_updateDeleteMessages))
                                    newUpdates.add(item);
                                else
                                    XposedBridge.log("[TGAR] Protected message! event: " + item.getClass());

                            param.args[0] = newUpdates;
                        }
                    }
                });
            }

            // Fake Premium
            // XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", lpparam.classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));
        }
    }

    public static <T> ArrayList<T> castList(Object obj, Class<T> clazz)
    {
        ArrayList<T> result = new ArrayList<>();
        if(obj instanceof ArrayList<?>)
        {
            for (Object o : (ArrayList<?>) obj)
                result.add(clazz.cast(o));
            return result;
        }
        return null;
    }
}
