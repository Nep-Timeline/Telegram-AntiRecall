package nep.timeline.tgar.viruals;

import de.robv.android.xposed.XposedHelpers;
import nep.timeline.tgar.Utils;
import nep.timeline.tgar.obfuscate.AutomationResolver;
import nep.timeline.tgar.utils.FieldUtils;

public class UserConfig {
    public static int getSelectedAccount() {
        Class<?> userConfig = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.messenger.UserConfig"), Utils.globalLoadPackageParam.classLoader);
        String selectedAccountField = AutomationResolver.resolve("UserConfig", "selectedAccount", AutomationResolver.ResolverType.Field);
        return FieldUtils.getFieldIntOfClass(null, userConfig, selectedAccountField);
    }
}
