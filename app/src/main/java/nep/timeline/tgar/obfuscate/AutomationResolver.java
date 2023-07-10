package nep.timeline.tgar.obfuscate;

import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nep.timeline.tgar.ClientChecker;
import nep.timeline.tgar.Utils;
import nep.timeline.tgar.obfuscate.resolves.*;

public class AutomationResolver {
    public static String resolve(String className, final XC_LoadPackage.LoadPackageParam lpparam)
    {
        if (ClientChecker.isNekogram(lpparam))
        {
            if (Nekogram.ClassResolver.has(className))
                return Nekogram.ClassResolver.resolve(className);
        }
        else if (ClientChecker.isYukigram(lpparam))
        {
            if (Yukigram.ClassResolver.has(className))
                return Yukigram.ClassResolver.resolve(className);
        }

        return className;
    }

    public static String resolve(String className, String name, ResolverType type, final XC_LoadPackage.LoadPackageParam lpparam)
    {
        if (ClientChecker.isNekogram(lpparam))
        {
            if (type == ResolverType.Field)
            {
                if (Nekogram.FieldResolver.has(className, name))
                    return Nekogram.FieldResolver.resolve(className, name);
            }
            else if (type == ResolverType.Method)
            {
                if (Nekogram.MethodResolver.has(className, name))
                    return Nekogram.MethodResolver.resolve(className, name);
            }
        }
        else if (ClientChecker.isYukigram(lpparam))
        {
            if (type == ResolverType.Field)
            {
                if (Yukigram.FieldResolver.has(className, name))
                    return Yukigram.FieldResolver.resolve(className, name);
            }
            else if (type == ResolverType.Method)
            {
                if (Yukigram.MethodResolver.has(className, name))
                    return Yukigram.MethodResolver.resolve(className, name);
            }
        }

        return name;
    }

    public static String resolve(String className)
    {
        return resolve(className, Utils.globalLoadPackageParam);
    }

    public static String resolve(String className, String name, ResolverType type)
    {
        return resolve(className, name, type, Utils.globalLoadPackageParam);
    }

    public enum ResolverType
    {
        Field,
        Method
    }
}
