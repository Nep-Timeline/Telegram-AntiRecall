package nep.timeline.tgar.viruals;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.ClientChecker;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.FieldUtils;

public class UserConfig {
    public static int getSelectedAccount(final XC_LoadPackage.LoadPackageParam lpparam) {
        return FieldUtils.getFieldIntOfClass(XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.UserConfig", lpparam), lpparam.classLoader), AutomationResolver.resolve("UserConfig", "selectedAccount", AutomationResolver.ResolverType.Field, lpparam));
    }
}
