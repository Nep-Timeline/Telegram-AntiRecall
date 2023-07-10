package nep.timeline.tgar.obfuscate.resolves;

import java.util.ArrayList;
import java.util.List;

import nep.timeline.tgar.obfuscate.struct.ClassInfo;
import nep.timeline.tgar.obfuscate.struct.FieldInfo;
import nep.timeline.tgar.obfuscate.struct.MethodInfo;

public class Yukigram {
    private static final List<ClassInfo> classList = new ArrayList<>();
    private static final List<FieldInfo> fieldList = new ArrayList<>();
    private static final List<MethodInfo> methodList = new ArrayList<>();

    static {
        classList.add(new ClassInfo("org.telegram.messenger.MessagesController", "iz0"));
        classList.add(new ClassInfo("org.telegram.messenger.NotificationsController", "bD0"));
        classList.add(new ClassInfo("org.telegram.messenger.NotificationCenter", "sC0"));
        classList.add(new ClassInfo("org.telegram.messenger.MessagesStorage", "pA0"));
        classList.add(new ClassInfo("org.telegram.messenger.MessageObject", "Mw0"));
        classList.add(new ClassInfo("org.telegram.messenger.UserConfig", "Zs1"));
        classList.add(new ClassInfo("org.telegram.tgnet.TLRPC$Message", "Ng1"));
        classList.add(new ClassInfo("org.telegram.tgnet.TLRPC$TL_updateDeleteMessages", "Ja1"));
        classList.add(new ClassInfo("org.telegram.tgnet.TLRPC$TL_updateDeleteChannelMessages", "Ia1"));
        classList.add(new ClassInfo("org.telegram.ui.Cells.ChatMessageCell", "org.telegram.ui.Cells.m"));
        classList.add(new ClassInfo("org.telegram.ui.ActionBar.Theme", "Wk1"));
        classList.add(new ClassInfo("org.telegram.ui.ChatPullingDownDrawable", "org.telegram.ui.T2"));
        classList.add(new ClassInfo("org.telegram.ui.ChatActivity", "org.telegram.ui.G2"));

        fieldList.add(new FieldInfo("MessageObject", "messageOwner", "a"));
        fieldList.add(new FieldInfo("UserConfig", "selectedAccount", "o"));
        fieldList.add(new FieldInfo("Theme", "chat_timePaint", "J"));
        fieldList.add(new FieldInfo("NotificationCenter", "messagesDeleted", "i"));
        fieldList.add(new FieldInfo("TLRPC$Message", "id", "a"));
        fieldList.add(new FieldInfo("TLRPC$TL_updateDeleteMessages", "messages", "a"));
        fieldList.add(new FieldInfo("TLRPC$TL_updateDeleteChannelMessages", "messages", "a"));

        methodList.add(new MethodInfo("ApplicationLoader", "onCreate", "n"));
        methodList.add(new MethodInfo("NotificationsController", "removeNotificationsForDialog", "p1"));
        methodList.add(new MethodInfo("NotificationCenter", "postNotificationName", "i"));
        methodList.add(new MethodInfo("MessagesStorage", "markMessagesAsDeleted", "A0"));
        methodList.add(new MethodInfo("MessagesStorage", "updateDialogsWithDeletedMessages", "C1"));
        methodList.add(new MethodInfo("MessageObject", "updateMessageText", "A3"));
        methodList.add(new MethodInfo("MessagesController", "isChatNoForwards", "j1"));
        methodList.add(new MethodInfo("MessagesController", "markDialogMessageAsDeleted", "O1"));
        methodList.add(new MethodInfo("MessagesController", "deleteMessages", "P"));
        methodList.add(new MethodInfo("MessageObject", "canForwardMessage", "o"));
        methodList.add(new MethodInfo("MessagesController", "getInstance", "I0"));
        methodList.add(new MethodInfo("ChatMessageCell", "measureTime", "Z4"));
        methodList.add(new MethodInfo("UserConfig", "getInstance", "g"));
        methodList.add(new MethodInfo("NotificationsController", "removeDeletedMessagesFromNotifications", "D"));
        methodList.add(new MethodInfo("ChatActivity", "addSponsoredMessages", "Kf"));
        methodList.add(new MethodInfo("ChatPullingDownDrawable", "getNextUnreadDialog", "g"));
        methodList.add(new MethodInfo("ChatPullingDownDrawable", "drawBottomPanel", "f"));
        methodList.add(new MethodInfo("ChatPullingDownDrawable", "draw", "e"));
        methodList.add(new MethodInfo("ChatPullingDownDrawable", "showBottomPanel", "l"));
    }

    public static class ClassResolver
    {
        public static String resolve(String name) {
            for (ClassInfo info : classList)
                if (info.getOriginal().equals(name))
                    return info.getResolved();

            return null;
        }

        public static boolean has(String name)
        {
            boolean has = false;
            for (ClassInfo info : classList) {
                if (info.getOriginal().equals(name)) {
                    has = true;
                    break;
                }
            }
            return has;
        }
    }

    public static class FieldResolver
    {
        public static String resolve(String className, String name) {
            for (FieldInfo info : fieldList)
                if (info.getClassName().equals(className) && info.getOriginal().equals(name))
                    return info.getResolved();

            return null;
        }

        public static boolean has(String className, String name)
        {
            boolean has = false;
            for (FieldInfo info : fieldList) {
                if (info.getClassName().equals(className) && info.getOriginal().equals(name)) {
                    has = true;
                    break;
                }
            }
            return has;
        }
    }

    public static class MethodResolver
    {
        public static String resolve(String className, String name) {
            for (MethodInfo info : methodList)
                if (info.getClassName().equals(className) && info.getOriginal().equals(name))
                    return info.getResolved();

            return null;
        }

        public static boolean has(String className, String name)
        {
            boolean has = false;
            for (MethodInfo info : methodList) {
                if (info.getClassName().equals(className) && info.getOriginal().equals(name)) {
                    has = true;
                    break;
                }
            }
            return has;
        }
    }
}
