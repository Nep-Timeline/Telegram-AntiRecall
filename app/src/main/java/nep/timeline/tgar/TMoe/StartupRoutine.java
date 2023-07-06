package nep.timeline.tgar.TMoe;

import android.app.Application;
import android.os.Build;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

public class StartupRoutine {
    public static void execPreStartupInit(Application application) {
        // native library was already loaded before this method is called
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.setHiddenApiExemptions("L");
        }
        HostInfo.setHostApplication(application);
    }
}