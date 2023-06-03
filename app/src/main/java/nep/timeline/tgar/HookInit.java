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
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookInit implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    private static final List<String> hookPackages = Arrays.asList("org.telegram.messenger", "org.telegram.messenger.web", "org.telegram.messenger.beta", "org.telegram.plus",
            "tw.nekomimi.nekogram",
            "com.cool2645.nekolite",
            "com.exteragram.messenger",
            "org.forkclient.messenger",
            "org.forkclient.messenger.beta",
            "uz.unnarsx.cherrygram");
    private static final List<String> hookPackagesCustomization = Arrays.asList("xyz.nextalone.nagram", "xyz.nextalone.nnngram",
            "nekox.messenger");
    private static String MODULE_PATH = null;
    private static final String issue = "Your telegram may have been modified! You can submit issue to let developer to try support to the telegram client you are using.";
    private static final boolean DEBUG_MODE = true;
    private static final boolean ONLY_ANTIRECALL = false;

    public final List<String> getHookPackages()
    {
        List<String> hookPackagesLocal = new ArrayList<>(hookPackages);
        List<String> hookPackagesCustomizationLocal = new ArrayList<>(hookPackagesCustomization);
        hookPackagesLocal.addAll(hookPackagesCustomizationLocal);
        return hookPackagesLocal;
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!getHookPackages().contains(resparam.packageName))
            return;

        XModuleResources.createInstance(MODULE_PATH, resparam.res);
    }

    private boolean onlyNeedAR(final XC_LoadPackage.LoadPackageParam lppara)
    {
        return hookPackagesCustomization.contains(lppara.packageName);
    }

    private boolean isNekogram(final XC_LoadPackage.LoadPackageParam lpparam)
    {
        return lpparam.packageName.equals("tw.nekomimi.nekogram");
    }

    private boolean isCherrygram(final XC_LoadPackage.LoadPackageParam lpparam)
    {
        return lpparam.packageName.equals("uz.unnarsx.cherrygram");
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (getHookPackages().contains(lpparam.packageName)) {
            if (DEBUG_MODE)
                XposedBridge.log("[TGAR] Trying to hook app: " + lpparam.packageName);

            String messagesControllerPath = "org.telegram.messenger.MessagesController";

            Class<?> messagesController = XposedHelpers.findClassIfExists(messagesControllerPath, lpparam.classLoader);

            if (messagesController != null)
            {
                Method[] messagesControllerMethods = messagesController.getDeclaredMethods();

                // Anti Recall
                {
                    List<String> methodNames = new ArrayList<>();

                    for (Method method : messagesControllerMethods)
                        if (method.getParameterCount() == 5 && method.getParameterTypes()[0] == ArrayList.class && method.getParameterTypes()[1] == ArrayList.class && method.getParameterTypes()[2] == ArrayList.class && method.getParameterTypes()[3] == boolean.class && method.getParameterTypes()[4] == int.class)
                            methodNames.add(method.getName());

                    if (methodNames.size() != 1)
                        XposedBridge.log("[TGAR Error] Failed to hook processUpdateArray! Reason: " + (methodNames.isEmpty() ? "No method found" : "Multiple methods found") + ", " + issue);
                    else
                    {
                        String methodName = methodNames.get(0);

                        XposedHelpers.findAndHookMethod(messagesController, methodName, ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String TL_updateDeleteMessagesPath = "org.telegram.tgnet.TLRPC$TL_updateDeleteMessages";
                                String TL_updateDeleteChannelMessagesPath = "org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages";
                                if (isNekogram(lpparam))
                                {
                                    TL_updateDeleteMessagesPath = ObfuscateHelper.resolveNekogramClass(TL_updateDeleteMessagesPath);
                                    TL_updateDeleteChannelMessagesPath = ObfuscateHelper.resolveNekogramClass(TL_updateDeleteChannelMessagesPath);
                                }
                                Class<?> TL_updateDeleteMessages = lpparam.classLoader.loadClass(TL_updateDeleteMessagesPath);
                                Class<?> TL_updateDeleteChannelMessages = lpparam.classLoader.loadClass(TL_updateDeleteChannelMessagesPath);
                                ArrayList<Object> updates = castList(param.args[0], Object.class);
                                if (updates != null && !updates.isEmpty())
                                {
                                    ArrayList<Object> newUpdates = new ArrayList<>();

                                    for (Object item : updates)
                                        if (!item.getClass().equals(TL_updateDeleteChannelMessages) && !item.getClass().equals(TL_updateDeleteMessages))
                                            newUpdates.add(item);
                                        else if (DEBUG_MODE)
                                            XposedBridge.log("[TGAR] Protected message! event: " + item.getClass());

                                    param.args[0] = newUpdates;
                                }
                            }
                        });
                    }
                }

                if (!onlyNeedAR(lpparam) && !ONLY_ANTIRECALL)
                {
                    // No Sponsored Messages
                    if (!isCherrygram(lpparam))
                    {
                        String gsmMethodName = "getSponsoredMessages";
                        XposedHelpers.findAndHookMethod(messagesController, gsmMethodName, long.class, XC_MethodReplacement.returnConstant(null));
                    }

                    // Anti AntiForward
                    {
                        String aafMethodName = "isChatNoForwards";

                        if (isNekogram(lpparam))
                            aafMethodName = ObfuscateHelper.resolveNekogramMethod(aafMethodName);

                        HookUtils.findAndHookAllMethod(messagesController, aafMethodName, XC_MethodReplacement.returnConstant(false));

                        String messageObjectPath = "org.telegram.messenger.MessageObject";

                        if (isNekogram(lpparam))
                            messageObjectPath = ObfuscateHelper.resolveNekogramClass(messageObjectPath);

                        Class<?> messageObject = XposedHelpers.findClassIfExists(messageObjectPath, lpparam.classLoader);
                        if (messageObject != null)
                        {
                            String aafObjectMethodName = "canForwardMessage";

                            if (isNekogram(lpparam))
                                aafObjectMethodName = ObfuscateHelper.resolveNekogramMethod(aafObjectMethodName);

                            XposedHelpers.findAndHookMethod(messageObject, aafObjectMethodName, XC_MethodReplacement.returnConstant(false));
                        }
                        else
                        {
                            XposedBridge.log("[TGAR Error] Not found MessageObject, " + issue);
                        }
                    }
                }
            }
            else
            {
                XposedBridge.log("[TGAR Error] Not found MessagesController, " + issue);
            }

            // Fake Premium
            // XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", lpparam.classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));

            // test
            // Class<?> chatMessageCell = XposedHelpers.findClassIfExists("org.telegram.ui.Cells.ChatMessageCell", lpparam.classLoader);
            // Class<?> messageObject = XposedHelpers.findClassIfExists("org.telegram.messenger.MessageObject", lpparam.classLoader);
        }
    }

    public static <T> ArrayList<T> castList(Object obj, Class<T> clazz)
    {
        ArrayList<T> result = new ArrayList<>();
        if (obj instanceof ArrayList<?>)
        {
            for (Object o : (ArrayList<?>) obj)
                result.add(clazz.cast(o));

            return result;
        }
        return null;
    }
}
