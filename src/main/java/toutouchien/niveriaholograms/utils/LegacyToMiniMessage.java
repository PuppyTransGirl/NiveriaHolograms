package toutouchien.niveriaholograms.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to convert legacy Minecraft color/format codes (e.g. {@code &a}, {@code §l}, hex
 * forms like {@code &#RRGGBB} or {@code &x&R&R&G&G&B&B}) into MiniMessage-like tags
 * (e.g. {@code <green>}, {@code <bold>}, {@code <#RRGGBB>}).
 *
 * <p>Behavior highlights:
 * - Legacy color codes reset active text formatting (decorations) - this implementation
 * closes decoration tags when a new color (including hex) is opened.
 * - {@code &r} resets both decorations and the active color.
 *
 * <p>This class is not instantiable and provides a single static entry point:
 * {@link #convert(String)}.
 */
public final class LegacyToMiniMessage {
    private static final Map<Character, String> COLOR_MAP = Map.ofEntries(
            Map.entry('0', "black"),
            Map.entry('1', "dark_blue"),
            Map.entry('2', "dark_green"),
            Map.entry('3', "dark_aqua"),
            Map.entry('4', "dark_red"),
            Map.entry('5', "dark_purple"),
            Map.entry('6', "gold"),
            Map.entry('7', "gray"),
            Map.entry('8', "dark_gray"),
            Map.entry('9', "blue"),
            Map.entry('a', "green"),
            Map.entry('b', "aqua"),
            Map.entry('c', "red"),
            Map.entry('d', "light_purple"),
            Map.entry('e', "yellow"),
            Map.entry('f', "white")
    );

    private static final Map<Character, String> FORMAT_MAP = Map.of(
            'k', "obfuscated",
            'l', "bold",
            'm', "strikethrough",
            'n', "underlined",
            'o', "italic"
    );

    /**
     * Master regex that matches:
     * - Hex colors in forms "&#RRGGBB" or "#RRGGBB" (group 1)
     * - The "§x§R§R§G§G§B§B" hex colors style (group 2)
     * - Standard legacy codes (group 3)
     */
    private static final Pattern MASTER_PATTERN = Pattern.compile(
            "(?:&#|#)([0-9A-Fa-f]{6})|"
                    + "[§&][xX]((?:[§&][0-9A-Fa-f]){6})|"
                    + "[§&]([0-9A-FK-ORa-fk-or])"
    );

    private LegacyToMiniMessage() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert a string containing legacy Minecraft formatting codes into a string using
     * MiniMessage-like tags.
     *
     * <p>Examples:
     * - "&aHello" -> "<green>Hello"
     * - "&#FF00FFText" -> "<#FF00FF>Text"
     * - "&lBold" -> "<bold>Bold"
     *
     * @param input non-null input string. If empty, the same empty string is returned.
     * @return converted string with MiniMessage-like tags
     */
    @NotNull
    public static String convert(@NotNull String input) {
        if (input.isEmpty())
            return input;

        Matcher matcher = MASTER_PATTERN.matcher(input);
        StringBuilder builder = new StringBuilder();

        Set<String> activeDecorations = new LinkedHashSet<>();
        // Track the active color so we can close it manually when switching colors/resetting
        String activeColor = null;

        while (matcher.find()) {
            StringBuilder replacement = new StringBuilder();

            // CASE 1 & 2: Hex Colors
            String hexColor = null;
            if (matcher.group(1) != null)
                hexColor = matcher.group(1).toUpperCase(Locale.ROOT);
            else if (matcher.group(2) != null)
                hexColor = matcher.group(2).replaceAll("[§&]", "").toUpperCase(Locale.ROOT);

            if (hexColor != null) {
                // 1. Close Decorations (Legacy behavior: color resets formats)
                replacement.append(closeDecorations(activeDecorations));

                // 2. Close previous color if any
                if (activeColor != null) {
                    replacement.append("</").append(activeColor).append(">");
                }

                // 3. Open new hex color
                activeColor = "#" + hexColor;
                replacement.append("<").append(activeColor).append(">");
            }
            // CASE 3: Standard Legacy Code (colors/formats/reset)
            else if (matcher.group(3) != null) {
                activeColor = convertStandardLegacyCode(matcher, replacement, activeDecorations, activeColor);
            }

            matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement.toString()));
        }

        matcher.appendTail(builder);
        return builder.toString();
    }

    /**
     * Handle standard single-character legacy codes (color, format, reset).
     *
     * <p>This method updates {@code replacement} with the tags to open/close and returns the
     * potentially updated active color value.
     *
     * @param matcher           matcher positioned on the match; group(3) contains the code char
     * @param replacement       builder for the replacement text (tags)
     * @param activeDecorations ordered set of currently open decoration tags
     * @param activeColor       currently active color tag (without angle brackets), or null
     * @return the updated active color (may be same as input, new value, or null if closed)
     */
    @Nullable
    private static String convertStandardLegacyCode(
            @NotNull Matcher matcher,
            @NotNull StringBuilder replacement,
            @NotNull Set<String> activeDecorations,
            @Nullable String activeColor
    ) {
        char code = Character.toLowerCase(matcher.group(3).charAt(0));

        if (code == 'r') {
            // &r -> Close ALL decorations AND the active color
            replacement.append(closeDecorations(activeDecorations));
            if (activeColor != null) {
                replacement.append("</").append(activeColor).append(">");
                activeColor = null;
            }
        } else if (COLOR_MAP.containsKey(code)) {
            // New legacy color: reset decorations and switch color
            replacement.append(closeDecorations(activeDecorations));

            if (activeColor != null)
                replacement.append("</").append(activeColor).append(">");

            String newColor = COLOR_MAP.get(code);
            activeColor = newColor;
            replacement.append("<").append(newColor).append(">");
        } else if (FORMAT_MAP.containsKey(code)) {
            // Decoration: open if not already active
            String tag = FORMAT_MAP.get(code);
            if (!activeDecorations.contains(tag)) {
                activeDecorations.add(tag);
                replacement.append("<").append(tag).append(">");
            }
        }

        return activeColor;
    }

    /**
     * Close all active decoration tags in reverse order of opening.
     *
     * <p>Example: if active contains ["bold", "italic"] then this returns
     * "</italic></bold>" and clears the {@code active} set.
     *
     * @param active the ordered set of open decoration tags
     * @return string containing closing tags in correct order, or empty string if none
     */
    @NotNull
    private static String closeDecorations(@NotNull Set<String> active) {
        if (active.isEmpty())
            return "";

        StringBuilder closer = new StringBuilder();
        List<String> toClose = new ArrayList<>(active).reversed();

        for (String tag : toClose)
            closer.append("</").append(tag).append(">");

        active.clear();
        return closer.toString();
    }
}