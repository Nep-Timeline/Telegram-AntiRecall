package nep.timeline.tgar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nep.timeline.tgar.obfuscate.AutomationResolver;

public class AntiDeleteMsg {
    private static final List<Integer> deletedMessagesIds = new ArrayList<>();

    public static boolean messageIsDeleted(int messageId) {
        return deletedMessagesIds.contains(messageId);
    }

    public static void insertDeletedMessage(ArrayList<Integer> messageIds) {
        deletedMessagesIds.addAll(messageIds);
    }

    public static void init() throws ClassNotFoundException, NoSuchMethodException {
        Class<?> messagesStorage = Utils.globalLoadPackageParam.classLoader.loadClass(AutomationResolver.resolve("org.telegram.messenger.MessagesStorage"));
        Class<?> notificationCenter = Utils.globalLoadPackageParam.classLoader.loadClass(AutomationResolver.resolve("org.telegram.messenger.NotificationCenter"));
        Class<?> notificationsController = Utils.globalLoadPackageParam.classLoader.loadClass(AutomationResolver.resolve("org.telegram.messenger.NotificationsController"));

        int messagesDeletedValue = (int) XposedHelpers.getStaticObjectField(notificationCenter, AutomationResolver.resolve("NotificationCenter", "messagesDeleted", AutomationResolver.ResolverType.Field));

        Method postNotificationName = notificationCenter.getDeclaredMethod(AutomationResolver.resolve("NotificationCenter", "postNotificationName", AutomationResolver.ResolverType.Method), int.class, Object[].class);

        Method removeDeletedMessagesFromNotifications = null;
        for (Method method : notificationsController.getDeclaredMethods()) {
            if (method.getName().equals(AutomationResolver.resolve("NotificationsController", "removeDeletedMessagesFromNotifications", AutomationResolver.ResolverType.Method))) {
                removeDeletedMessagesFromNotifications = method;
            }
        }

        ArrayList<Method> methods = new ArrayList<>();
        for (Method declaredMethod : messagesStorage.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(AutomationResolver.resolve("MessagesStorage", "markMessagesAsDeleted", AutomationResolver.ResolverType.Method)) || declaredMethod.getName().equals(AutomationResolver.resolve("MessagesStorage", "updateDialogsWithDeletedMessages", AutomationResolver.ResolverType.Method))) {
                methods.add(declaredMethod);
            }
        }

        if (methods.isEmpty()) {
            XposedBridge.log("[TGAR Error] Failed to hook markMessagesAsDeleted! Reason: No method found, " + Utils.issue);
            return;
        }

        for (Method method : methods) {
            XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
        }

        XposedBridge.hookMethod(postNotificationName, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (((int) param.args[0]) == messagesDeletedValue) {
                    Object[] args = (Object[]) param.args[1];
                    //long dialogID = (long) args[1];
                    ArrayList<Integer> arrayList = Utils.castList(args[0], Integer.class);
                    param.setResult(null);
                    insertDeletedMessage(arrayList);
                }
            }
        });

        if (removeDeletedMessagesFromNotifications == null) {
            XposedBridge.log("[TGAR Error] Failed to hook removeDeletedMessagesFromNotifications! Reason: No method found, " + Utils.issue);
            return;
        }

        XposedBridge.hookMethod(removeDeletedMessagesFromNotifications, XC_MethodReplacement.returnConstant(null));
    }
}
