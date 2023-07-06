package nep.timeline.tgar.TMoe;

import android.app.Application;
import android.os.Build;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

public class StartupRoutine {

    private StartupRoutine() {
        throw new AssertionError("No instance for you!");
    }

    /**
     * Parent ClassLoader is now changed to a new one, we can initialize the rest now.
     * There are the early init procedures.
     *
     * @param application the application
     */
    public static void execPreStartupInit(Application application) {
        // native library was already loaded before this method is called
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.setHiddenApiExemptions("L");
        }
        HostInfo.setHostApplication(application);
        Initiator.initWithHostClassLoader(application.getClassLoader());
    }
}