package nep.timeline.tgar.TMoe;

import android.app.Application;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class StartupHook {
    public static final StartupHook INSTANCE = new StartupHook();

    private boolean initialized = false;

    public void doInit(ClassLoader rtLoader) {
        // our minSdk is 21 so there is no need to wait for MultiDex to initialize
        if (initialized)
            return;
        if (rtLoader == null) {
            throw new AssertionError("StartupHook.doInit: rtLoader == null");
        }
        Class<?> applicationClass = null;
        try {
            applicationClass = rtLoader.loadClass("org.telegram.messenger.ApplicationLoader");
        } catch (ClassNotFoundException ignored) {
        }
        if (applicationClass == null) {
            try {
                applicationClass = rtLoader.loadClass("org.telegram.messenger.ApplicationLoaderImpl");
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (applicationClass == null) {
            try {
                applicationClass = rtLoader.loadClass("org.thunderdog.challegram.BaseApplication");
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (applicationClass == null) {
            throw new AssertionError("StartupHook.doInit: unable to find ApplicationLoader");
        }
        XposedHelpers.findAndHookMethod(applicationClass, "onCreate", new XC_MethodHook(51) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Application app = (Application) param.thisObject;
                if (app == null)
                    throw new AssertionError("app == null");
                StartupRoutine.execPreStartupInit(app);
            }
        });
        initialized = true;
    }
}