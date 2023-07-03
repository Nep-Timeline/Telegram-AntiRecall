package nep.timeline.tgar.viruals;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.ClientChecker;
import nep.timeline.tgar.ObfuscateHelper;
import nep.timeline.tgar.utils.FieldUtils;

public class UserConfig {
    public static int getSelectedAccount(final XC_LoadPackage.LoadPackageParam lpparam) {
        String userConfigPath = "org.telegram.messenger.UserConfig";
        if (ClientChecker.isNekogram(lpparam))
            userConfigPath = ObfuscateHelper.resolveNekogramClass(userConfigPath);
        Class<?> userConfig = XposedHelpers.findClassIfExists(userConfigPath, lpparam.classLoader);
        String selectedAccountFieldName = "selectedAccount";
        if (ClientChecker.isNekogram(lpparam))
            selectedAccountFieldName = ObfuscateHelper.resolveNekogramField(selectedAccountFieldName);
        return FieldUtils.getFieldIntOfClass(userConfig, selectedAccountFieldName);
    }
}
