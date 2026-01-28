package toutouchien.niveriaholograms.configurations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import toutouchien.niveriaapi.NiveriaAPI;
import toutouchien.niveriaapi.hook.HookManager;
import toutouchien.niveriaapi.hook.HookType;
import toutouchien.niveriaapi.hook.impl.PlaceholderAPIHook;
import toutouchien.niveriaapi.utils.ComponentUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TextHologramConfiguration extends HologramConfiguration {
    private List<String> text = new ArrayList<>();
    private final Map<UUID, Component> serializedText = new ConcurrentHashMap<>();
    private TextColor background;
    private TextDisplay.TextAlignment textAlignment = TextDisplay.TextAlignment.CENTER;
    private boolean seeThrough = false;
    private boolean textShadow = false;
    private int updateInterval;
    private boolean textDirty = true;
    private boolean updateIntervalDirty = true;

    public TextHologramConfiguration() {
        // Needed for HologramType
    }

    private TextHologramConfiguration(HologramConfiguration oldConfig) {
        this.scale(oldConfig.scale());
        this.translation(oldConfig.translation());
        this.billboard(oldConfig.billboard());
        this.brightness(oldConfig.brightness());
        this.shadowRadius(oldConfig.shadowRadius());
        this.shadowStrength(oldConfig.shadowStrength());
        this.visibilityDistance(oldConfig.visibilityDistance());
    }

    public List<String> text() {
        return Collections.unmodifiableList(text);
    }

    public Component serializedText(Player player) {
        UUID uuid = player.getUniqueId();
        if (serializedText.containsKey(uuid) && updateInterval == 0)
            return serializedText.get(uuid);

        List<String> textLines = this.text();
        TextComponent.Builder builder = Component.text();

        for (int i = 0; i < textLines.size(); i++) {
            if (i > 0)
                builder.appendNewline();

            String line = textLines.get(i);

            line = applyPapiPlaceholders(player, line);

            builder.append(ComponentUtils.deserializeMM(line));
        }

        TextComponent builtComponent = builder.build();
        this.serializedText.put(uuid, builtComponent);
        return builtComponent;
    }

    private String applyPapiPlaceholders(Player player, String line) {
        HookManager hookManager = NiveriaAPI.instance().hookManager();
        PlaceholderAPIHook hook = hookManager.hook(HookType.PlaceholderAPIHook);
        if (hook != null)
            line = hook.replacePlaceholders(player, line);

        return line;
    }

    public TextColor background() {
        return background;
    }

    public TextDisplay.TextAlignment textAlignment() {
        return textAlignment;
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

    public TextHologramConfiguration text(List<String> text) {
        this.text = new ArrayList<>(text); // Make the text modifiable
        this.serializedText.clear();
        this.textDirty = true;
        return this;
    }

    public TextHologramConfiguration text(int index, String text) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.set(index, text);
        this.serializedText.clear();
        this.textDirty = true;
        return this;
    }

    public TextHologramConfiguration addText(String text) {
        this.text.add(text);
        this.serializedText.clear();
        this.textDirty = true;
        return this;
    }

    public TextHologramConfiguration removeText(int index) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.remove(index);
        this.serializedText.clear();
        this.textDirty = true;
        return this;
    }

    public TextHologramConfiguration addTextAfter(int index, String text) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.add(index + 1, text);
        this.serializedText.clear();
        this.textDirty = true;
        return this;
    }

    public TextHologramConfiguration addTextBefore(int index, String text) {
        if (index < 0 || index >= this.text.size())
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for text list of size " + this.text.size());

        this.text.add(index, text);
        this.serializedText.clear();
        this.textDirty = true;
        return this;
    }

    public TextHologramConfiguration background(TextColor background) {
        this.background = background;
        return this;
    }

    public TextHologramConfiguration textAlignment(TextDisplay.TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public TextHologramConfiguration seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    public TextHologramConfiguration textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public TextHologramConfiguration updateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        this.updateIntervalDirty = true;
        return this;
    }

    public TextHologramConfiguration updateIntervalDirty(boolean updateIntervalDirty) {
        this.updateIntervalDirty = updateIntervalDirty;
        return this;
    }

    public TextHologramConfiguration textDirty(boolean textDirty) {
        this.textDirty = textDirty;
        return this;
    }

    @Override
    public TextHologramConfiguration copy() {
        TextHologramConfiguration copy = new TextHologramConfiguration(super.copy());
        copy.text = new ArrayList<>(this.text);
        copy.background = this.background;
        copy.textAlignment = this.textAlignment;
        copy.seeThrough = this.seeThrough;
        copy.textShadow = this.textShadow;
        copy.updateInterval = this.updateInterval;
        return copy;
    }

    public void clearCache(Player player) {
        this.serializedText.remove(player.getUniqueId());
    }
}
