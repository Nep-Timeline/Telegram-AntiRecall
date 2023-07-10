package nep.timeline.tgar;

import android.content.res.XModuleResources;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.application.ApplicationLoaderHook;

public class HookInit implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    private static final List<String> hookPackages = Arrays.asList("org.telegram.messenger", "org.telegram.messenger.web", "org.telegram.messenger.beta", "org.telegram.plus",
            "tw.nekomimi.nekogram",
            "com.cool2645.nekolite",
            "com.exteragram.messenger",
            "org.forkclient.messenger",
            "org.forkclient.messenger.beta",
            "uz.unnarsx.cherrygram",
            "xyz.nextalone.nagram", "xyz.nextalone.nnngram",
            "nekox.messenger",
            "me.onlyfire.yukigram.beta");
    private static String MODULE_PATH = null;
    public static final boolean DEBUG_MODE = true;
    public static final boolean LITE_MODE = true;

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!hookPackages.contains(resparam.packageName))
            return;

        XModuleResources.createInstance(MODULE_PATH, resparam.res);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (hookPackages.contains(lpparam.packageName)) {
            if (DEBUG_MODE)
                XposedBridge.log("[TGAR] Trying to hook app: " + lpparam.packageName);
            Utils.globalLoadPackageParam = lpparam;
            ApplicationLoaderHook.init(lpparam.classLoader);

            AntiRecall.initUI(lpparam);

            AntiRecall.initNotification(lpparam);

            AntiRecall.init(lpparam);
        }
    }
}
