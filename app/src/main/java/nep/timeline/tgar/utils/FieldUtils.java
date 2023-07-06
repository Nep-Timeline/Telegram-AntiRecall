package nep.timeline.tgar.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XposedBridge;

public class FieldUtils {
    public static Field getFieldOfClass(Object classs, String fieldName) {
        try
        {
            Field field = classs.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field;
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static double getFieldDoubleOfClass(Object clazz, String fieldName) {
        try
        {
            Field field = clazz.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field.getDouble(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static double getFieldDoubleOfClass(Class<?> clazz, Object instance, String fieldName) {
        try
        {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field.getDouble(instance);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static float getFieldFloatOfClass(Object clazz, String fieldName) {
        try
        {
            Field field = clazz.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field.getFloat(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static float getFieldFloatOfClass(Object instance, Class<?> clazz, String fieldName) {
        try
        {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field.getFloat(instance);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static int getFieldIntOfClass(Object clazz, String fieldName) {
        try
        {
            Field field = clazz.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field.getInt(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static long getFieldLongOfClass(Object clazz, String fieldName) {
        try
        {
            Field field = clazz.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field.getLong(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static Object getFieldClassOfClass(Object clazz, String fieldName) {
        try
        {
            Field field = clazz.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field.get(clazz);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFinalField(Field field, Object instance, Object newValue) throws NoSuchFieldException, IllegalAccessException
    {
        Field modifiersField = Field.class.getDeclaredField("modifiers");

        if (!modifiersField.isAccessible())
            modifiersField.setAccessible(true);

        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        if (!field.isAccessible())
            field.setAccessible(true);

        field.set(instance, newValue);
    }

    public static void setField(Field field, Object instance, Object newValue) throws NoSuchFieldException, IllegalAccessException
    {
        if (!field.isAccessible())
            field.setAccessible(true);

        field.set(instance, newValue);
    }
}
