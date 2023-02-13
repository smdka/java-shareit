package ru.practicum.shareit.item.util;

public final class StringUtil {

    private StringUtil() {
    }

    public static boolean containsTextIgnoreCase(String str, String text) {
        return str.toLowerCase().contains(text.toLowerCase());
    }
}
