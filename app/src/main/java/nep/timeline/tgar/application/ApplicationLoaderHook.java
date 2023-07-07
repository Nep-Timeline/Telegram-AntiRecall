package nep.timeline.tgar.application;

import android.app.Application;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nep.timeline.tgar.Utils;
import nep.timeline.tgar.obfuscate.AutomationResolver;

public class ApplicationLoaderHook {
    private static boolean initialized = false;

    public static void init(ClassLoader loader) {
        // our minSdk is 21 so there is no need to wait for MultiDex to initialize
        if (initialized)
            return;

        Class<?> applicationClass = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.ApplicationLoader"), loader);
        if (applicationClass == null)
            applicationClass = XposedHelpers.findClassIfExists("org.thunderdog.challegram.BaseApplication", loader);
        if (applicationClass == null) {
            XposedBridge.log("Not found ApplicationLoader, " + Utils.issue);
            return;
        }
        XposedHelpers.findAndHookMethod(applicationClass, AutomationResolver.resolve("ApplicationLoader", "onCreate", AutomationResolver.ResolverType.Method), new XC_MethodHook(51) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Application app = (Application) param.thisObject;
                if (app == null)
                {
                    XposedBridge.log("ApplicationLoader is wrong, " + Utils.issue);
                    return;
                }

                new File(app.getFilesDir().getAbsolutePath() + "/DeletedMessages").mkdirs();
                Utils.deletedMessagesSavePath = new File(app.getFilesDir().getAbsolutePath() + "/DeletedMessages/messages.tgar");
                Utils.readDeletedMessages();

                ApplicationInfo.setApplication(app);
            }
        });
        initialized = true;
    }
}