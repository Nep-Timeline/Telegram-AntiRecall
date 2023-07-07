package nep.timeline.tgar;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ClientChecker {
    public static boolean isNekogram(final XC_LoadPackage.LoadPackageParam lpparam)
    {
        return lpparam.packageName.equals("tw.nekomimi.nekogram");
    }

    public static boolean isNekogram()
    {
        return isNekogram(Utils.globalLoadPackageParam);
    }
}
