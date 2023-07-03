package nep.timeline.tgar;

import java.util.ArrayList;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Utils {
    public static XC_LoadPackage.LoadPackageParam globalLoadPackageParam = null;

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
