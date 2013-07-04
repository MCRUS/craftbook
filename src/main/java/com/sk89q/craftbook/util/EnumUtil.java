package com.sk89q.craftbook.util;

import java.util.Locale;

/**
 * @author Silthus
 */
public final class EnumUtil {

    /**
     * Get the enum value of a string, null if it doesn't exist.
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {

        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    /**
     * Get the enum value of a string, null if it doesn't exist.
     */
    public static <T extends Enum<T>> T getEnumFromStringCaseSensitive(Class<T> c, String string) {

        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
