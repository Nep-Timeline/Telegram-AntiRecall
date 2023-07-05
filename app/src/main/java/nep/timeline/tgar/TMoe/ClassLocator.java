package nep.timeline.tgar.TMoe;

import androidx.annotation.Nullable;

import de.robv.android.xposed.XposedBridge;
import nep.timeline.tgar.obfuscate.AutomationResolver;

public class ClassLocator {
    private static Class<?> kThemeClass = null;
    private static boolean kThemeFastFail = false;

    @Nullable
    public static Class<?> getThemeClass() {
        if (kThemeClass != null) {
            return kThemeClass;
        }
        if (kThemeFastFail) {
            return null;
        }
        kThemeClass = Initiator.load(AutomationResolver.resolve("org.telegram.ui.ActionBar.Theme"));
        return kThemeClass;
    }

    @Nullable
    public static Class<?> findThemeClass() {
        return kThemeClass;
    }

    private static Class<?> kUserConfigClass = null;

    @Nullable
    public static Class<?> getUserConfigClass() {
        if (kUserConfigClass != null) {
            return kUserConfigClass;
        }
        kUserConfigClass = Initiator.load(AutomationResolver.resolve("org.telegram.messenger.UserConfig"));
        // TODO: 2022-01-30 this class is obfuscated
        if (kUserConfigClass != null) {
            return kUserConfigClass;
        }
        XposedBridge.log("UserConfig class not found");
        return null;
    }
}