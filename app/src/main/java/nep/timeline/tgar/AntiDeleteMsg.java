package nep.timeline.tgar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.TMoe.AccountController;
import nep.timeline.tgar.TMoe.HostInfo;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.viruals.UserConfig;

public class AntiDeleteMsg {
    private static final Map<Integer, SQLiteDatabase> mDatabase = new HashMap<>(1);

    private static final Object lock = new Object();

    private static SQLiteDatabase ensureDatabase(int slot) {
        if (!(slot >= 0 && slot < Short.MAX_VALUE)) {
            throw new IllegalArgumentException("invalid slot: " + slot);
        }
        if (mDatabase.containsKey(slot)) {
            return mDatabase.get(slot);
        }
        Context context = HostInfo.getApplication();
        File filesDir = context.getFilesDir();
        File databaseFile = new File(
                slot == 0 ? filesDir.getAbsolutePath() : new File(filesDir, "account" + slot).getAbsolutePath(),
                "Telegram_deleted_messages.db"
        );
        boolean createTable = !databaseFile.exists();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                databaseFile.getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READWRITE | (createTable ? SQLiteDatabase.CREATE_IF_NECESSARY : 0)
        );
        synchronized (lock) {
            database.beginTransaction();
            try {
                database.rawQuery("PRAGMA secure_delete = ON", null).close();
                database.rawQuery("PRAGMA temp_store = MEMORY", null).close();
                database.rawQuery("PRAGMA journal_mode = WAL", null).close();
                database.rawQuery("PRAGMA journal_size_limit = 10485760", null).close();
                database.rawQuery("PRAGMA busy_timeout = 5000", null).close();
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
        database.execSQL("CREATE TABLE IF NOT EXISTS t_deleted_messages (\n" +
                "  message_id INTEGER NOT NULL,\n" +
                "  dialog_id INTEGER NOT NULL\n" +
                ");");
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_messages_combined ON t_deleted_messages (message_id, dialog_id)");
        mDatabase.put(slot, database);
        return database;
    }


    public static boolean messageIsDeleted(int messageId, long dialogId) {
        int currentSlot = AccountController.getCurrentActiveSlot();
        if (currentSlot < 0) {
            XposedBridge.log("message_is_delete: no active account");
            return false;
        }
        SQLiteDatabase database = ensureDatabase(currentSlot);
        Cursor cursor = null;
        boolean result;

        try {
            String[] columns = {"message_id", "dialog_id"};
            String selection = "message_id = ? AND dialog_id = ?";
            String[] selectionArgs = {String.valueOf(messageId), String.valueOf(dialogId)};
            cursor = database.query("t_deleted_messages", columns, selection, selectionArgs, null, null, null);

            result = (cursor.getCount() > 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    private static void insertDeletedMessage(ArrayList<Integer> messageIds, long dialogId) {
        int currentSlot = AccountController.getCurrentActiveSlot();
        if (currentSlot < 0) {
            XposedBridge.log("message_is_delete: no active account");
            return;
        }
        SQLiteDatabase database = ensureDatabase(currentSlot);
        database.beginTransaction();
        try {
            for (Integer messageId : messageIds) {
                ContentValues values = new ContentValues();
                values.put("message_id", messageId);
                values.put("dialog_id", dialogId);

                database.insert("t_deleted_messages", null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            if (!(e instanceof SQLiteConstraintException)) {
                XposedBridge.log("failed to insert deleted message: " + e.getMessage());
            }
        } finally {
            database.endTransaction();
        }
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
                    long dialogID = (long) args[1];
                    ArrayList<Integer> arrayList = Utils.castList(args[0], Integer.class);
                    param.setResult(null);
                    insertDeletedMessage(arrayList, dialogID);
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
