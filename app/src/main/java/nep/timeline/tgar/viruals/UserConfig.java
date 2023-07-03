package nep.timeline.tgar.viruals;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.FieldUtils;

public class UserConfig {
    public static int getSelectedAccount(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> userConfig = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.UserConfig", lpparam), lpparam.classLoader);
        String selectedAccountField = AutomationResolver.resolve("UserConfig", "selectedAccount", AutomationResolver.ResolverType.Field, lpparam);
        return FieldUtils.getFieldIntOfClass(userConfig, selectedAccountField);
    }
}
