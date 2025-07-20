package toutouchien.niveriaholograms.configurations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import toutouchien.niveriaapi.NiveriaAPI;
import toutouchien.niveriaapi.hook.HookManager;
import toutouchien.niveriaapi.hook.HookType;
import toutouchien.niveriaapi.hook.impl.PlaceholderAPIHook;
import toutouchien.niveriaapi.utils.ui.ComponentUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderboardHologramConfiguration extends HologramConfiguration {
    private final Map<UUID, Component> serializedText = new ConcurrentHashMap<>();
    private List<String> text = new ArrayList<>();
    private TextColor background;
    private boolean seeThrough = false;
    private boolean textShadow = false;
    private int updateInterval;
    private boolean textDirty = true;
    private boolean updateIntervalDirty = true;

    private String placeholder = "%statistic_player_kills%";
    private TextColor firstColor = TextColor.fromHexString("#FEE101");
    private TextColor secondColor = TextColor.fromHexString("#A7A7AD");
    private TextColor thirdColor = TextColor.fromHexString("#A77044");
    private String title = "Kills Leaderboard";
    private String suffix = "kills";
    private boolean showSuffix = true;
    private boolean showEmptyPlaces = true;
    private int maxLines = 10;

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

    public LeaderboardHologramConfiguration placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public LeaderboardHologramConfiguration firstColor(TextColor firstColor) {
        this.firstColor = firstColor;
        return this;
    }

    public LeaderboardHologramConfiguration secondColor(TextColor secondColor) {
        this.secondColor = secondColor;
        return this;
    }

    public LeaderboardHologramConfiguration thirdColor(TextColor thirdColor) {
        this.thirdColor = thirdColor;
        return this;
    }

    public LeaderboardHologramConfiguration title(String title) {
        this.title = title;
        return this;
    }

    public LeaderboardHologramConfiguration suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public LeaderboardHologramConfiguration showSuffix(boolean showSuffix) {
        this.showSuffix = showSuffix;
        return this;
    }

    public LeaderboardHologramConfiguration showEmptyPlaces(boolean showEmptyPlaces) {
        this.showEmptyPlaces = showEmptyPlaces;
        return this;
    }

    public LeaderboardHologramConfiguration maxLines(int maxLines) {
        this.maxLines = maxLines;
        return this;
    }

    public List<String> text() {
        return Collections.unmodifiableList(text);
    }

    public Component serializedText(Player player) {
        UUID uuid = player.getUniqueId();
        if (serializedText.containsKey(uuid) && updateInterval == 0)
            return serializedText.get(uuid);

        List<String> textLines = this.text;
        TextComponent.Builder builder = Component.text();

        for (int i = 0; i < textLines.size(); i++) {
            if (i > 0)
                builder.appendNewline();

            String line = textLines.get(i);

            HookManager hookManager = NiveriaAPI.instance().hookManager();
            PlaceholderAPIHook hook = hookManager.hook(HookType.PlaceholderAPIHook);
            if (hook != null) {
                line = hook.replacePlaceholders(player, line);
            }

            builder.append(ComponentUtils.deserializeMiniMessage(line));
        }

        this.serializedText.put(uuid, builder.build());
        this.textDirty = false;
        return builder.build();
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

    public TextColor firstColor() {
        return firstColor;
    }

    public TextColor secondColor() {
        return secondColor;
    }

    public TextColor thirdColor() {
        return thirdColor;
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

    @Override
    public LeaderboardHologramConfiguration copy() {
        LeaderboardHologramConfiguration copy = new LeaderboardHologramConfiguration();
        copy.text = new ArrayList<>(this.text);
        copy.background = this.background;
        copy.seeThrough = this.seeThrough;
        copy.textShadow = this.textShadow;
        copy.updateInterval = this.updateInterval;
        copy.placeholder = this.placeholder;
        copy.firstColor = this.firstColor;
        copy.secondColor = this.secondColor;
        copy.thirdColor = this.thirdColor;
        copy.title = this.title;
        copy.suffix = this.suffix;
        copy.showSuffix = this.showSuffix;
        copy.showEmptyPlaces = this.showEmptyPlaces;
        copy.maxLines = this.maxLines;
        return copy;
    }

    public void clearCache(Player player) {
        this.serializedText.remove(player.getUniqueId());
    }
}
