package nep.timeline.tgar;

import android.content.res.XModuleResources;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;

import java.lang.reflect.Field;
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
import nep.timeline.tgar.TMoe.ClassLocator;
import nep.timeline.tgar.TMoe.Initiator;
import nep.timeline.tgar.TMoe.StartupHook;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.FieldUtils;
import nep.timeline.tgar.viruals.ChatMessageCellDefault;
import nep.timeline.tgar.viruals.MessageObject;
import nep.timeline.tgar.viruals.OfficialChatMessageCell;
import nep.timeline.tgar.viruals.TLRPC;
import nep.timeline.tgar.viruals.Theme;
import nep.timeline.tgar.viruals.nekogram.NekoChatMessageCell;

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

    private boolean onlyNeedAR(final XC_LoadPackage.LoadPackageParam lpparam)
    {
        return hookPackagesCustomization.contains(lpparam.packageName);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (getHookPackages().contains(lpparam.packageName)) {
            if (DEBUG_MODE)
                XposedBridge.log("[TGAR] Trying to hook app: " + lpparam.packageName);
            Utils.globalLoadPackageParam = lpparam;
            StartupHook.INSTANCE.doInit(lpparam.classLoader);

            Class<?> chatMessageCell = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.ui.Cells.ChatMessageCell"), lpparam.classLoader);

            if (chatMessageCell != null) {
                HookUtils.findAndHookMethod(chatMessageCell, AutomationResolver.resolve("ChatMessageCell", "measureTime", AutomationResolver.ResolverType.Method), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (ClientChecker.isNekogram())
                        {
                            NekoChatMessageCell cell = new NekoChatMessageCell(param.thisObject);
                            SpannableStringBuilder time = cell.getCurrentTimeString();
                            MessageObject messageObject = new MessageObject(param.args[0]);
                            TLRPC.Message owner = messageObject.getMessageOwner();
                            TLRPC.Peer peerID = owner.getPeerID();
                            long dialog_id = peerID.getChannelID();
                            int id = owner.getID();
                            String deleted = "";
                            if (AntiDeleteMsg.messageIsDeleted(id, dialog_id)) {
                                deleted = "(recalled) ";
                            }
                            String delta = deleted + " ";
                            SpannableStringBuilder newDelta = new SpannableStringBuilder();
                            newDelta.append(delta).append(time);
                            time = newDelta;
                            cell.setCurrentTimeString(time);
                            TextPaint paint = Theme.getTextPaint();
                            if (paint != null)
                            {
                                int deltaWidth = (int) Math.ceil(paint.measureText(delta));
                                cell.setTimeTextWidth(deltaWidth + cell.getTimeTextWidth());
                                cell.setTimeWidth(deltaWidth + cell.getTimeWidth());
                            }
                        }
                        else
                        {
                            OfficialChatMessageCell cell = new OfficialChatMessageCell(param.thisObject);
                            String time = (String) cell.getCurrentTimeString();
                            MessageObject messageObject = new MessageObject(param.args[0]);
                            TLRPC.Message owner = messageObject.getMessageOwner();
                            TLRPC.Peer peerID = owner.getPeerID();
                            long dialog_id = peerID.getChannelID();
                            int id = owner.getID();
                            String deleted = "";
                            if (AntiDeleteMsg.messageIsDeleted(id, dialog_id)) {
                                deleted = "(recalled) ";
                            }
                            String delta = deleted + " ";
                            time = delta + time;
                            cell.setCurrentTimeString(time);
                            TextPaint paint = Theme.getTextPaint();
                            if (paint != null)
                            {
                                int deltaWidth = (int) Math.ceil(paint.measureText(delta));
                                cell.setTimeTextWidth(deltaWidth + cell.getTimeTextWidth());
                                cell.setTimeWidth(deltaWidth + cell.getTimeWidth());
                            }
                        }
                    }
                });
            }
            else
            {
                XposedBridge.log("[TGAR Error] Not found ChatMessageCell, " + Utils.issue);
            }

            Class<?> messagesController = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.MessagesController"), lpparam.classLoader);

            if (messagesController != null)
            {
                Method[] messagesControllerMethods = messagesController.getDeclaredMethods();

                // Anti Recall
                AntiDeleteMsg.init();
                {
                    List<String> methodNames = new ArrayList<>();

                    for (Method method : messagesControllerMethods)
                        if (method.getParameterCount() == 5 && method.getParameterTypes()[0] == ArrayList.class && method.getParameterTypes()[1] == ArrayList.class && method.getParameterTypes()[2] == ArrayList.class && method.getParameterTypes()[3] == boolean.class && method.getParameterTypes()[4] == int.class)
                            methodNames.add(method.getName());

                    if (methodNames.size() != 1)
                        XposedBridge.log("[TGAR Error] Failed to hook processUpdateArray! Reason: " + (methodNames.isEmpty() ? "No method found" : "Multiple methods found") + ", " + Utils.issue);
                    else
                    {
                        String methodName = methodNames.get(0);

                        XposedHelpers.findAndHookMethod(messagesController, methodName, ArrayList.class, ArrayList.class, ArrayList.class, boolean.class, int.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                Class<?> TL_updateDeleteMessages = lpparam.classLoader.loadClass(AutomationResolver.resolve("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages"));
                                Class<?> TL_updateDeleteChannelMessages = lpparam.classLoader.loadClass(AutomationResolver.resolve("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages"));
                                ArrayList<Object> updates = Utils.castList(param.args[0], Object.class);
                                if (updates != null && !updates.isEmpty())
                                {
                                    ArrayList<Object> newUpdates = new ArrayList<>();

                                    for (Object item : updates)
                                        if (!item.getClass().equals(TL_updateDeleteChannelMessages) && !item.getClass().equals(TL_updateDeleteMessages))
                                            newUpdates.add(item);
                                        else
                                        {
                                            if (DEBUG_MODE)
                                                XposedBridge.log("[TGAR] Protected message! event: " + item.getClass());
                                        }

                                    param.args[0] = newUpdates;
                                }
                            }
                        });
                    }
                }

                if (!onlyNeedAR(lpparam) && !ONLY_ANTIRECALL)
                {
                    // No Sponsored Messages
                    if (!ClientChecker.isCherrygram())
                    {
                        String getSponsoredMessagesMethod = AutomationResolver.resolve("MessagesController", "getSponsoredMessages", AutomationResolver.ResolverType.Method);
                        XposedHelpers.findAndHookMethod(messagesController, getSponsoredMessagesMethod, long.class, XC_MethodReplacement.returnConstant(null));
                    }

                    // Anti AntiForward
                    {
                        String isChatNoForwardsMethod = AutomationResolver.resolve("MessagesController", "isChatNoForwards", AutomationResolver.ResolverType.Method);
                        HookUtils.findAndHookAllMethod(messagesController, isChatNoForwardsMethod, XC_MethodReplacement.returnConstant(false));

                        Class<?> messageObject = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.MessageObject"), lpparam.classLoader);
                        if (messageObject != null)
                        {
                            String canForwardMessageMethod = AutomationResolver.resolve("MessageObject", "canForwardMessage", AutomationResolver.ResolverType.Method);
                            XposedHelpers.findAndHookMethod(messageObject, canForwardMessageMethod, XC_MethodReplacement.returnConstant(false));
                        }
                        else
                        {
                            XposedBridge.log("[TGAR Error] Not found MessageObject, " + Utils.issue);
                        }
                    }
                }
            }
            else
            {
                XposedBridge.log("[TGAR Error] Not found MessagesController, " + Utils.issue);
            }

            // Fake Premium
            // XposedHelpers.findAndHookMethod("org.telegram.messenger.UserConfig", lpparam.classLoader, "isPremium", XC_MethodReplacement.returnConstant(true));
        }
    }
}
