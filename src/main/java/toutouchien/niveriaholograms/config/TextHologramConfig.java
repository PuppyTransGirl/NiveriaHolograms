package toutouchien.niveriaholograms.config;

import it.unimi.dsi.fastutil.objects.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import toutouchien.niveriaapi.NiveriaAPI;
import toutouchien.niveriaapi.hook.HookManager;
import toutouchien.niveriaapi.hook.HookType;
import toutouchien.niveriaapi.hook.impl.PlaceholderAPIHook;
import toutouchien.niveriaholograms.config.special.DisplayHologramConfig;
import toutouchien.niveriaholograms.utils.HologramUtils;
import toutouchien.niveriaholograms.utils.LegacyToMiniMessage;

import java.util.UUID;

@NullMarked
public class TextHologramConfig extends DisplayHologramConfig {
    private ObjectList<String> text = ObjectLists.synchronize(new ObjectArrayList<>());
    private final Object2ObjectMap<UUID, Component> serializedText = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    @Nullable private TextColor background = HologramUtils.DEFAULT_TEXT_BACKGROUND_COLOR;
    private TextDisplay.TextAlignment textAlignment = TextDisplay.TextAlignment.CENTER;
    private boolean seeThrough = false;
    private boolean textShadow = false;
    private int updateInterval = -1;

    private boolean textDirty, updateIntervalDirty;

    public ObjectList<String> text() {
        return ObjectLists.unmodifiable(text);
    }

    public Component serializedText(Player player) {
        UUID uuid = player.getUniqueId();
        if (serializedText.containsKey(uuid) && updateInterval == 0)
            return serializedText.get(uuid);

        ObjectList<String> textLines = this.text();
        TextComponent.Builder builder = Component.text();

        for (int i = 0; i < textLines.size(); i++) {
            if (i > 0)
                builder.appendNewline();

            String line = textLines.get(i);
            line = applyPapiPlaceholders(player, line);

            // We have to run a conversion because CMI treats modern standards as a suggestion.
            // It insists on burying legacy color codes inside placeholder values—like
            // "§25§7d"—effectively forcing us to play janitor for their 2014-era formatting
            // choices just to get a clean MiniMessage string.
            String mmString = LegacyToMiniMessage.convertPlaceholders(line);
            builder.append(MiniMessage.miniMessage().deserialize(mmString));
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

    @Nullable
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
        return this.textDirty;
    }

    public boolean updateIntervalDirty() {
        return this.updateIntervalDirty;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig text(ObjectList<String> text) {
        this.text = new ObjectArrayList<>(text);
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig background(@Nullable TextColor background) {
        this.background = background;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig textAlignment(TextDisplay.TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig updateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig updateIntervalDirty(boolean updateIntervalDirty) {
        this.updateIntervalDirty = updateIntervalDirty;
        return this;
    }
}
