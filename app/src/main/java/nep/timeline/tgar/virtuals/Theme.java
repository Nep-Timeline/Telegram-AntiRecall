package nep.timeline.tgar.virtuals;

import android.text.TextPaint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nep.timeline.tgar.Utils;
import nep.timeline.tgar.obfuscate.AutomationResolver;

public class Theme {
    public static TextPaint getTextPaint()
    {
        Class<?> theme = XposedHelpers.findClassIfExists(AutomationResolver.resolve("org.telegram.ui.ActionBar.Theme"), Utils.globalLoadPackageParam.classLoader);
        List<Field> fields = new ArrayList<>();
        for (Field declaredField : theme.getDeclaredFields())
            if (declaredField.getName().equals(AutomationResolver.resolve("Theme", "chat_timePaint", AutomationResolver.ResolverType.Field)))
                fields.add(declaredField);

        if (!fields.isEmpty()) {
            try
            {
                Field textPaintField = null;
                for (Field field : fields) {
                    if (field.getType().equals(TextPaint.class))
                    {
                        textPaintField = field;
                    }
                }
                if (textPaintField != null)
                    return (TextPaint) textPaintField.get(null);
                else {
                    for (Field field : fields) {
                        if (field.getType().getName().contains("TextPaint"))
                        {
                            textPaintField = field;
                        }
                    }
                    if (textPaintField != null)
                        return (TextPaint) textPaintField.get(null);
                    else
                        XposedBridge.log("Not found chat_timePaint field in Theme, " + Utils.issue);
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        else
            XposedBridge.log("Not found chat_timePaint field in Theme, " + Utils.issue);

        return null;
    }
}
