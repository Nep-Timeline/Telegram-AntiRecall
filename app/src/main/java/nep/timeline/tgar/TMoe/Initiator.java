package nep.timeline.tgar.TMoe;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

public class Initiator {
    private static ClassLoader sHostClassLoader;
    private static ClassLoader sPluginParentClassLoader;

    private Initiator() {
        throw new AssertionError("No instance for you!");
    }

    public static void initWithHostClassLoader(ClassLoader classLoader) {
        sHostClassLoader = classLoader;
        sPluginParentClassLoader = Initiator.class.getClassLoader();
    }

    public static ClassLoader getPluginClassLoader() {
        return Initiator.class.getClassLoader();
    }

    public static ClassLoader getHostClassLoader() {
        return sHostClassLoader;
    }

    @NonNull
    public static Class<?> loadClass(@NonNull String className) throws ClassNotFoundException {
        Class<?> clazz = load(className);
        if (clazz == null) {
            throw new ClassNotFoundException(className);
        }
        return clazz;
    }

    @Nullable
    public static Class<?> load(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        className = className.replace('/', '.');
        if (className.endsWith(";")) {
            if (className.charAt(0) == 'L') {
                className = className.substring(1, className.length() - 1);
            } else {
                className = className.substring(0, className.length() - 1);
            }
        }
        if (className.startsWith(".")) {
            className = HostInfo.getPackageName() + className;
        }
        try {
            return sHostClassLoader.loadClass(className);
        } catch (ClassNotFoundException ignored) {
        }
        try {
            return sPluginParentClassLoader.loadClass(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}