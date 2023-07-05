package nep.timeline.tgar.TMoe;

import android.app.Application;
import android.os.Build;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import de.robv.android.xposed.XposedBridge;
import nep.timeline.tgar.Utils;

public class StartupRoutine {

    private StartupRoutine() {
        throw new AssertionError("No instance for you!");
    }

    /**
     * Parent ClassLoader is now changed to a new one, we can initialize the rest now.
     * There are the early init procedures.
     *
     * @param application the application
     * @param lpwReserved null, not used
     * @param bReserved   false, not used
     */
    public static void execPreStartupInit(Application application, String lpwReserved, boolean bReserved) {
        // native library was already loaded before this method is called
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.setHiddenApiExemptions("L");
        }
        HostInfo.setHostApplication(application);
        Initiator.initWithHostClassLoader(application.getClassLoader());
    }

    public static void execPostStartupInit() {
        Class<?> kTheme = ClassLocator.getThemeClass();
        if (kTheme == null) {
            XposedBridge.log("can not find class Theme");
            // maybe obfuscated
            Utils.async(() -> {
                Class<?> k = ClassLocator.findThemeClass();
                if (k != null) {
                    XposedBridge.log("find class Theme: " + k.getName());
                } else {
                    XposedBridge.log("can not find class Theme");
                }
            });
        }
    }
}