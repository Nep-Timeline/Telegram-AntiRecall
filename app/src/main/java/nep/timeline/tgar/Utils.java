package nep.timeline.tgar;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Utils {
    public static XC_LoadPackage.LoadPackageParam globalLoadPackageParam = null;
    public static final String issue = "Your telegram may have been modified! You can submit issue to let developer to try support to the telegram client you are using.";

    public static <T> ArrayList<T> castList(Object obj, Class<T> clazz)
    {
        ArrayList<T> result = new ArrayList<>();
        if (obj instanceof ArrayList<?>)
        {
            for (Object o : (ArrayList<?>) obj)
                result.add(clazz.cast(o));

            return result;
        }
        return null;
    }
}
