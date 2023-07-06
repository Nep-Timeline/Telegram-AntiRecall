package nep.timeline.tgar.TMoe;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XposedBridge;
import nep.timeline.tgar.obfuscate.AutomationResolver;

/**
 * Static helper class for accessing the Telegram account status.
 *
 * @author cinit
 */
public class AccountController {
    private AccountController() {
        throw new AssertionError("no instance for you");
    }

    /**
     * Get the user id for the current active account slot.
     *
     * @param slot the account slot, non-negative
     * @return the user id, or 0 if the slot is not logged in, or exception occurs
     * @throws IllegalArgumentException if the slot is invalid
     */
    public static long getUserIdForSlot(int slot) {
        if (slot < 0 || slot > 32767) {
            throw new IllegalArgumentException("invalid slot: " + slot);
        }
        Class<?> kUserConfig = ClassLocator.getUserConfigClass();
        if (kUserConfig == null) {
            XposedBridge.log("getUserIdForSlot but UserConfig.class is null");
            return 0;
        }
        try {
            Object userConfig = kUserConfig.getMethod(AutomationResolver.resolve("UserConfig", "getInstance", AutomationResolver.ResolverType.Method), int.class).invoke(null, slot);
            return kUserConfig.getField("clientUserId").getLong(userConfig);
        } catch (IllegalAccessException e) {
            // should not happen
            throw new LinkageError("unable to access UserConfig.getInstance(I).clientUserId", e);
        } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException e) {
            XposedBridge.log(e);
        }
        return 0;
    }

    /**
     * Get the current active account slot.
     * Notice that Telegram DOES support multiple accounts and the ongoing transaction may NOT always be the active one.
     * AVOID USE THIS METHOD OR USE WITH CAUTION.
     *
     * @return the current active account slot, or -1 if exception occurs
     * @see #getUserIdForSlot(int)
     */
    public static int getCurrentActiveSlot() {
        Class<?> kUserConfig = ClassLocator.getUserConfigClass();
        if (kUserConfig == null) {
            XposedBridge.log("getCurrentActiveSlot but UserConfig.class is null");
            return -1;
        }
        try {
            return kUserConfig.getDeclaredField(AutomationResolver.resolve("UserConfig", "selectedAccount", AutomationResolver.ResolverType.Field)).getInt(null);
        } catch (IllegalAccessException e) {
            // should not happen
            throw new LinkageError("unable to access UserConfig.selectedAccount", e);
        } catch (NoSuchFieldException e) {
            XposedBridge.log(e);
        }
        return -1;
    }

    /**
     * Get current active account user id.
     * Notice that Telegram DOES support multiple accounts and the ongoing transaction may NOT always be the active one.
     * AVOID USING THIS METHOD OR USE WITH CAUTION.
     *
     * @return the current active user id, or 0 if not logged in, or exception occurs
     */
    public static long getCurrentActiveUserId() {
        int slot = getCurrentActiveSlot();
        if (slot < 0) {
            return 0;
        }
        return getUserIdForSlot(slot);
    }

    /**
     * Test if the current active account is logged in.
     *
     * @return false if the current active account is not logged in, or exception occurs
     */
    public static boolean isCurrentUserLoggedIn() {
        return getCurrentActiveUserId() != 0;
    }
}