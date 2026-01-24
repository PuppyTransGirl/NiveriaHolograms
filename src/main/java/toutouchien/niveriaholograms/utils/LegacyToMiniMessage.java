package toutouchien.niveriaholograms.utils;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LegacyToMiniMessage {
    private static final Map<Character, String> COLOR_MAP = new HashMap<>();
    private static final Map<Character, String> FORMAT_MAP = new HashMap<>();

    static {
        COLOR_MAP.put('0', "black");
        COLOR_MAP.put('1', "dark_blue");
        COLOR_MAP.put('2', "dark_green");
        COLOR_MAP.put('3', "dark_aqua");
        COLOR_MAP.put('4', "dark_red");
        COLOR_MAP.put('5', "dark_purple");
        COLOR_MAP.put('6', "gold");
        COLOR_MAP.put('7', "gray");
        COLOR_MAP.put('8', "dark_gray");
        COLOR_MAP.put('9', "blue");
        COLOR_MAP.put('a', "green");
        COLOR_MAP.put('b', "aqua");
        COLOR_MAP.put('c', "red");
        COLOR_MAP.put('d', "light_purple");
        COLOR_MAP.put('e', "yellow");
        COLOR_MAP.put('f', "white");

        FORMAT_MAP.put('k', "obfuscated"); // §k
        FORMAT_MAP.put('l', "bold");       // §l
        FORMAT_MAP.put('m', "strikethrough"); // §m
        FORMAT_MAP.put('n', "underlined"); // §n
        FORMAT_MAP.put('o', "italic");     // §o
    }

    // Matches (?:&#|#)RRGGBB
    private static final Pattern AMP_HASH_PATTERN =
            Pattern.compile("(?:&#|#)([0-9A-Fa-f]{6})");

    // Matches Minecraft-style §x§R§R... (or &x&...)
    private static final Pattern HEX_PATTERN = Pattern.compile(
            "[§&][xX][§&]([0-9A-Fa-f])[§&]([0-9A-Fa-f])[§&]([0-9A-Fa-f])"
                    + "[§&]([0-9A-Fa-f])[§&]([0-9A-Fa-f])[§&]([0-9A-Fa-f])");

    // Single-character legacy codes like &6 or §l
    private static final Pattern SINGLE_PATTERN =
            Pattern.compile("[§&]([0-9A-FK-ORa-fk-or])");

    private LegacyToMiniMessage() {
        throw new IllegalStateException("Utility class");
    }

    public static String convert(String input) {
        if (input == null || input.isEmpty())
            return input;

        // Convert &#RRGGBB and #RRGGBB -> <#RRGGBB>
        Matcher singleMatcher = matcher(input);
        StringBuilder finalOut = new StringBuilder();
        while (singleMatcher.find()) {
            char code = Character.toLowerCase(singleMatcher.group(1).charAt(0));

            if (COLOR_MAP.containsKey(code)) {
                singleMatcher.appendReplacement(finalOut, "<" + COLOR_MAP.get(code) + ">");
                continue;
            }

            if (FORMAT_MAP.containsKey(code)) {
                singleMatcher.appendReplacement(finalOut, "<" + FORMAT_MAP.get(code) + ">");
                continue;
            }

            singleMatcher.appendReplacement(finalOut, "");
        }

        singleMatcher.appendTail(finalOut);

        return finalOut.toString();
    }

    private static @NonNull Matcher matcher(String input) {
        Matcher ampHash = AMP_HASH_PATTERN.matcher(input);
        StringBuffer step1 = new StringBuffer();
        while (ampHash.find()) {
            String hex = ampHash.group(1).toUpperCase();
            ampHash.appendReplacement(step1, "<#" + hex + ">");
        }

        ampHash.appendTail(step1);
        String intermediate = convertMinecraftStyle(step1);

        // Convert single-character legacy codes (&6, §l, etc.)
        return SINGLE_PATTERN.matcher(intermediate);
    }

    private static @NonNull String convertMinecraftStyle(StringBuffer step1) {
        String stage1 = step1.toString();

        // Convert Minecraft-style §x§R... -> <#RRGGBB>
        Matcher hexMatcher = HEX_PATTERN.matcher(stage1);
        StringBuilder hexReplaced = new StringBuilder();
        while (hexMatcher.find()) {
            String hex = (hexMatcher.group(1) + hexMatcher.group(2) + hexMatcher.group(3)
                    + hexMatcher.group(4) + hexMatcher.group(5) + hexMatcher.group(6)).toUpperCase();
            hexMatcher.appendReplacement(hexReplaced, "<#" + hex + ">");
        }

        hexMatcher.appendTail(hexReplaced);
        return hexReplaced.toString();
    }
}