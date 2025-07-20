package toutouchien.niveriaholograms.configurations;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import toutouchien.niveriaapi.utils.ui.ColorUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class LeaderboardHologramConfiguration extends HologramConfiguration {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+$");
    private Component serializedText;
    private TextColor background;
    private boolean seeThrough = false;
    private boolean textShadow = false;
    private int updateInterval;
    private boolean textDirty = true;
    private boolean updateIntervalDirty = true;

    private String placeholder = "%statistic_player_kills%";
    private TextColor mainColor = ColorUtils.primaryColor();
    private TextColor firstColor = TextColor.fromHexString("#FEE101");
    private TextColor secondColor = TextColor.fromHexString("#A7A7AD");
    private TextColor thirdColor = TextColor.fromHexString("#A77044");
    private TextColor otherColor = NamedTextColor.GRAY;
    private TextColor valueColor = NamedTextColor.GRAY;
    private TextColor suffixColor = NamedTextColor.WHITE;
    private String title = "Kills Leaderboard";
    private String suffix = "kills";
    private boolean showSuffix = true;
    private boolean showEmptyPlaces = true;
    private int maxLines = 10;
    private boolean reverseOrder;

    public LeaderboardHologramConfiguration background(TextColor background) {
        this.background = background;
        return this;
    }

    public LeaderboardHologramConfiguration seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public LeaderboardHologramConfiguration textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public LeaderboardHologramConfiguration updateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        this.updateIntervalDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration updateIntervalDirty(boolean updateIntervalDirty) {
        this.updateIntervalDirty = updateIntervalDirty;
        return this;
    }

    public LeaderboardHologramConfiguration textDirty(boolean textDirty) {
        this.textDirty = textDirty;
        return this;
    }

    public LeaderboardHologramConfiguration placeholder(String placeholder) {
        this.placeholder = placeholder;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration mainColor(TextColor mainColor) {
        this.mainColor = mainColor;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration firstColor(TextColor firstColor) {
        this.firstColor = firstColor;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration secondColor(TextColor secondColor) {
        this.secondColor = secondColor;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration thirdColor(TextColor thirdColor) {
        this.thirdColor = thirdColor;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration otherColor(TextColor otherColor) {
        this.otherColor = otherColor;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration valueColor(TextColor valueColor) {
        this.valueColor = valueColor;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration suffixColor(TextColor suffixColor) {
        this.suffixColor = suffixColor;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration title(String title) {
        this.title = title;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration suffix(String suffix) {
        this.suffix = suffix;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration showSuffix(boolean showSuffix) {
        this.showSuffix = showSuffix;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration showEmptyPlaces(boolean showEmptyPlaces) {
        this.showEmptyPlaces = showEmptyPlaces;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration maxLines(int maxLines) {
        this.maxLines = maxLines;
        this.textDirty = true;
        return this;
    }

    public LeaderboardHologramConfiguration reverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
        this.textDirty = true;
        return this;
    }

    public Component serializedText() {
        if (serializedText != null && updateInterval == 0 && !textDirty)
            return serializedText;

        List<PlayerEntry> entries = new ArrayList<>();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            String raw = PlaceholderAPI.setPlaceholders(player, placeholder);
            boolean isNum = NUMBER_PATTERN.matcher(raw).matches();
            if (!isNum && !showEmptyPlaces) {
                continue;
            }

            String display = isNum
                    ? raw + (showSuffix ? " " + suffix : "")
                    : "N/A";
            Integer score = isNum ? Integer.valueOf(raw) : null;
            entries.add(new PlayerEntry(player.getName(), score, display));
        }

        Comparator<Integer> numCmp = reverseOrder
                ? Comparator.reverseOrder()
                : Comparator.naturalOrder();

        entries.sort(
                Comparator.comparing(
                                PlayerEntry::score,
                                Comparator.nullsLast(numCmp)
                        )
                        .thenComparing(playerEntry -> playerEntry.playerName())
        );

        if (entries.size() > maxLines) {
            entries = entries.subList(0, maxLines);
        }

        while (entries.size() < maxLines) {
            entries.add(new PlayerEntry("Unknown", null, "N/A"));
        }

        TextComponent.Builder builder = Component.text()
                .append(Component.text(title, mainColor))
                .appendNewline()
                .appendNewline();

        for (int i = 0; i < entries.size(); i++) {
            PlayerEntry e = entries.get(i);
            TextColor lineColor;
            lineColor = switch (i) {
                case 0 -> firstColor;
                case 1 -> secondColor;
                case 2 -> thirdColor;
                default -> otherColor;
            };

            builder.append(Component.text((i + 1) + ". ", lineColor))
                    .append(Component.text(e.playerName()))
                    .appendSpace()
                    .append(Component.text(e.score() != null ? Integer.toString(e.score()) : "N/A", valueColor))
                    .appendSpace();

            if (showSuffix) {
                builder.append(Component.text(suffix, suffixColor));
            }

            builder.appendNewline();
        }

        return this.serializedText = builder.build();
    }

    public TextColor background() {
        return background;
    }

    public boolean seeThrough() {
        return seeThrough;
    }

    public boolean textShadow() {
        return textShadow;
    }

    public int updateInterval() {
        return updateInterval;
    }

    public boolean textDirty() {
        return textDirty;
    }

    public boolean updateIntervalDirty() {
        return updateIntervalDirty;
    }

    public String placeholder() {
        return placeholder;
    }

    public TextColor mainColor() {
        return mainColor;
    }

    public TextColor firstColor() {
        return firstColor;
    }

    public TextColor secondColor() {
        return secondColor;
    }

    public TextColor thirdColor() {
        return thirdColor;
    }

    public TextColor otherColor() {
        return otherColor;
    }

    public TextColor valueColor() {
        return valueColor;
    }

    public TextColor suffixColor() {
        return suffixColor;
    }

    public String title() {
        return title;
    }

    public String suffix() {
        return suffix;
    }

    public boolean showSuffix() {
        return showSuffix;
    }

    public boolean showEmptyPlaces() {
        return showEmptyPlaces;
    }

    public int maxLines() {
        return maxLines;
    }

    public boolean reverseOrder() {
        return reverseOrder;
    }

    @Override
    public LeaderboardHologramConfiguration copy() {
        LeaderboardHologramConfiguration copy = new LeaderboardHologramConfiguration();
        copy.background = this.background;
        copy.seeThrough = this.seeThrough;
        copy.textShadow = this.textShadow;
        copy.updateInterval = this.updateInterval;
        copy.placeholder = this.placeholder;
        copy.mainColor = this.mainColor;
        copy.firstColor = this.firstColor;
        copy.secondColor = this.secondColor;
        copy.thirdColor = this.thirdColor;
        copy.title = this.title;
        copy.suffix = this.suffix;
        copy.showSuffix = this.showSuffix;
        copy.showEmptyPlaces = this.showEmptyPlaces;
        copy.maxLines = this.maxLines;
        copy.reverseOrder = this.reverseOrder;
        return copy;
    }

    private record PlayerEntry(String playerName, Integer score, String display) {
    }
}
